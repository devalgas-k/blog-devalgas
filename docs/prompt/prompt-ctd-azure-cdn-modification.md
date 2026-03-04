Titre: Mise à jour / modification de CTD — Ajout d’Azure CDN pour accélération du site

Rôle: Tu es un architecte logiciel senior. Tu dois produire en français une mise à jour maîtrisée d’une CTD d’infrastructure existante pour ajouter un service Azure CDN, avec traçabilité, compatibilité et validation de non‑régression.

Objectif: Cartographier l’infra actuelle (Terraform + GitOps), définir et documenter l’ajout d’un CDN Azure à faible coût, prévoir un budget/limite et fournir les blocs de configuration exacts ainsi que le plan de tests et de rollback.

Entrées (à fournir):

- CTD source et version: Infra Azure v1 (App Service Linux B1, PostgreSQL Flexible, Key Vault optionnel, Monitor/Alerts), pilotée par Terraform et GitHub Actions GitOps
- Motif du changement: Accélérer le chargement du site par mise en cache et distribution géographique des assets/HTML via Azure CDN
- Périmètre et contraintes: Intégration IaC (Terraform), pipeline GitOps, budget mensuel bas, aucun secret en clair, HTTPS only
- Volumétrie/SLA impactés: Trafic statique et partiellement dynamique; cible egress CDN très faible (≈ 50–100 GB/mois)
- Risques et dépendances: Enregistrement provider Microsoft.CDN, CORS/Cache-Control, choix de l’origine (App Service vs Storage), custom domain optionnel

Livrables attendus:

- CTD mise à jour décrivant l’ajout d’Azure CDN (profil + endpoint)
- Changelog CTD (avant → après)
- Spécifications détaillées (Terraform + GitOps) incluant variables et ressources
- Liste des fichiers à modifier avec chemins absolus
- Patchs de code proposés si applicables
- Plan de tests et validation locale

Contraintes et contexte:

- Respect des conventions du dépôt et du provisionnement existant
- Aucune fuite de secrets; variables Terraform et Key Vault
- Compatibilité et non‑régression garanties, bascule progressive possible

Structure et étapes de mise à jour:

1. Contexte et périmètre
   - CTD actuelle vs comportement attendu; non‑objectifs
2. Synthèse de la CTD existante
   - Architecture, ressources, pipeline GitOps
3. Objectifs de modification et critères de non‑régression
   - Invariants, contrats, cas limites
4. Décisions techniques et architecture cible
   - Changements proposés et justification
5. Spécifications détaillées mises à jour
   - Variables et ressources Terraform, snippets GitOps
   - Blocs de configuration exacts (sans commentaires)
6. Impacts sécurité, performance et observabilité
   - HTTPS, CORS, cache, métriques/alertes
7. Plan de migration et rollback
   - Étapes, compatibilités, bascule et retour arrière
8. Plan de tests et validation
   - Tests de déploiement, de cache, de coût; validation locale
9. Changelog CTD
   - Liste des changements, rationale, compatibilité
10. Annexes

1) Contexte et périmètre

- Infra actuelle sans CDN: App Service Linux B1, PostgreSQL Flexible, Key Vault optionnel, Azure Monitor (alerts), provisionnés via Terraform; pipeline GitOps exécute init/plan/apply et enregistre certains providers.
- Attendu: Ajouter Azure CDN Standard from Microsoft pour servir l’origine applicative et distribuer le contenu au plus près des utilisateurs. Activer un budget mensuel bas et des alertes.
- Non‑objectifs: Refactor applicatif majeur, adoption Front Door Premium, refonte DNS complète; domaine CDN personnalisé optionnel.

2. Synthèse de la CTD existante

- Terraform:
  - Ressources: groupe de ressources, App Service Plan, Linux Web App, PostgreSQL Flexible Server, Key Vault (optionnel), Monitor alerts, Defender pricing.
  - Variables: noms, location, secrets, mail, flags Key Vault et Defender.
  - Absence de profil/endpoint CDN.
- GitOps:
  - Providers enregistrés: Microsoft.KeyVault, Microsoft.Web, Microsoft.DBforPostgreSQL.
  - Passage des TF_VAR via env, Azure login OIDC/SP, fmt/init/plan/apply conditionnels.

3. Objectifs de modification et critères de non‑régression

- Objectifs:
  - Créer un profil CDN Azure (SKU Standard_Microsoft) et un endpoint pointant vers l’origine applicative.
  - Exposer une variable d’activation (enable_cdn) et un budget mensuel bas avec alertes.
  - Intégrer l’enregistrement du provider Microsoft.CDN dans GitOps.
- Critères:
  - Déploiement idempotent, aucun impact si enable_cdn=false.
  - HTTPS uniquement; pas de fuite de secrets.
  - Maintien des endpoints existants; bascule contrôlée.

4. Décisions techniques et architecture cible

- Choix: Azure CDN Standard from Microsoft pour minimiser coûts fixes; origine par défaut = App Service public (azurewebsites.net).
- Option: plus tard, déplacer l’origine vers un Storage public dédié pour assets statiques si besoin.
- Budget: Utiliser un budget de consommation mensuel au niveau subscription avec notifications par email; seuils 50/80/100%.

5. Spécifications détaillées mises à jour

Terraform — variables à ajouter:

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

Terraform — ressources à ajouter:

```hcl
resource "azurerm_cdn_profile" "cdn" {
  count               = var.enable_cdn ? 1 : 0
  name                = var.cdn_profile_name
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_resource_group.rg.location
  sku                 = "Standard_Microsoft"
}

resource "azurerm_cdn_endpoint" "cdn" {
  count                       = var.enable_cdn ? 1 : 0
  name                        = var.cdn_endpoint_name
  profile_name                = azurerm_cdn_profile.cdn[0].name
  resource_group_name         = azurerm_resource_group.rg.name
  location                    = azurerm_resource_group.rg.location
  is_http_allowed             = false
  is_https_allowed            = true
  is_compression_enabled      = true
  content_types_to_compress   = ["text/plain","text/css","application/javascript","application/json","image/svg+xml","font/woff2"]
  querystring_caching_behaviour = var.cdn_querystring_behavior
  origin_host_header          = var.cdn_origin_hostname
  origin_path                 = ""
  origin {
    name       = "origin-app"
    host_name  = var.cdn_origin_hostname
    https_port = 443
  }
}
```

Terraform — budget mensuel:

```hcl
resource "azurerm_consumption_budget_subscription" "cdn_budget" {
  count          = var.enable_cdn ? 1 : 0
  name           = "budget-cdn"
  amount         = var.cdn_monthly_budget_eur
  time_grain     = "Monthly"
  subscription_id = data.azurerm_client_config.current.subscription_id
  time_period {
    start_date = var.cdn_budget_start_date
  }
  notification {
    enabled        = true
    operator       = "GreaterThan"
    threshold      = 50
    contact_emails = [var.cdn_budget_contact_email != "" ? var.cdn_budget_contact_email : var.alert_email]
  }
  notification {
    enabled        = true
    operator       = "GreaterThan"
    threshold      = 80
    contact_emails = [var.cdn_budget_contact_email != "" ? var.cdn_budget_contact_email : var.alert_email]
  }
  notification {
    enabled        = true
    operator       = "GreaterThan"
    threshold      = 100
    contact_emails = [var.cdn_budget_contact_email != "" ? var.cdn_budget_contact_email : var.alert_email]
  }
}
```

GitHub Actions — GitOps (ajouts):

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

```yaml
- name: Register required Azure Resource Providers (CDN)
  uses: azure/cli@v1
  with:
    azcliversion: latest
    inlineScript: |
      set -e
      az provider register --namespace "Microsoft.CDN" || true
```

Trio de cohérence du cache (déploiements):

- Assets versionnés par hash avec Cache-Control long et immutable.
- TTL courte sur les entrées non versionnées (index.html, manifest.json, service-worker.js).
- Purge ciblée post-déploiement uniquement de ces entrées non versionnées.

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

Option App Settings (exposition côté App Service si utile):

```hcl
"CDN_BASE_URL" = "https://${var.cdn_endpoint_name}.azureedge.net"
```

Liste des fichiers à modifier/créer:

- /Users/devalgas/Documents/projets/perso/blog-devalgas/terraform/variables.tf
- /Users/devalgas/Documents/projets/perso/blog-devalgas/terraform/cdn.tf (nouveau)
- /Users/devalgas/Documents/projets/perso/blog-devalgas/terraform/app_service.tf
- /Users/devalgas/Documents/projets/perso/blog-devalgas/.github/workflows/gitops.yml

6. Impacts sécurité, performance et observabilité

- Sécurité: HTTPS uniquement sur CDN; pas de secrets; CORS minimal si nécessaire.
- Performance: Latence réduite; cache sur types compressibles; conserver Cache‑Control côté origin.
- Observabilité: Ajouter budget CDN et alertes; envisager alertes métriques sur cache hit ratio/4xx/5xx endpoint CDN.

7. Plan de migration et rollback

- Migration: déployer profil+endpoint avec enable_cdn=true; vérifier accessibilité; activer l’usage côté front via CDN_BASE_URL si requis.
- Rollback: remettre enable_cdn=false, supprimer endpoint et profil; retirer App Settings éventuels.

8. Plan de tests et validation

- Terraform: init/plan/apply réussis avec provider Microsoft.CDN enregistré.
- Connectivité: résolutions DNS azureedge.net et chargement HTTPS.
- Cache: vérifier headers et compression, cache hit après seconde requête.
- Coûts: vérifier création du budget, réception des alertes sur seuils simulés.

9. Changelog CTD

- Ajout: variables enable_cdn et paramètres CDN.
- Ajout: ressources azurerm_cdn_profile, azurerm_cdn_endpoint, azurerm_consumption_budget_subscription.
- GitOps: enregistrement provider Microsoft.CDN et passage des TF_VAR.

10. Annexes — Estimation de coût à très faible trafic et limite

- Hypothèses: CDN Standard_Microsoft, Zone 1 (Amérique du Nord/Europe), trafic 50–100 GB/mois, compression active, assets versionnés.
- Estimation: 50 GB/mois ≈ 3,5–4,5 €/mois; 100 GB/mois ≈ 7–9 €/mois (ordre de grandeur). Pas de coût fixe significatif sur le profil Standard Microsoft.
- Limite: Budget mensuel fixé à 5 € par défaut avec seuils 50/80/100% et notifications email. Ajuster TF_VAR_cdn_monthly_budget_eur selon besoin.
