Titre: Optimiser les coûts Microsoft Defender for Cloud et renforcer la sécurité (Cloudflare + rate limiting Spring)

Rôle: Tu es un ingénieur cloud/sécurité senior. Tu dois produire une stratégie et des changements concrets pour limiter les coûts de Microsoft Defender for Cloud tout en renforçant la sécurité applicative via Cloudflare (WAF/Firewall/Rate Limiting/Access), restrictions d’accès sur Azure App Service, et rate limiting côté backend Spring (Bucket4j), avec validation et observabilité.

Objectifs:

- Réduire/maîtriser le coût Defender en activant uniquement les plans nécessaires (prod) et en laissant “Free” ou désactivé ailleurs
- Protéger l’origine via Cloudflare (WAF, bot, DDoS, rate limiting, IP de proxy)
- Implémenter un rate limiting serveur sur les endpoints sensibles et un verrouillage anti brute-force
- Documenter les vérifications et fournir une CTD + patchs prêts à appliquer

Entrées à fournir:

- Liste des environnements (dev/stage/prod) et exigences sécurité/cout par environnement
- Ressources Azure utilisées (App Services, Bases OSS, Key Vaults) et leur volumétrie
- Zone Cloudflare (domaine) et contraintes (pays/ASN à bloquer, chemins sensibles)
- Endpoints backend à protéger en priorité (/api/**, /authenticate, /account/**)

Livrables attendus:

- CTD “Sécurité & coût Defender” dans docs/ctd (structure claire, sans commentaires dans le code)
- tfvars prod/non‑prod pour Terraform, avec tiers Defender par type (AppServices/OSS DB/KeyVaults)
- Patchs code prêts: filtre Bucket4j (429), verrouillage login, configuration App Service access restrictions (IP Cloudflare)
- Guide Cloudflare: règles WAF/Firewall/Rate Limiting/Turnstile
- Plan de tests (manuels + unitaires si pertinent) et checklist de validation (Azure Portal + Cloudflare Analytics)

Contraintes et contexte:

- AzureRM 3.x, Terraform déjà présent (defender_pricing.tf)
- Spring Boot 3.5.x (JHipster), endpoints REST /api/\*\*
- Cloudflare gère la zone publique; l’origine est Azure App Service Linux
- Ne jamais exposer de secrets côté front; limiter au strict nécessaire côté backend/CI

Stratégie et étapes:

1. Analyse de l’inventaire et des plans actifs

- Recenser App Services, Bases OSS, Key Vaults
- Vérifier les plans Defender actuels dans Azure Portal (Environment settings → Plans)

2. Politique de coûts par environnement

- non‑prod: enable_defender_pricing=false ou tiers “Free” partout
- prod: Standard uniquement pour App Services; OSS DB et Key Vaults en “Free” tant que le risque/volume ne l’exige pas
- Préparer terraform.tfvars.prod et terraform.tfvars.nonprod avec ces choix
- Idempotence: adopter les variables GH existantes via terraform import avant apply

3. Terraform

- Mettre à jour defender_pricing.tf si nécessaire (ajouts de types futurs uniquement si ressources associées)
- Créer tfvars et documenter la commande plan/apply + vérifications

4. Azure App Service access restrictions

- Autoriser uniquement les IP ranges Cloudflare vers l’origine (deny all par défaut)
- Option: mTLS “Authenticated Origin Pulls” si compatible
- Vérifier le health path après restrictions

5. Cloudflare

- WAF Managed Rules + Bot Fight Mode + DDoS toujours activés
- Firewall Rules: blocage pays/ASN indésirables, chemins sensibles
- Rate Limiting Rules: /api/\*\* → limite adaptée (ex: 100 req/min/IP, action Challenge ou Block)
- Turnstile pour formulaires (login/contact) pour réduire scripts tiers

6. Backend Spring: rate limiting et anti brute‑force

- Ajouter filtre Bucket4j pour /api/\*\*: token bucket (ex: 100 req/min/IP), 429 + Retry‑After
- Verrouillage temporaire du compte après N échecs de login (ex: 5/15 min)
- Journaliser 429 et échecs de login; métriques par IP/chemin

7. Tests et validation

- Cloudflare: valider les règles (simulate/challenge), vérifier analytics (rate limited, blocked)
- App Service: vérifier que l’origine n’est pas accessible hors IP Cloudflare
- Backend: test d’excès de requêtes → 429, test brute‑force → verrouillage
- Defender: vérifier que les plans actifs correspondent (AppServices=Standard, OSS/KV=Free) et que la facture cible est atteinte

8. Observabilité et suivi

- Tableau de bord: 429 par route/IP, échecs login, Cloudflare events
- Alerte si dépassement de seuils

Critères d’acceptation:

- Plans Defender conformes à la politique (prod vs non‑prod) et coûts réduits au niveau attendu
- Origine accessible uniquement via Cloudflare; WAF et rate limiting en place
- 429 correctement renvoyés en cas de dépassement; verrouillage login fonctionnel
- Documentation et CTD complètes avec patchs exacts et checklist post‑déploiement

Non‑objectifs:

- Pas d’activation de plans Defender sur des types non utilisés (VM/Containers/Storage) sans besoin explicite
- Pas de secrets côté front; pas de modification de la logique métier hors sécurité/limitation
