# Conception technique — Intégration SMTP (Brevo + Fournisseur de secours) v1

## Conception technique

- Objectifs
  - Prioriser Brevo pour l’envoi transactionnel, prévoir un fournisseur de secours.
  - Minimiser la complexité (paramétrage plutôt que code), sécuriser les secrets.
  - S’aligner sur Spring Boot (JavaMailSender), JHipster et GitOps/Terraform.
- Portée et contexte
  - Service d’envoi: [MailServiceImplV1](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/java/com/devalgas/blog/service/impl/v1/MailServiceImplV1.java) utilise JavaMailSender + Thymeleaf + `jhipster.mail.from`.
  - Configuration prod: [application-prod.yml](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/resources/config/application-prod.yml#L45-L52) mappe `spring.mail.*` et `jhipster.mail.base-url`.
  - Infra: GitOps ([.github/workflows/gitops.yml](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/.github/workflows/gitops.yml)) et Terraform injectent les App Settings et référencent Key Vault.
- Architecture
  - Flux d’envoi actuel: JavaMailSender direct (pas de failover en code).
  - Stratégie proposée: failover applicatif léger (Brevo → fournisseur de secours) déclenché sur `MailException/MessagingException` ou quota détecté.
  - Gouvernance: domaine expéditeur authentifié (SPF/DKIM/DMARC), quotas respectés.
- Flux (pseudo-code)
  - initialiser `primary` depuis `SPRING_MAIL_*`
  - si `FALLBACK_MAIL_ENABLED=true`, initialiser `fallback` depuis `FALLBACK_MAIL_*`
  - envoyer via `primary`; en cas d’erreur, tenter `fallback` si disponible; journaliser statut et fournisseur.
- Sécurité et conformité
  - STARTTLS sur port 587; ne jamais committer de secrets; utiliser Key Vault + Managed Identity/RBAC.
  - Authentifier le domaine expéditeur (SPF, DKIM, DMARC) et journaliser sans données sensibles.
- Quotas
  - Gmail personnel ≈ 500/j, Workspace jusqu’à ≈ 2 000/j; basculer vers fallback avec parcimonie.
  - Heuristique simple côté app (compteur quotidien) pour anticiper un dépassement et éviter surcharge.
- Observabilité et santé
  - Activer `management.health.mail.enabled=true` en prod une fois SMTP opérationnel.
  - Suivre les logs fournisseur (Brevo Transactional Logs) et les journaux applicatifs.

## Configuration des services

- Paramètres Brevo (principal)
  - Host: `smtp-relay.brevo.com`, Port: `587` (STARTTLS)
  - Username: votre email de connexion Brevo, Password: “SMTP key”
  - From: adresse validée comme “Transactional sender” dans Brevo
- Paramètres SMTP fallback (exemples: Gmail/Workspace/Office365/SES/SendGrid)
  - Host: `smtp.gmail.com`, Port: `587` (STARTTLS) ou `465` (SSL)
  - Auth: App Password (2FA) recommandé; Workspace possible via `smtp-relay.gmail.com`
- Azure App Service (prod)
  - Variables d’environnement injectées si `enable_email_service=true`:
    - `SPRING_MAIL_HOST`, `SPRING_MAIL_PORT`, `SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD`
    - `SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH=true`
    - `SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=true`
    - `JHIPSTER_MAIL_FROM` et `JHIPSTER_MAIL_BASE_URL`
  - Fallback optionnel (proposition):
    - `FALLBACK_MAIL_ENABLED=true|false`
    - `FALLBACK_MAIL_HOST`, `FALLBACK_MAIL_PORT`, `FALLBACK_MAIL_USERNAME`, `FALLBACK_MAIL_PASSWORD`
- Mapping Spring
  - `spring.mail.*` dans [application-prod.yml](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/resources/config/application-prod.yml#L45-L52)
  - `jhipster.mail.base-url` et `jhipster.mail.from` via `JHIPSTER_MAIL_BASE_URL` et `JHIPSTER_MAIL_FROM`
- Terraform
  - Variables: `mail_host`, `mail_port`, `mail_username`, `mail_password`, `mail_from` ([variables.tf](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/terraform/variables.tf#L72-L106))
  - App Settings: injection `SPRING_MAIL_*`, `JHIPSTER_MAIL_FROM`, STARTTLS ([app_service.tf](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/terraform/app_service.tf#L45-L55))
  - Secret: `smtp-password` dans Key Vault ([keyvault.tf](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/terraform/keyvault.tf#L85-L91)); `SPRING_MAIL_PASSWORD=@Microsoft.KeyVault(SecretUri=...)`

### Noms parlants et mapping (pour lisibilité)

- ExpediteurEmail → `JHIPSTER_MAIL_FROM` → propriété `jhipster.mail.from`
- BaseUrlPublique → `JHIPSTER_MAIL_BASE_URL` → propriété `jhipster.mail.base-url`
- SmtpHotePrincipal → `SPRING_MAIL_HOST` → propriété `spring.mail.host`
- SmtpPortPrincipal → `SPRING_MAIL_PORT` → propriété `spring.mail.port`
- SmtpUtilisateurPrincipal → `SPRING_MAIL_USERNAME` → propriété `spring.mail.username`
- SmtpMotDePassePrincipal → `SPRING_MAIL_PASSWORD` → propriété `spring.mail.password` (secret Key Vault: `smtp-password`)
- SmtpAuthActif → `SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH` → propriété `mail.smtp.auth`
- SmtpStartTlsActif → `SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE` → propriété `mail.smtp.starttls.enable`
- FailoverActif → `FALLBACK_MAIL_ENABLED` → indicateur de bascule applicative (si implémentée)
- SmtpHoteSecours → `FALLBACK_MAIL_HOST` → hôte SMTP de secours
- SmtpPortSecours → `FALLBACK_MAIL_PORT` → port SMTP de secours
- SmtpUtilisateurSecours → `FALLBACK_MAIL_USERNAME` → identifiant SMTP de secours
- SmtpMotDePasseSecours → `FALLBACK_MAIL_PASSWORD` → mot de passe/app password de secours
- Blocs à copier-coller (prod)
  - Brevo
    - `SPRING_MAIL_HOST = smtp-relay.brevo.com`
    - `SPRING_MAIL_PORT = 587`
    - `SPRING_MAIL_USERNAME = <login Brevo (email)>`
    - `SPRING_MAIL_PASSWORD = @Microsoft.KeyVault(SecretUri=https://kv-devalgas-blog.vault.azure.net/secrets/smtp-password/XXXXXXXX)`
    - `SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH = true`
    - `SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE = true`
    - `JHIPSTER_MAIL_FROM = noreply@<domaine-validé>`
    - `JHIPSTER_MAIL_BASE_URL = https://blog.<domaine>`
  - Fournisseur fallback (si activé)
    - `FALLBACK_MAIL_ENABLED = true`
    - `FALLBACK_MAIL_HOST = smtp.gmail.com`
    - `FALLBACK_MAIL_PORT = 587`
    - `FALLBACK_MAIL_USERNAME = <compte du fournisseur fallback>`
    - `FALLBACK_MAIL_PASSWORD = <App Password>`

### Fallback SMTP (Fournisseur de secours)

- Options
  - Fournisseur grand public (ex. Gmail)
    - Activer 2FA et générer un App Password “Mail”.
    - Risque de réécriture du “From” ou ajout “Sender”; quotas ≈ 500/jour.
    - À utiliser en dernier recours.
  - Fournisseur pro/Entreprise (ex. Google Workspace, Microsoft 365)
    - Vérifier le domaine, configurer DKIM et DMARC.
    - Authentification via `smtp.gmail.com` (AUTH) ou `smtp-relay.gmail.com` (Relay).
    - Aligne le DKIM et améliore la délivrabilité.
- Variables d’environnement (fallback)
  - `FALLBACK_MAIL_ENABLED=true`
  - `FALLBACK_MAIL_HOST=smtp.gmail.com`
  - `FALLBACK_MAIL_PORT=587`
  - `FALLBACK_MAIL_USERNAME=<compte>`
  - `FALLBACK_MAIL_PASSWORD=<App Password>`
- Politique de bascule (proposée)
  - Déclencher le fallback sur `MailException/MessagingException` ou erreurs 4xx/5xx spécifiques au quota/transport.
  - Journaliser le fournisseur utilisé; activer un circuit‑breaker court pour éviter la boucle d’échecs.
  - Désactiver proprement le fallback si non nécessaire ou si les quotas du fournisseur sont proches de la limite.
- Tests de fallback
  - Forcer une exception de transport (ex. host invalide côté primaire) et vérifier l’envoi via fallback.
  - Contrôler les en‑têtes (From, DKIM, DMARC) et les journaux fournisseur (Brevo/fallback).
- DNS et authentification de domaine
  - Publier SPF/DKIM/DMARC pour le domaine expéditeur (Brevo fournit DKIM; Workspace/M365 fournissent leurs enregistrements).
  - Les enregistrements MX/TXT doivent rester “DNS only” (Cloudflare).
- Validation
  - Envoyer un test (parcours d’abonnement), vérifier DKIM/SPF/DMARC dans les en-têtes et les logs Brevo.

## Autocritique

- Exactitude vs code
  - Le code V1 n’implémente pas de failover; la bascule est une proposition technique.
  - Le template [subscribeEmail.html](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/resources/templates/mail/subscribeEmail.html) attend `baseUrl`; le service n’injecte pas cette variable actuellement.
- Simplicité et clarté
  - Le découpage en deux blocs réduit la redondance et facilite l’ingestion par des modèles IA.
  - Les “blocs à copier-coller” rendent la configuration reproductible et vérifiable.
- Risques et limites
  - Un fallback grand public (ex. Gmail) doit rester exceptionnel (quotas, réécriture possible du “From”).
  - Multiplier les fournisseurs augmente la complexité (tests, surveillance).
- Améliorations recommandées
  - Injecter `baseUrl` dans le contexte Thymeleaf depuis `jhipster.mail.base-url`.
  - Ajouter des tests unitaires sur `MailServiceImplV1` pour l’envoi simple et depuis template.
  - Activer `management.health.mail.enabled=true` en prod pour le health check.
    - Conditions: SMTP AUTH doit être activé sur la boîte; identifiants = adresse complète (`noreply@devalgas.com`) + mot de passe
    - Attention MFA: si MFA est activée et que SMTP AUTH n’est pas compatible, prévoir une boîte technique dédiée ou utiliser un connecteur relay
  - Exchange Online SMTP Relay:
    - Configurez un connecteur dans Exchange Online; autorisez les IPs d’envoi et le routage TLS
    - Utile pour appareils/serveurs; pas d’authentification par identifiants côté application
- Intégration applicative (fallback possible):
  - `FALLBACK_MAIL_ENABLED=true`
  - `FALLBACK_MAIL_HOST=smtp.office365.com`
  - `FALLBACK_MAIL_PORT=587`
  - `FALLBACK_MAIL_USERNAME=noreply@devalgas.com`
  - `FALLBACK_MAIL_PASSWORD=<mot de passe de la boîte ou secret dédié>`
  - Secrets: stocker en Key Vault; injecter via App Settings
- Conseil Cloudflare:
  - Enregistrements MX/TXT/CNAME doivent être “DNS only” (non proxifiés)
  - Activez DNSSEC et la protection WHOIS pour la sécurité du domaine

## Checklist — Associer le domaine Cloudflare au SMTP (Brevo + fallback)

- Acheter ou transférer le domaine:
  - Cloudflare Registrar: acheter au prix coûtant, WHOIS redaction gratuite, DNSSEC possible.
  - Lien direct: https://domains.cloudflare.com/?domain=devalgas
- Fournisseur email (ex. Google Workspace) — fallback ou hébergement:
  - Workspace:
    - Vérifier le domaine via TXT, ajouter MX Google (DNS only).
    - SPF: `v=spf1 include:_spf.google.com ~all`
    - DKIM: générer et publier TXT `selector._domainkey`, activer la signature DKIM.
    - DMARC: TXT `_dmarc` (ex. `v=DMARC1; p=none; rua=mailto:dmarc@devalgas.com`).
    - Envoi:
      - Simple: `smtp.gmail.com` + App Password sur le compte `noreply@devalgas.com`.
      - Entreprise: `smtp-relay.gmail.com` (configuré dans l’Admin Console).
- Fournisseur grand public (ex. Gmail) — fallback léger:
  - App Password obligatoire (2FA activée), attention aux quotas et au “From” potentiel réécrit.
- Intégration applicative:
  - Prod: définir `SPRING_MAIL_*` et `jhipster.mail.from` (SMTP).
  - Activer le secours: `FALLBACK_MAIL_ENABLED=true` + `FALLBACK_MAIL_*` (fournisseur choisi).
  - Secrets: stocker en Key Vault, injecter via App Settings (jamais en clair dans les yml).
- Validation:
  - Envoyer un test, inspecter les en-têtes:
    - SPF: “pass” (Received-SPF)
    - DKIM: signature “pass”
    - DMARC: policy appliquée (none/quarantine/reject)
  - Consulter les logs Brevo et l’Email log Google Workspace.

## Blocs à copier‑coller — Cloudflare DNS et App Settings

- Remplacer `<DOMAIN>` par votre domaine (ex. devalgas.com), `<SELECTOR>` par le sélecteur DKIM, et les valeurs cibles fournies par le fournisseur.
- Dans Cloudflare, ces enregistrements doivent être “DNS only” (non proxifiés).

  - CNAME <SELECTOR>.\_domainkey
    - <DKIM_TARGET_FOURNI_PAR_LE_FOURNISSEUR (Brevo/Workspace/M365)>

- Google Workspace (DNS):

  - MX @
    - ASPMX.L.GOOGLE.COM (prio 1)
    - ALT1.ASPMX.L.GOOGLE.COM (prio 5)
    - ALT2.ASPMX.L.GOOGLE.COM (prio 5)
    - ALT3.ASPMX.L.GOOGLE.COM (prio 10)
    - ALT4.ASPMX.L.GOOGLE.COM (prio 10)
  - TXT @
    - v=spf1 include:\_spf.google.com ~all
  - TXT \_dmarc
    - v=DMARC1; p=none; rua=mailto:dmarc@<DOMAIN>
  - TXT <SELECTOR>.\_domainkey
    - v=DKIM1; k=rsa; p=<CLE_PUBLIQUE_FOURNIE_PAR_GOOGLE>

- Microsoft 365 / Exchange Online (DNS):

  - MX @
    - <TENANT_MX>.mail.protection.outlook.com (prio selon Admin)
  - TXT @
    - v=spf1 include:spf.protection.outlook.com ~all
  - CNAME selector1.\_domainkey
    - <VALEUR_CNAME_DKIM_SELECTOR1_FOURNIE_PAR_M365>
  - CNAME selector2.\_domainkey
    - <VALEUR_CNAME_DKIM_SELECTOR2_FOURNIE_PAR_M365>
  - TXT \_dmarc
    - v=DMARC1; p=none; rua=mailto:dmarc@<DOMAIN>

- Variables d’environnement (App Settings — Azure):
  - Brevo (primaire):
    - SPRING_MAIL_HOST=smtp-relay.brevo.com
    - SPRING_MAIL_PORT=587
    - SPRING_MAIL_USERNAME=<login_email>
    - SPRING_MAIL_PASSWORD=<secret>
    - MAIL_FROM=noreply@<DOMAIN>
    - jhipster.mail.base-url=https://<DOMAIN>
  - Gmail / Google Workspace (fallback):
    - FALLBACK_MAIL_ENABLED=true
    - FALLBACK_MAIL_HOST=smtp.gmail.com
    - FALLBACK_MAIL_PORT=587
    - FALLBACK_MAIL_USERNAME=noreply@<DOMAIN>
    - FALLBACK_MAIL_PASSWORD=<app_password>
  - Microsoft 365 (fallback alternatif):
    - FALLBACK_MAIL_ENABLED=true
    - FALLBACK_MAIL_HOST=smtp.office365.com
    - FALLBACK_MAIL_PORT=587
    - FALLBACK_MAIL_USERNAME=noreply@<DOMAIN>
    - FALLBACK_MAIL_PASSWORD=<secret_boite_m365>

## Guide pas‑à‑pas — Domaine Cloudflare → SMTP (Brevo + fallback)

- Préparer le domaine Cloudflare:
  - Acheter/transferer le domaine via Cloudflare Registrar (prix coûtant, DNSSEC possible).
  - Activer DNSSEC et 2FA sur le compte Cloudflare. Les enregistrements email (MX/TXT) doivent rester “DNS only”.
- Configurer Brevo (primaire):
  - Créer/activer le compte et authentifier le domaine (SPF/DKIM fournis par Brevo).
  - Créer un expéditeur transactionnel (Transactional sender).
  - Générer une “SMTP key” (Settings > SMTP & API) et la stocker en lieu sûr.
- Configurer Google Workspace (fallback ou hébergement):
  - Vérifier le domaine: ajouter TXT `google-site-verification=...`.
  - Poser les MX Google (DNS only):
    - `ASPMX.L.GOOGLE.COM` (prio 1)
    - `ALT1.ASPMX.L.GOOGLE.COM` (prio 5)
    - `ALT2.ASPMX.L.GOOGLE.COM` (prio 5)
    - `ALT3.ASPMX.L.GOOGLE.COM` (prio 10)
    - `ALT4.ASPMX.L.GOOGLE.COM` (prio 10)
  - Authentifier le domaine:
    - SPF (TXT `@`): `v=spf1 include:_spf.google.com ~all`
    - DKIM (TXT `selector._domainkey`): `v=DKIM1; k=rsa; p=<clé>`, puis activer la signature DKIM dans l’Admin Console
    - DMARC (TXT `_dmarc`): ex. `v=DMARC1; p=none; rua=mailto:dmarc@devalgas.com`
  - Créer l’utilisateur `noreply@devalgas.com`, activer 2FA, générer un App Password ou configurer SMTP Relay.
- Configurer l’application:
  - Brevo (primaire):
    - `SPRING_MAIL_HOST=smtp-relay.brevo.com`
    - `SPRING_MAIL_PORT=587`
    - `SPRING_MAIL_USERNAME=<login email>`
    - `SPRING_MAIL_PASSWORD=<secret en Key Vault>`
    - `jhipster.mail.from=noreply@devalgas.com`
  - Fallback (ex. Gmail/Workspace):
    - `FALLBACK_MAIL_ENABLED=true`
    - `FALLBACK_MAIL_HOST=smtp.gmail.com`
    - `FALLBACK_MAIL_PORT=587`
    - `FALLBACK_MAIL_USERNAME=noreply@devalgas.com`
    - `FALLBACK_MAIL_PASSWORD=<App Password>`
  - Secrets: stocker en Key Vault et injecter via App Settings (ne pas mettre en clair dans les yml).
- Valider:
  - Envoyer un email test depuis l’application; vérifier:
    - SPF: “pass”
    - DKIM: “pass” (signature présente)
    - DMARC: policy appliquée
  - Vérifier les Transactional Logs Brevo et l’Email log Google Workspace.

## Dépannage — SMTP générique

- Vérifier que le domaine expéditeur est correctement authentifié (SPF/DKIM) et que l’adresse “From” est autorisée.
- Contrôler les identifiants SMTP (login, mot de passe/clé) et le port/chiffrement (587 + STARTTLS ou 465 + SSL).
- Examiner les logs du fournisseur (Brevo: Transactional Logs) pour les codes d’erreur et rebonds.

## Alternatives au fournisseur SMTP

- Amazon SES:
  - Hôte: `email-smtp.<region>.amazonaws.com`, Port: 587, TLS
  - Auth: identifiants SMTP générés dans la console (username/password)
  - DNS: vérification de domaine; DKIM recommandé; DMARC à configurer
  - Avantages: coût bas, haute délivrabilité; Attention: sandbox initial et quotas par région
- SendGrid:
  - Hôte: `smtp.sendgrid.net`, Port: 587
  - Auth: username `apikey`, password `<API_KEY>`
  - DNS: “Domain Authentication” via CNAME; DMARC recommandé
  - Avantages: intégrations et outils; free tier limité
- Mailgun:
  - Hôte: `smtp.mailgun.org`, Port: 587
  - Auth: `postmaster@<DOMAIN>` + password
  - DNS: DKIM, SPF `include:mailgun.org`; DMARC à configurer
  - Avantages: délivrabilité solide; attention au pricing après free tier
- Postmark:
  - Hôte: `smtp.postmarkapp.com`, Port: 587
  - Auth: Server Token comme mot de passe
  - DNS: DKIM, SPF (via domain signature); DMARC recommandé
  - Avantages: focus transactionnels, haute réputation; pas marketing massif
- SparkPost:
  - Hôte: `smtp.sparkpostmail.com`, Port: 587
  - Auth: API Key
  - DNS: DKIM/SPF via “Domain configuration”; DMARC recommandé
- Brevo (Sendinblue):
  - Hôte: `smtp-relay.sendinblue.com`, Port: 587
  - Auth: login, password = API Key
  - DNS: authentification de domaine via CNAME; DMARC recommandé
- Mailjet:
  - Hôte: `in-v3.mailjet.com`, Port: 587
  - Auth: API Key / Secret Key
  - DNS: DKIM/SPF via “Domain configuration”; DMARC recommandé
- Remarques d’intégration:
  - Variables d’environnement usuelles:
    - `SPRING_MAIL_HOST`, `SPRING_MAIL_PORT`, `SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD`, `MAIL_FROM`, `jhipster.mail.base-url`
  - Toujours vérifier et poser DKIM/SPF/DMARC pour le domaine, quel que soit le fournisseur
  - Éviter de mélanger trop de fournisseurs simultanément; un primaire + un fallback suffisent

## Risques et limites

- Les fournisseurs grand public (ex. Gmail) ne sont pas idéaux pour du volume ou des envois applicatifs fréquents; risque de suspension si mauvaise hygiène.
- Les plans gratuits des fournisseurs imposent des quotas; prévoir une montée de plan si trafic augmente.
- La bascule multi‑fournisseur ajoute de la complexité (tests, configuration, surveillance).

## Autocritique

- Pertinence: prioriser Brevo permet une intégration simple en gratuit. Le secours via un fournisseur alternatif est utile mais doit rester exceptionnel.
- Performance/efficacité: l’implémentation de failover dans une couche dédiée minimise l’impact et garde les services v1 stables.
- Évolutivité: la stratégie est extensible (API/Logs Brevo pour tracking, bascule vers paid plan sans changer de code).
- Sécurité: Key Vault, STARTTLS/SSL, et authentification de domaine réduisent les risques de fuite et améliorent la délivrabilité.
- Constat du code v1 et corrections:
  - Le template de souscription doit être adressé par nom Thymeleaf sans extension. Corrigé: [`MailServiceImplV1.sendEmailNewSubscribe`](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/java/com/devalgas/blog/service/impl/v1/MailServiceImplV1.java#L98-L101) utilise `mail/subscribeEmail`.
  - Le template [`subscribeEmail.html`](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/resources/templates/mail/subscribeEmail.html#L6) attend `baseUrl` pour l’icône; injection ajoutée dans [`sendEmailFromTemplateSync`](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/java/com/devalgas/blog/service/impl/v1/MailServiceImplV1.java#L121-L127).
  - Intégration failover: [`FailoverMailSenderV1`](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/java/com/devalgas/blog/service/impl/v1/FailoverMailSenderV1.java) route l’envoi vers un fournisseur secondaire si l’envoi primaire échoue; [`MailServiceImplV1`](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/java/com/devalgas/blog/service/impl/v1/MailServiceImplV1.java#L67-L90) l’utilise désormais pour l’envoi.
  - `jhipster.mail.base-url` est en placeholder en prod; à surcharger via App Settings pour générer des liens corrects.
  - `management.health.mail.enabled` est désactivé globalement; à activer en prod pour observabilité.

## Checklist de mise en œuvre (v1)

1. Activer `enable_email_service=true` dans Terraform et renseigner `mail_*`.
2. Vérifier/ajouter les enregistrements SPF/DKIM pour le domaine expéditeur (fournis par Brevo).
3. Stocker `mail_password` dans Key Vault; déployer via GitOps.
4. Ajouter les tests unitaires v1 pour `MailServiceImplV1`.
5. (Option) Implémenter `FailoverMailSenderV1` + variables `FALLBACK_MAIL_*`.
6. Valider observabilité et journaux côté Brevo et applicatif.
7. Activer `management.health.mail.enabled=true` en prod pour le health check.

## Exemples de pseudos/identifiants (placeholders)

- Expéditeur (From):
  - `MAIL_FROM = noreply@devalgas.com` (domaine vérifié chez Brevo)
- Brevo:
  - `SPRING_MAIL_HOST = smtp-relay.brevo.com`
  - `SPRING_MAIL_PORT = 587`
  - `SPRING_MAIL_USERNAME = brevo_user_app`
  - `SPRING_MAIL_PASSWORD = <référencé via Key Vault: smtp-password>`
- Gmail (secours, si activé):
  - `FALLBACK_MAIL_HOST = smtp.gmail.com`
  - `FALLBACK_MAIL_PORT = 587`
  - `FALLBACK_MAIL_USERNAME = app.devalgas@gmail.com`
  - `FALLBACK_MAIL_PASSWORD = <App Password> (référencé via Key Vault)`

## Implémentation failover concrète (v1)

- Composant d’envoi:
  - [`FailoverMailSenderV1`](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/java/com/devalgas/blog/service/impl/v1/FailoverMailSenderV1.java) tente l’envoi primaire, journalise l’échec, puis essaye le fallback s’il est disponible.
- Configuration du fallback:
  - [`MailFallbackConfiguration`](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/java/com/devalgas/blog/config/MailFallbackConfiguration.java) fournit un bean `fallbackJavaMailSender` si `fallback.mail.enabled=true`.
  - Variables d’environnement attendues (mapping Spring Boot):
    - `FALLBACK_MAIL_ENABLED=true`
    - `FALLBACK_MAIL_HOST=smtp.gmail.com`
    - `FALLBACK_MAIL_PORT=587`
    - `FALLBACK_MAIL_USERNAME=...`
    - `FALLBACK_MAIL_PASSWORD=...`
  - SMTP sécurisé:
    - `mail.smtp.auth=true`
    - `mail.smtp.starttls.enable=true`
