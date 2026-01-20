Titre: Mise à jour / Envoi d’e-mails de contact après createMessage (API v1)

Rôle: Tu es un architecte et ingénieur logiciel senior. Tu dois produire en français une mise à jour maîtrisée d’une CTD existante, avec traçabilité, compatibilité et validation de non‑régression.

Objectif: Doubler l’envoi après createMessage (API v1) avec:

- Accusé de réception à l’expéditeur (utilisateur) via message.email
- Notification interne avec détails du message vers application.contact.email
  Les deux e‑mails utilisent un “from” contrôlé par MAIL_FROM_CONTACT (défaut: contact@devalgas.net), des templates Thymeleaf, i18n, envoi asynchrone et un contenu texte alternatif pour éviter les corps vides.

Entrées:

- CTD source et version: conception-technique-mail-v1.md (v1), configuration mail (Brevo SMTP), GitOps/TF mappant SPRING*MAIL*_ et JHIPSTER*MAIL*_.
- Motif: amélioration fonctionnelle (accusé de réception + notification interne).
- Périmètre: API v1 Message (createMessage), service mail v1, templates, i18n, configuration applicative (env/app settings).
- Volumétrie/SLA: faible; envoi asynchrone hors transaction; impact latence API négligeable.
- Risques/dépendances: SMTP opérationnel, Key Vault pour secrets, reCAPTCHA intact; i18n sujets et contenu.

1. Contexte et périmètre

- Actuel: /api/v1/messages envoie 2 e‑mails après persistance:
  - Confirmation à l’expéditeur (utilisateur)
  - Notification interne avec détails du message (admin)
- Correction: ajout d’un contenu texte alternatif pour éviter les corps vides côté clients qui ignorent le HTML.
- Non‑objectifs: pièces jointes, retours d’état SMTP avancés, templating conditionnel complexe.

2. Synthèse de la CTD existante

- Service mail v1: MailServiceImplV1 (Thymeleaf + i18n, envoi async, failover optionnel).
- Contrôleur subscribe v1: SubscribeResourceV1 appelle sendEmailNewSubscribe.
- Config: jhipster.mail.from/base-url, SPRING*MAIL*_, FALLBACK*MAIL*_ (optionnel), health mail activé en prod.
- GitOps/TF: app settings pour SPRING*MAIL*_, JHIPSTER*MAIL*_, RECAPTCHA\_\*, CONTACT_EMAIL/PHONE; secrets SMTP via Key Vault.

3. Objectifs de modification et critères de non‑régression

- Confirmation utilisateur: to = message.email; sujet i18n; HTML + texte alternatif; pas de détails du message.
- Notification admin: to = application.contact.email; sujet i18n; HTML + texte alternatif; inclut les détails (Nom, Email, Téléphone, Objet, Message).
- from = MAIL_FROM_CONTACT pour les deux envois; envoi asynchrone; pas d’échec bloquant API (log, failover si présent).
- reCAPTCHA et validations inchangés; aucune modification du modèle de données; fallback Accept‑Language si langKey manquant.

4. Décisions techniques et architecture cible

- MailServiceImplV1:
  - sendEmailNewMessage(MessageDTO): to = message.getEmail(); sujet i18n email.message.title; template mail/contactMessageEmail.html; construction d’un “plain text” (welcome, text1, regards, signature); from = application.mail.contact-from.
  - sendEmailNewMessageNotification(MessageDTO): to = application.contact.email; sujet i18n email.message.notify.title; template mail/contactMessageAdminEmail.html; “plain text” listant les détails; from = application.mail.contact-from.
- Templates:
  - contactMessageEmail.html: sans bloc “détails”, utilise white-space: pre-line pour email.message.text1.
  - contactMessageAdminEmail.html: inclut le bloc “détails” (Name, Email, Phone, Subject, Message).
- Contrôleur MessageResourceV1: après save, appeler sendEmailNewMessage(messageDTO) puis sendEmailNewMessageNotification(messageDTO).
- Fallback langue: si message.langKey absent, resolve via en-tête Accept-Language (fr/en), sinon défaut en.

5. Spécifications détaillées mises à jour

- API/événement:
  - Endpoint: POST /api/v1/messages
  - Post‑persist: déclencher sendEmailNewMessage(messageDTO) puis sendEmailNewMessageNotification(messageDTO)
- Templates:
  - Fichier confirmation: src/main/resources/templates/mail/contactMessageEmail.html
    - Variables: message, baseUrl; sujet i18n: email.message.title
    - Contenu: salutation + texte principal; white-space: pre-line sur text1; pas de bloc “détails”
  - Fichier admin: src/main/resources/templates/mail/contactMessageAdminEmail.html
    - Variables: message, baseUrl; sujet i18n: email.message.notify.title
    - Contenu: bloc “détails” avec Name, Email, Phone, Subject, Message
