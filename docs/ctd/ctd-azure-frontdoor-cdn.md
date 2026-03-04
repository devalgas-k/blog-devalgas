# Conception Technique Détaillée (CTD) — Migration CDN vers Azure Front Door Standard et option Cloudflare

## Résumé

- Contexte: des essais avec Azure CDN classiques (Microsoft/Akamai/Verizon) renvoient des erreurs de création (SKUs retirés). Remplacer par Azure Front Door Standard, pérenne et supporté par Microsoft.
- Objectif: servir le site via un endpoint edge Front Door avec cache/compression, purge post-déploiement et budget de consommation. Prévoir une section optionnelle pour configurer Cloudflare si souhaité (cdn.devalgas.net).
- Portée: Terraform + GitOps; aucune modification du code applicatif. Injection de CDN_BASE_URL côté App Service pour faciliter la bascule des assets.

## Choix techniques

- Front Door Standard (SKU Standard_AzureFrontDoor) avec:
  - Endpoint unique
  - Origin group vers App Service (azurewebsites.net)
  - Route “/\*” (HTTPS only + redirect, cache UseQueryString, compression sur types sûrs)
- Budgets: consommation au niveau subscription, seuils 50/80/100% avec email
- Purge: az afd endpoint purge sur les entrées non versionnées
- Option: custom domain cdn.devalgas.net (Azure Front Door) ou configuration Cloudflare dédiée

## Variables Terraform

```hcl
variable "enable_cdn" { type = bool, default = false }
variable "cdn_profile_name" { type = string, default = "cdn-devalgas" }
variable "cdn_endpoint_name" { type = string, default = "cdn-devalgas-endpoint" }
variable "cdn_origin_hostname" { type = string, default = "app-springboot-blog-devalgas.azurewebsites.net" }
variable "cdn_querystring_behavior" { type = string, default = "UseQueryString" }
variable "cdn_monthly_budget_eur" { type = number, default = 5 }
variable "cdn_budget_contact_email" { type = string, default = "" }
variable "cdn_budget_start_date" { type = string, default = "2026-01-01T00:00:00Z" }
```

## Ressources Terraform (Front Door)

- Profil/endpoint Front Door:

```hcl
resource "azurerm_cdn_frontdoor_profile" "cdn" {
  count               = var.enable_cdn ? 1 : 0
  name                = var.cdn_profile_name
  resource_group_name = azurerm_resource_group.rg.name
  sku_name            = "Standard_AzureFrontDoor"
}

resource "azurerm_cdn_frontdoor_endpoint" "cdn" {
  count                    = var.enable_cdn ? 1 : 0
  name                     = var.cdn_endpoint_name
  cdn_frontdoor_profile_id = azurerm_cdn_frontdoor_profile.cdn[0].id
  enabled                  = true
}
```

- Origin group + origin App Service:

```hcl
resource "azurerm_cdn_frontdoor_origin_group" "app" {
  count                    = var.enable_cdn ? 1 : 0
  name                     = "origin-group-app"
  cdn_frontdoor_profile_id = azurerm_cdn_frontdoor_profile.cdn[0].id
  load_balancing { additional_latency_in_milliseconds = 0 sample_size = 4 successful_samples_required = 3 }
  health_probe { path = "/" protocol = "Https" request_type = "HEAD" interval_in_seconds = 120 }
}

resource "azurerm_cdn_frontdoor_origin" "app" {
  count                          = var.enable_cdn ? 1 : 0
  name                           = "origin-app"
  cdn_frontdoor_origin_group_id  = azurerm_cdn_frontdoor_origin_group.app[0].id
  enabled                        = true
  certificate_name_check_enabled = false
  host_name                      = var.cdn_origin_hostname
  origin_host_header             = var.cdn_origin_hostname
  http_port                      = 80
  https_port                     = 443
  priority                       = 1
  weight                         = 1000
}
```

- Route “/\*” (cache/compression):

```hcl
resource "azurerm_cdn_frontdoor_route" "route_all" {
  count                         = var.enable_cdn ? 1 : 0
  name                          = "route-all"
  cdn_frontdoor_endpoint_id     = azurerm_cdn_frontdoor_endpoint.cdn[0].id
  cdn_frontdoor_origin_group_id = azurerm_cdn_frontdoor_origin_group.app[0].id
  cdn_frontdoor_origin_ids      = [azurerm_cdn_frontdoor_origin.app[0].id]
  enabled                       = true
  forwarding_protocol           = "HttpsOnly"
  https_redirect_enabled        = true
  patterns_to_match             = ["/*"]
  supported_protocols           = ["Http", "Https"]
  link_to_default_domain        = true
  cache {
    query_string_caching_behavior = var.cdn_querystring_behavior == "UseQueryString" ? "UseQueryString" : "IgnoreQueryString"
    compression_enabled           = true
    content_types_to_compress     = ["text/plain","text/css","application/javascript","application/json","image/svg+xml","text/html"]
  }
}
```

- Budget consommation:

```hcl
resource "azurerm_consumption_budget_subscription" "cdn_monthly" {
  count           = var.enable_cdn ? 1 : 0
  name            = "budget-cdn"
  subscription_id = "/subscriptions/${data.azurerm_client_config.current.subscription_id}"
  amount          = var.cdn_monthly_budget_eur
  time_grain      = "Monthly"
  time_period { start_date = var.cdn_budget_start_date end_date = "2030-12-31T00:00:00Z" }
  notification { enabled = true operator = "GreaterThan" threshold = 50 threshold_type = "Actual" contact_emails = var.cdn_budget_contact_email != "" ? [var.cdn_budget_contact_email] : [var.alert_email] }
  notification { enabled = true operator = "GreaterThan" threshold = 80 threshold_type = "Actual" contact_emails = var.cdn_budget_contact_email != "" ? [var.cdn_budget_contact_email] : [var.alert_email] }
  notification { enabled = true operator = "GreaterThanOrEqualTo" threshold = 100 threshold_type = "Actual" contact_emails = var.cdn_budget_contact_email != "" ? [var.cdn_budget_contact_email] : [var.alert_email] }
}
```

