# Guide AdSense: générer ADSENSE_CLIENT, configurer les slots et ads.txt

## Objectif

- Générer un nouvel identifiant éditeur AdSense (ADSENSE_CLIENT, type `ca-pub-xxxxxxxxxxxxxxxx`)
- Créer les unités d’annonce par emplacement et récupérer leurs IDs (`ADSENSE_SLOT_TOP`, `ADSENSE_SLOT_SIDEBAR_LEFT`, `ADSENSE_SLOT_SIDEBAR_RIGHT`, `ADSENSE_SLOT_FOOTER`)
- Mettre à jour le projet (Variables, CI/CD) et publier `ads.txt`

## Prérequis

- Compte Google AdSense actif
- Site ajouté et validé dans AdSense
- Accès au dépôt pour configurer les variables d’environnement

## Générer un nouvel ADSENSE_CLIENT (Publisher ID)

- Se connecter à AdSense → “Sites” → Ajouter votre domaine et suivre la procédure de validation
- Une fois le site approuvé, l’ID éditeur est visible dans les extraits de code AdSense et/ou les paramètres du compte
- Format: `ca-pub-XXXXXXXXXXXXXXX`
- Cet identifiant est public (ce n’est pas un secret)

## Créer les unités d’annonce par emplacement

- Dans AdSense → “Ads” → “By ad unit” → “Display ads” → “Create ad unit”
- Créer 4 unités distinctes et nommées:
  - TOP (bandeau header)
  - SIDEBAR_LEFT (colonne gauche)
  - SIDEBAR_RIGHT (colonne droite)
  - FOOTER (pied de page)
- Choisir “Responsive” pour faciliter l’adaptation (ou tailles fixes si vous maîtrisez le design)
- Cliquer “Get code” et repérer l’attribut `data-ad-slot="XXXXXXXXXX"` dans le snippet: c’est l’ID du slot

## Tailles conseillées (indicatives)

- TOP: 728×90, 970×90 ou responsive; réserver une hauteur d’au moins ~90px
- SIDEBAR_LEFT: 300×600 (ou 300×250); réserver ~600px
- SIDEBAR_RIGHT: 300×250; réserver ~250px
- FOOTER: 728×90 ou responsive; réserver ~280px selon le design

## Configuration dans le projet

- Variables à renseigner:
  - `ADSENSE_ENABLED` (boolean, par défaut `true`)
  - `ADSENSE_CLIENT` (ex: `ca-pub-6181972205565553`)
  - `ADSENSE_SLOT_TOP`, `ADSENSE_SLOT_SIDEBAR_LEFT`, `ADSENSE_SLOT_SIDEBAR_RIGHT`, `ADSENSE_SLOT_FOOTER` (IDs récupérés depuis `data-ad-slot`)
- Exemples de valeurs (fictives) pour `ADSENSE_SLOT_TOP`:
  - `1234567890`
  - `9876501234`
  - `7524986321`
  - Remarque: ce sont des numéros (10+ chiffres) fournis par AdSense dans le code de l’unité d’annonce, via `data-ad-slot="..."`. Ne pas utiliser les placeholders internes `0000000001` du projet en production.
- En build frontend (Vite):
  - Le projet lit ces variables d’environnement et les injecte dans le bundle via `define`
  - Réglage par défaut si absent: `ADSENSE_ENABLED=true` et slots de placeholder

### CI/CD (variables du dépôt)

- Option recommandée: Terraform provider GitHub (idempotent, traçable)
  - Crée les variables Actions: `ADSENSE_ENABLED`, `ADSENSE_CLIENT`, `ADSENSE_SLOT_TOP`, `ADSENSE_SLOT_SIDEBAR_LEFT`, `ADSENSE_SLOT_SIDEBAR_RIGHT`, `ADSENSE_SLOT_FOOTER`
  - Crée `RECAPTCHA_SITE_KEY` (public) et gère `RECAPTCHA_SECRET` (secret) côté CI pour le backend (distinct d’AdSense)
- Option alternative: job bootstrap avec `gh` CLI:
  - `gh variable set ADSENSE_CLIENT --repo <owner/repo> --body "ca-pub-..."` (et idem pour les slots)