- i18n:
  - messages_fr.properties:
    ```
    email.message.title=Confirmation de réception
    email.message.welcome=Bonjour,
    email.message.text1=Merci pour votre message 🌟 \n J'ai bien reçu et vous répondrai dans les meilleurs délais.
    email.message.notify.title=Nouveau message reçu
    email.message.name=Nom
    email.message.email=Email
    email.message.phone=Téléphone
    email.message.subject=Objet
    email.message.body=Message
    ```
  - messages_en.properties:
    ```
    email.message.title=Confirmation of receipt
    email.message.welcome=Hello,
    email.message.text1=Thank you for your message 🌟 \n We have received it and will respond as soon as possible.
    email.message.notify.title=New message received
    email.message.name=Name
    email.message.email=Email
    email.message.phone=Phone
    email.message.subject=Subject
    email.message.body=Message
    ```
- Configuration (blocs exacts):
  - application-local.yml:
    ```
    application:
      mail:
        contact-from: ${MAIL_FROM_CONTACT:contact@devalgas.net}
      contact:
        email: ${CONTACT_EMAIL:devkobbi@gmail.com}
    ```
  - application-dev.yml:
    ```
    application:
      mail:
        contact-from: ${MAIL_FROM_CONTACT:contact@devalgas.net}
      contact:
        email: ${CONTACT_EMAIL:devkobbi@gmail.com}
    ```
  - application-prod.yml:
    ```
    application:
      recaptcha:
        enabled: true
        secret: ${RECAPTCHA_SECRET:}
      mail:
        contact-from: ${MAIL_FROM_CONTACT:contact@devalgas.net}
      contact:
        email: ${CONTACT_EMAIL:}
    ```
  - GitOps/Terraform: s’assurer que MAIL_FROM_CONTACT et CONTACT_EMAIL sont mappés en app settings; mots de passe SMTP via Key Vault.

6. Impacts sécurité, performance et observabilité

- Sécurité: pas de fuite de secrets; “from” via MAIL_FROM_CONTACT; reCAPTCHA inchangé; admin destinataire via CONTACT_EMAIL.
- Performance: envoi asynchrone hors transaction; surcharge négligeable; failover optionnel.
- Observabilité: logs d’envoi/erreurs; health mail actif en prod; métriques inchangées.

7. Plan de migration et rollback

- Déployer/valider MAIL_FROM_CONTACT et CONTACT_EMAIL en app settings (prod).
- En cas d’incident SMTP, l’API reste fonctionnelle; rollback en désactivant l’appel de notification admin si nécessaire.

8. Plan de tests et validation

- Tests unitaires/IT pour MailServiceImplV1:
  - Confirmation: vérifier from == MAIL_FROM_CONTACT, to == message.email, sujets i18n non vides, HTML et texte alternatif non vides.
  - Admin: vérifier from == MAIL_FROM_CONTACT, to == application.contact.email, présence des labels/détails.
- Tests d’intégration API v1:
  - createMessage avec reCAPTCHA valide; vérifier double invocation (confirmation + notification).
- Validation locale:
  - npm run backend:unit:test

9. Changelog CTD

- Ajout confirmation utilisateur et notification admin après createMessage.
- Nouveau template contactMessageAdminEmail.html; mise à jour contactMessageEmail.html (sans détails, pre-line).
- Ajout des clés i18n email.message.notify.title; mise à jour des sujets.
- Ajout d’un contenu texte alternatif pour éviter les corps vides.

10. Fichiers modifiés

- /Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/java/com/devalgas/blog/web/rest/v1/MessageResourceV1.java
- /Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/java/com/devalgas/blog/service/impl/v1/MailServiceImplV1.java
- /Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/resources/templates/mail/contactMessageEmail.html
- /Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/resources/templates/mail/contactMessageAdminEmail.html
- /Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/resources/i18n/messages_fr.properties
- /Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/resources/i18n/messages_en.properties
- /Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/resources/config/application-prod.yml
- /Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/resources/config/application-dev.yml
- /Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/resources/config/application-local.yml

Références

- MessageResourceV1: [MessageResourceV1.java](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/java/com/devalgas/blog/web/rest/v1/MessageResourceV1.java)
- Service mail v1: [MailServiceImplV1.java](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/java/com/devalgas/blog/service/impl/v1/MailServiceImplV1.java)
- Templates: [contactMessageEmail.html](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/resources/templates/mail/contactMessageEmail.html), [contactMessageAdminEmail.html](file:///Users/devalgas/Documents/projets/perso/blog-devalgas/src/main/resources/templates/mail/contactMessageAdminEmail.html)
