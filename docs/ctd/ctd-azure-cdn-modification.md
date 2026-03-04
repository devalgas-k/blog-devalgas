# Conception Technique Détaillée (CTD) — Mise à jour: Ajout d’Azure CDN pour accélération du site

## Résumé

- Contexte: application Spring Boot + App Service Linux B1, PostgreSQL Flexible, Key Vault (optionnel), Azure Monitor. Pas de CDN actuellement.
- Objectif: ajouter Azure CDN (Standard_Microsoft) pour distribuer les contenus au plus près des utilisateurs, avec budget mensuel faible et limites configurables.
- Portée: Terraform + GitOps; aucun code applicatif modifié, option App Settings pour exposer CDN_BASE_URL; stratégie de cohérence du cache (trio).
- Non-objectifs: migration complète vers Front Door, refonte DNS complète, refactor majeur du front.

## Architecture & choix techniques

- Profil: Azure CDN Standard from Microsoft (coût fixe minimal, egress facturé au volume).
- Origine: par défaut App Service public (azurewebsites.net). Option ultérieure Storage public pour assets statiques (voir [cdt-storage-cdn.md](../cdt-storage-cdn.md)).
- Cache: compression active, UseQueryString, trio de cohérence du cache:
  - Assets versionnés par hash, Cache-Control long et immutable.
  - TTL courte sur les entrées non versionnées (index.html, manifest.json, service-worker.js).
  - Purge ciblée post-déploiement uniquement de ces entrées non versionnées.
- Budget: budget mensuel au niveau subscription avec seuils 50/80/100% et notification email.

## Synthèse de l’existant

- Terraform: [main.tf](../../terraform/main.tf), [app_service.tf](../../terraform/app_service.tf), [database.tf](../../terraform/database.tf), [keyvault.tf](../../terraform/keyvault.tf), [monitor.tf](../../terraform/monitor.tf), [defender_pricing.tf](../../terraform/defender_pricing.tf), [variables.tf](../../terraform/variables.tf). Pas de ressources CDN.
- GitOps: [.github/workflows/gitops.yml](../../.github/workflows/gitops.yml) enregistre certains providers et pilote Terraform; aucune étape CDN/purge actuellement.

## Objectifs et critères de non-régression

- Idempotence IaC: activation via enable_cdn; pas d’effet si false.
- Sécurité: HTTPS seulement; aucune fuite de secrets; CORS minimal si nécessaire.
- Compatibilité: pas de rupture côté front; bascule progressive via variable CDN_BASE_URL; aucun changement d’API.
- Observabilité/coûts: création d’un budget CDN avec alertes; monitoring cache hit ratio et codes 4xx/5xx envisagés.

## Spécifications détaillées (Terraform + GitOps)

### Variables Terraform

```hcl
variable "enable_cdn" { type = bool, default = false }
variable "cdn_profile_name" { type = string, default = "cdn-devalgas" }
variable "cdn_endpoint_name" { type = string, default = "cdn-devalgas-endpoint" }
variable "cdn_origin_hostname" { type = string, default = "app-springboot-blog-devalgas.azurewebsites.net" }
variable "cdn_querystring_behavior" { type = string, default = "UseQueryString" }
variable "cdn_monthly_budget_eur" { type = number, default = 5 }
variable "cdn_budget_contact_email" { type = string, default = "" }
variable "cdn_budget_start_date" { type = string, default = "2026-01-01" }
```

### Ressources Terraform CDN

```hcl
resource "azurerm_cdn_profile" "cdn" {
  count               = var.enable_cdn ? 1 : 0
  name                = var.cdn_profile_name
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_resource_group.rg.location
  sku                 = "Standard_Microsoft"
}

resource "azurerm_cdn_endpoint" "cdn" {
  count                         = var.enable_cdn ? 1 : 0
  name                          = var.cdn_endpoint_name
  profile_name                  = azurerm_cdn_profile.cdn[0].name
  resource_group_name           = azurerm_resource_group.rg.name
  location                      = azurerm_resource_group.rg.location
  is_http_allowed               = false
  is_https_allowed              = true
  is_compression_enabled        = true
  content_types_to_compress     = ["text/plain","text/css","application/javascript","application/json","image/svg+xml","font/woff2"]
  querystring_caching_behaviour = var.cdn_querystring_behavior
  origin_host_header            = var.cdn_origin_hostname
  origin_path                   = ""
  origin {
    name       = "origin-app"
    host_name  = var.cdn_origin_hostname
    https_port = 443
  }
}
```

### Budget Azure (consommation)