### Workflow GitHub Actions

- Le build frontend exporte les variables et Vite les consomme
- Les tests Vitest lisent aussi ces variables pour exécuter des scénarios proches de prod

### Variables existantes (idempotence)

- Si les variables existent déjà dans le dépôt (Settings → Variables), il n’y a rien à “recréer”. Le workflow les lit via `vars.*`.
- Pour les créer/mettre à jour de manière sûre sans erreur de doublon, utiliser `gh` CLI (idempotent):
  - `gh variable set ADSENSE_ENABLED --repo <owner/repo> --body "true"`
  - `gh variable set ADSENSE_CLIENT --repo <owner/repo> --body "ca-pub-8340083616743463"`
  - `gh variable set ADSENSE_SLOT_TOP --repo <owner/repo> --body "1234567890"`
  - (et idem pour `ADSENSE_SLOT_SIDEBAR_LEFT`, `ADSENSE_SLOT_SIDEBAR_RIGHT`, `ADSENSE_SLOT_FOOTER`)
- Avec Terraform, adopter les variables existantes via `terraform import` avant `apply`:
  - `terraform import github_actions_variable.adsense_enabled <owner>/<repo>:ADSENSE_ENABLED`
  - `terraform import github_actions_variable.adsense_client <owner>/<repo>:ADSENSE_CLIENT`
  - `terraform import github_actions_variable.adsense_slot_top <owner>/<repo>:ADSENSE_SLOT_TOP`
  - `terraform import github_actions_variable.adsense_slot_sidebar_left <owner>/<repo>:ADSENSE_SLOT_SIDEBAR_LEFT`
  - `terraform import github_actions_variable.adsense_slot_sidebar_right <owner>/<repo>:ADSENSE_SLOT_SIDEBAR_RIGHT`
  - `terraform import github_actions_variable.adsense_slot_footer <owner>/<repo>:ADSENSE_SLOT_FOOTER`
  - Secrets: `terraform import github_actions_secret.recaptcha_secret <owner>/<repo>:RECAPTCHA_SECRET` (si déjà présent)
  - Après import, `terraform plan`/`apply` mettra à jour au lieu de tenter de recréer.

## Fichier ads.txt

- Objectif: déclarer les vendeurs autorisés de votre inventaire (Google AdSense, etc.)
- Créer le fichier à la racine du domaine: `https://<votre-domaine>/ads.txt`
- Contenu minimal AdSense:
  - `google.com, pub-XXXXXXXXXXXXXXX, DIRECT, f08c47fec0942fa0`
  - Remplacer `pub-XXXXXXXXXXXXXXX` par votre `ADSENSE_CLIENT` sans le préfixe `ca-` (ex: `pub-6181972205565553`)
- Publication dans le projet:
  - Ajouter `src/main/webapp/public/ads.txt` (servi en prod à la racine)
  - Vérifier l’accessibilité après déploiement: `https://<votre-domaine>/ads.txt`

## Conformité et sécurité

- Pas de secret requis pour l’affichage de pubs AdSense côté front
- Respecter le consentement utilisateur (gating) et activer NPA si consentement absent
- S’assurer que la CSP autorise:
  - `https://pagead2.googlesyndication.com` (scripts, images, connect)
  - `https://googleads.g.doubleclick.net`, `https://tpc.googlesyndication.com`, `https://www.google.com`, `https://www.gstatic.com`

## Validation

- En dev: vérifier le rendu des emplacements avec `ADSENSE_ENABLED=true` et slots valides
- En prod: surveiller les no-fill et les performances (CLS faible grâce aux hauteurs réservées)
- Tests:
  - Présence des `<ins.adsbygoogle>` et injection du script head
  - Gating consent/script: aucune annonce sans consentement ou script indisponible

## FAQ

- “Les IDs/Client sont-ils secrets ?” → Non. Ils sont publics et côté front
- “Comment désactiver temporairement ?” → `ADSENSE_ENABLED=false` (via env)
- “Auto Ads vs By ad unit ?” → Ce guide cible “By ad unit” pour contrôler précisément les emplacements