## App Settings (bascule)

```hcl
var.enable_cdn ? { "CDN_BASE_URL" = "https://${azurerm_cdn_frontdoor_endpoint.cdn[0].host_name}" } : {}
```

## GitOps

- Variables Actions:
  - ENABLE_CDN (true/false)
  - CDN_PROFILE_NAME, CDN_ENDPOINT_NAME, CDN_ORIGIN_HOSTNAME, CDN_QUERYSTRING_BEHAVIOR
  - CDN_MONTHLY_BUDGET_EUR, CDN_BUDGET_CONTACT_EMAIL, CDN_BUDGET_START_DATE
- Normalisation:
  - TF_VAR_enable_cdn (true/false)
  - TF_VAR_cdn_budget_start_date → RFC3339 si fourni au format YYYY-MM-DD
- Providers:
  - Microsoft.CDN (enregistrement)
- Purge:

```bash
az afd endpoint purge -g "$RG" --profile-name "$PROFILE" --endpoint-name "$ENDPOINT" --content-paths "/index.html" "/manifest.json" "/service-worker.js"
```

- Import d’un budget existant:

```bash
terraform import "azurerm_consumption_budget_subscription.cdn_monthly[0]" "/subscriptions/${SUBSCRIPTION_ID}/providers/Microsoft.Consumption/budgets/budget-cdn"
```

- Nettoyage CDN classique:
  - suppression endpoints/profils via az cdn … si présents

## Impacts sécurité, performance, observabilité

- Sécurité: HTTPS partout; pas de secrets exposés; CORS minimal
- Performance: latence réduite; cache; compression; respecter Cache-Control de l’origine
- Observabilité: budget + alertes; logs de purge; métriques Front Door (cache hit ratio, 4xx/5xx)

## Plan de migration

- Activer enable_cdn=true; déployer profil/endpoint/route
- Vérifier accessibilité de l’endpoint azurefd.net
- Injecter CDN_BASE_URL et basculer progressivement les assets si nécessaire
- Purge ciblée post-déploiement des entrées non versionnées

## Plan de rollback

- enable_cdn=false; appliquer Terraform
- Supprimer route/endpoint/profil Front Door
- Retirer CDN_BASE_URL si présent

## Plan de tests et validation

- Terraform: fmt/init/validate/apply OK
- Connectivité: endpoint HTTPS accessible
- Cache/compression: vérifier headers et cache hit (seconde requête)
- Coûts: budget actif et alertes reçues

## Annexes — Estimations de coût

- Hypothèses: Zone 1 (EU/NA), 50–100 GB/mois, compression active, assets versionnés
- Ordres de grandeur d’egress CDN: 50 GB ≈ 3,5–4,5 €, 100 GB ≈ 7–9 € (indicatif). Les coûts exacts dépendent de la zone et des tarifs en vigueur.

---

## Option: Configuration Cloudflare (cdn.devalgas.net)

### Objectifs

- Fournir un sous‑domaine CDN (cdn.devalgas.net) pour les assets, piloté par Cloudflare, en complément de Front Door (HTML/API).
- Réduire la charge sur l’App Service; appliquer des règles de cache et de purge spécifiques aux assets.

### DNS et domaine

- Ajouter cdn.devalgas.net en CNAME vers:
  - Option A: l’endpoint Azure Front Door (recommandé pour rester “tout Azure” en façade)
  - Option B: l’origine directe (app-springboot-blog-devalgas.azurewebsites.net) si vous mettez Cloudflare devant l’origine
- Activer le proxy Cloudflare (nuage orange) pour cdn.devalgas.net

### Paramètres Cloudflare (compte/domaine)

- SSL/TLS:
  - Mode “Full” (si origine en HTTPS)
  - Minimum TLS 1.2
  - Activer HTTP/2, HTTP/3, 0‑RTT si disponible
- Performance:
  - Brotli: activé
  - Auto Minify (JS/CSS/HTML): activé si compatible avec votre build
- Cache:
  - Caching level: Standard
  - Edge Cache TTL: respect de Cache-Control de l’origine; sinon définir TTL long pour assets versionnés
  - Bypass cache pour HTML (index.html, manifest.json, service-worker.js)

### Rules (Page Rules ou Rulesets)

- “Cache Everything” pour les assets versionnés (ex: /assets/\*) avec TTL long (immutable)
- “Bypass Cache” pour index.html, manifest.json, service-worker.js
- Redirection HTTPS si nécessaire

### Purge

- Purge ciblée des entrées non versionnées après déploiement (API Cloudflare):
  - Purger “/index.html”, “/manifest.json”, “/service-worker.js”
  - Laisser en cache les assets versionnés par hash

### Terraform (optionnel)

- Provider Cloudflare:
  - Gestion DNS (CNAME cdn.devalgas.net)
  - Rulesets de cache et purge
  - Attention: nécessite jeton API Cloudflare avec permissions adéquates

### Validation

- Vérifier cdn.devalgas.net en HTTPS (certificat Cloudflare)
- Observer les headers (CF-Cache-Status), cache hit/miss
- Tester purge via API et revalidation

### Choix d’architecture

- Front Door pour HTML/API; Cloudflare pour assets:
  - Avantage: flexibilité maximale et coûts maîtrisés
  - Inconvénient: complexité multi‑CDN et gouvernance de deux fournisseurs
