# Conception Technique Détaillée (CTD) — Désactivation d’Azure Front Door (AFD)

## Résumé

- Objectif: désactiver l’usage d’Azure Front Door dans l’infrastructure et la chaîne GitOps.
- Portée: Terraform + GitHub Actions; aucune modification côté application front/back.
- Contexte actuel: Front Door est géré via Terraform conditionné par `enable_cdn`; la pipeline GitOps enregistre le provider Microsoft.CDN et purge l’endpoint AFD si activé.

## État actuel (analyse rapide)

- Terraform:
  - Variables CDN: voir [variables.tf](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/terraform/variables.tf#L233-L313) (`enable_cdn`, noms profil/endpoint, budget…).
  - Ressources AFD: [cdn.tf](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/terraform/cdn.tf#L1-L97) crée profil, endpoint, origin group, origin et route, tous conditionnés par `count = var.enable_cdn ? 1 : 0`.
  - Injection applicative: [app_service.tf](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/terraform/app_service.tf#L61-L81) ajoute `CDN_BASE_URL` uniquement si `var.enable_cdn` est vrai.
- GitOps:
  - Passage TF_VAR CDN: [gitops.yml](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/.github/workflows/gitops.yml#L55-L63).
  - Enregistrement providers: inclut `Microsoft.CDN` systématiquement [gitops.yml](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/.github/workflows/gitops.yml#L236-L251).
  - Purge AFD post-déploiement quand `ENABLE_CDN == 'true'`: [gitops.yml](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/.github/workflows/gitops.yml#L1039-L1053).

## Solutions de désactivation

### Solution A — Désactivation “forte” par IaC (suppression des ressources)

- Principe: basculer `ENABLE_CDN=false` côté GitHub Actions → `TF_VAR_enable_cdn=false`. Terraform détruit les ressources AFD et le budget. L’application fonctionne directement derrière l’App Service.
- Modifications:
  - GitHub Actions: positionner la variable `ENABLE_CDN` à `false` (ou supprimer la variable pour qu’elle devienne false via normalisation).
  - Terraform: aucune modification de code (déjà piloté par `enable_cdn`). Appliquer `plan/apply`.
  - Option pipeline: garder le provider `Microsoft.CDN` enregistré ou retirer son enregistrement pour accélérer l’init (non bloquant).
- Impacts:
  - Infra: suppression du profil/endpoint/route AFD et du budget de consommation.
  - App Settings: `CDN_BASE_URL` n’est plus injecté; aucun changement requis côté code.
  - Coûts: arrêt de la facturation AFD.
- Rollback:
  - Remettre `ENABLE_CDN=true` puis `terraform apply` pour recréer les ressources et réinjecter `CDN_BASE_URL`.
- Validation:
  - Terraform: `fmt/init/validate/plan/apply`.
  - GitOps: purge AFD sautée automatiquement car `ENABLE_CDN` est false.

### Solution B — Désactivation “souple” sans destruction (endpoint/route désactivés)

- Principe: conserver les objets AFD mais les mettre à l’état désactivé. Utile pour un retour rapide au service ou pour investigation.
- Modifications proposées (IaC):
  - Ajouter une variable `cdn_runtime_enabled` (bool, default `true`) et la propager dans:
    - `azurerm_cdn_frontdoor_endpoint.cdn.enabled = var.cdn_runtime_enabled`
    - `azurerm_cdn_frontdoor_route.route_all.enabled = var.cdn_runtime_enabled`
  - Séparer l’injection applicative avec une variable dédiée `use_cdn_for_assets` (bool) pour décorréler la présence des ressources de l’usage applicatif:
    - Injection `CDN_BASE_URL` si `var.use_cdn_for_assets` est vrai.
  - GitOps:
    - Ne pas purger AFD si `use_cdn_for_assets` est false (condition d’étape basée sur la nouvelle variable Actions).
    - Optionnel: n’enregistrer `Microsoft.CDN` que si `ENABLE_CDN == 'true'` ou si `cdn_runtime_enabled == true`.
- Impacts:
  - Infra: objets AFD existants mais inactifs; trafic non servi par AFD.
  - App: aucun `CDN_BASE_URL` si `use_cdn_for_assets=false`. Retour direct sur App Service.
  - Coûts: frais minimes éventuels liés au profil AFD, sans trafic.
- Rollback:
  - Remettre `cdn_runtime_enabled=true` et `use_cdn_for_assets=true` pour réactiver le trafic et l’injection.
- Validation:
  - Terraform plan/apply OK; vérifier que l’endpoint et la route passent à `enabled=false`.
  - GitOps: étape purge non exécutée si `use_cdn_for_assets=false`.

### Solution C — Bascule opérationnelle immédiate (sans toucher au code IaC)

- Principe: désactiver temporairement l’acheminement AFD au niveau Azure et basculer la consommation applicative hors CDN, tout en conservant l’état Terraform jusqu’à exécution ultérieure.
- Étapes:
  - Azure CLI (opérationnel): rendre la route AFD inactive et/ou désactiver l’endpoint.
  - GitHub Actions:
    - Mettre `ENABLE_CDN=false` pour désactiver la purge et l’injection `CDN_BASE_URL` côté app lors du prochain déploiement.
    - Laisser Terraform inchangé pour le moment (les ressources restent présentes).
- Impacts:
  - Rapide et réversible; écart temporaire entre état réel Azure et IaC jusqu’au prochain `apply`.
  - À synchroniser ensuite via Solution A ou B pour revenir à la conformité IaC.
- Rollback:
  - Réactiver l’endpoint/route via CLI; remettre `ENABLE_CDN=true` si besoin.
- Validation:
  - Vérifier que l’URL de l’application (App Service direct) répond et que l’endpoint AFD ne sert plus le trafic.

## Recommandation

- Choix par défaut: Solution A pour une désactivation nette, sans dette IaC et sans coûts résiduels.
- Choix alternatif: Solution B si vous souhaitez garder la configuration AFD prête au retour de service sans recréation.
- Choix express: Solution C pour un arrêt immédiat suivi d’une régularisation IaC ultérieure.

## Plans et contrôles

- Tests:
  - Santé applicative: endpoint `/management/health` après déploiement (see [gitops.yml](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/.github/workflows/gitops.yml#L1053-L1086)).
  - Purge: étape sautée si `ENABLE_CDN=false` (see [gitops.yml](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/.github/workflows/gitops.yml#L1039-L1053)).
- Observabilité:
  - Aucun purging AFD en mode désactivé; logs applicatifs collectés en cas d’échec.
- Sécurité:
  - HTTPS côté App Service conservé; aucun secret modifié.

## Annexes — Références

- Terraform:
  - Variables CDN: [variables.tf](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/terraform/variables.tf#L233-L313)
  - Ressources AFD: [cdn.tf](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/terraform/cdn.tf#L1-L97)
  - Injection CDN_BASE_URL: [app_service.tf](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/terraform/app_service.tf#L61-L81)
- GitOps:
  - TF_VAR enable_cdn & budget: [gitops.yml](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/.github/workflows/gitops.yml#L55-L63)
  - Provider Microsoft.CDN: [gitops.yml](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/.github/workflows/gitops.yml#L236-L251)
  - Purge AFD conditionnelle: [gitops.yml](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/.github/workflows/gitops.yml#L1039-L1053)