```hcl
resource "azurerm_consumption_budget_subscription" "cdn_budget" {
  count           = var.enable_cdn ? 1 : 0
  name            = "budget-cdn"
  amount          = var.cdn_monthly_budget_eur
  time_grain      = "Monthly"
  subscription_id = data.azurerm_client_config.current.subscription_id
  time_period { start_date = var.cdn_budget_start_date }
  notification { enabled = true operator = "GreaterThan" threshold = 50 contact_emails = [var.cdn_budget_contact_email != "" ? var.cdn_budget_contact_email : var.alert_email] }
  notification { enabled = true operator = "GreaterThan" threshold = 80 contact_emails = [var.cdn_budget_contact_email != "" ? var.cdn_budget_contact_email : var.alert_email] }
  notification { enabled = true operator = "GreaterThan" threshold = 100 contact_emails = [var.cdn_budget_contact_email != "" ? var.cdn_budget_contact_email : var.alert_email] }
}
```

### App Settings (optionnel)

```hcl
"CDN_BASE_URL" = "https://${var.cdn_endpoint_name}.azureedge.net"
```

### GitOps (env TF_VAR)

```yaml
env:
  TF_VAR_enable_cdn: ${{ vars.ENABLE_CDN }}
  TF_VAR_cdn_profile_name: cdn-devalgas
  TF_VAR_cdn_endpoint_name: cdn-devalgas-endpoint
  TF_VAR_cdn_origin_hostname: ${{ env.APP_NAME }}.azurewebsites.net
  TF_VAR_cdn_querystring_behavior: UseQueryString
  TF_VAR_cdn_monthly_budget_eur: 5
  TF_VAR_cdn_budget_contact_email: ${{ env.ADMIN_UPN }}
  TF_VAR_cdn_budget_start_date: '2026-01-01'
```

### GitOps (provider Microsoft.CDN)

```yaml
- name: Register required Azure Resource Providers (CDN)
  uses: azure/cli@v1
  with:
    azcliversion: latest
    inlineScript: |
      set -e
      az provider register --namespace "Microsoft.CDN" || true
```

### GitOps (purge ciblée post-déploiement)

```yaml
- name: Purge CDN (HTML & manifest)
  if: ${{ env.TF_VAR_enable_cdn == 'true' }}
  uses: azure/cli@v1
  with:
    azcliversion: latest
    inlineScript: |
      az cdn endpoint purge \
        -g "${RESOURCE_GROUP}" \
        --profile-name "cdn-devalgas" \
        -n "cdn-devalgas-endpoint" \
        --content-paths "/index.html" "/manifest.json" "/service-worker.js"
```

## Impacts sécurité, performance, observabilité

- Sécurité: HTTPS uniquement sur CDN; pas de secrets exposés; CORS minimal selon besoins.
- Performance: réduction de la latence; compression et caching sur les types statiques; conserver Cache-Control côté origin.
- Observabilité: budget CDN et alertes; envisager métriques cache hit ratio et surveillance 4xx/5xx sur l’endpoint CDN.

## Plan de migration

- Activer enable_cdn=true et enregistrer provider Microsoft.CDN.
- Déployer profil et endpoint CDN via Terraform.
- Option: injecter CDN_BASE_URL côté App Service et basculer progressivement le front.
- Purger uniquement les entrées non versionnées post-déploiement; laisser les assets versionnés sans purge.

## Plan de rollback

- Désactiver enable_cdn et appliquer Terraform; supprimer endpoint et profil.
- Retirer CDN_BASE_URL si présent; revenir au chemin d’actifs non-CDN.

## Plan de tests et validation locale

- Terraform: fmt, init, plan, validate; apply avec provider Microsoft.CDN enregistré.
- Connectivité: tester HTTPS sur l’endpoint azureedge.net; vérifier résolution DNS.
- Cache: vérifier headers Cache-Control, compression, et cache hit après seconde requête.
- Coûts: vérifier création du budget et réception des alertes en simulant seuils.

## Liste des fichiers à modifier

- ../../terraform/variables.tf
- ../../terraform/cdn.tf
- ../../terraform/app_service.tf
- ../../.github/workflows/gitops.yml

## Changelog CTD

- Ajout variables enable_cdn et paramètres CDN.
- Ajout ressources azurerm_cdn_profile, azurerm_cdn_endpoint, azurerm_consumption_budget_subscription.
- Ajout registres provider Microsoft.CDN et TF_VAR côté GitOps; purge ciblée post-déploiement.
- Option: ajout App Setting CDN_BASE_URL pour bascule front.

## Annexes — Estimation de coût et limite

- Hypothèses: Zone 1 (Amérique du Nord/Europe), trafic 50–100 GB/mois, compression active, assets versionnés.
- Ordres de grandeur:
  - 50 GB/mois ≈ 3,5–4,5 €/mois
  - 100 GB/mois ≈ 7–9 €/mois
- Limite: budget mensuel par défaut à 5 € avec seuils 50/80/100% et notifications email. Ajustable via TF_VAR_cdn_monthly_budget_eur.
