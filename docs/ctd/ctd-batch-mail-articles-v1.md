# Conception Technique Détaillée (CTD) — Batch e‑mail d’articles COMPLETED publiés non diffusés (V1)

## 1. Résumé exécutif

- Contexte: informer les abonnés (Subscribe) des nouveaux articles publiés V1.
- Objectif: sélectionner uniquement l’article le plus récent avec status=COMPLETED, display=true et newsletter=false, puis envoyer un e‑mail à chaque abonné avec lien absolu vers la page de l’article.
- Portée: batch interne côté backend (Spring Boot), sans UI dédiée.
- Non‑objectifs: envoi multi‑articles, gestion de désinscription, modification de Liquibase existante.

## 2. Architecture & choix techniques

- Composants:
  - Service ArticleDigestService: sélection de l’article, construction des liens, rendu Thymeleaf, envoi d’e‑mails, bascule idempotente newsletter=true.
  - Scheduler ArticleDigestScheduler: déclenchement périodique via @Scheduled, profilable par environnement.
  - Repositories V1: ArticleRepositoryV1 et SubscribeRepositoryV1 (fetch bag relationships pour les associations ManyToMany).
  - Templates Thymeleaf: nouveau articleDigestEmail.html, dérivé de subscribeEmail.html.
  - Mail: JavaMailSender (Brevo SMTP en prod), fallback activable.
- Patterns:
  - Service + Scheduler; idempotence stricte via bascule newsletter=true post‑succès global.
  - Eager loading des relations nécessaires via WithBagRelationships pour éviter N+1.
  - Construction de slug locale selon langKey de l’abonné (FR/EN).
- Décisions clés:
  - Sélection d’un unique article (date max) pour simplifier et garantir idempotence.
  - Liens absolus construits avec jhipster.mail.base-url, profilés par environnement.
  - Un envoi par abonné (pas de CC/Bcc de masse).

## 3. Modèle de domaine et données

- Entités: Article (labelFr/labelEn, descriptionFr/descriptionEn, date, status, display, newsletter), Subscribe (email, langKey).
- Invariants:
  - Article sélectionné: status=COMPLETED, display=true, newsletter=false.
  - Après succès global d’envoi, article.newsletter=true persisté.
- Idempotence:
  - Exécution répétée sans duplication: la bascule newsletter=true empêche toute re‑sélection ultérieure.

## 4. Interfaces & contrats

- Sélection des articles:
  - ArticleRepositoryV1:
    - Optional<Article> findFirstByStatusAndDisplayAndNewsletterOrderByDateDesc(Status.COMPLETED, true, false)
    - Variante eager:
      - default Optional<Article> findFirstByStatusAndDisplayAndNewsletterWithEagerRelationships(Status, Boolean, Boolean) utilisant fetchBagRelationships(Optional<Article>)
  - Fichier: src/main/java/com/devalgas/blog/repository/v1/ArticleRepositoryV1.java
- Abonnés:
  - Repository: src/main/java/com/devalgas/blog/repository/v1/SubscribeRepositoryV1.java
  - Entité: src/main/java/com/devalgas/blog/domain/Subscribe.java
- Service mail de référence:
  - sendEmailNewSubscribe(SubscribeDTO) dans src/main/java/com/devalgas/blog/service/impl/v1/MailServiceImplV1.java
  - Base URL injectée: jHipsterProperties.getMail().getBaseUrl() passée au contexte Thymeleaf.
- Template à créer:
  - Emplacement: src/main/resources/templates/mail/articleDigestEmail.html
  - Variables: article (Article), baseUrl, langKey, slug
  - Contenu: label FR/EN, description, date ISO‑8601 formatée, lien absolu vers /v1/articles/{id}-{slug}/view

## 5. Spécification de configuration

- Prod (application-prod.yml):

```yaml
spring:
  mail:
    host: ${SPRING_MAIL_HOST:smtp-relay.brevo.com}
    port: ${SPRING_MAIL_PORT:587}
    username: ${SPRING_MAIL_USERNAME:a05512001@smtp-brevo.com}
    password: ${SPRING_MAIL_PASSWORD:}
    properties:
      mail:
        smtp:
          auth: ${SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH:true}
          starttls:
            enable: ${SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE:true}
jhipster:
  mail:
    base-url: ${JHIPSTER_MAIL_BASE_URL:https://devalgas.net}
    from: ${JHIPSTER_MAIL_FROM:newsletter@devalgas.net}
fallback:
  mail:
    enabled: ${FALLBACK_MAIL_ENABLED:false}
    host: ${FALLBACK_MAIL_HOST:}
    port: ${FALLBACK_MAIL_PORT:587}
    username: ${FALLBACK_MAIL_USERNAME:}
    password: ${FALLBACK_MAIL_PASSWORD:}
app:
  article-digest:
    cron: '0 0 18 * * MON'
    zone: 'Europe/Paris'
```

- Local (application-local.yml):

```yaml
jhipster:
  mail:
    base-url: http://127.0.0.1:8080
    from: ${JHIPSTER_MAIL_FROM:newsletter@devalgas.net}
app:
  article-digest:
    cron: '0 */5 * * * *'
    zone: 'Europe/Paris'
```

- Azure (application-azure.yml):

```yaml
spring:
  mail:
    host: localhost
    port: 25
    username:
    password:
jhipster:
  mail:
    base-url: http://127.0.0.1:8080
app:
  article-digest:
    cron: '0 */5 * * * *'
    zone: 'Europe/Paris'
```

## 6. Sécurité & durcissement

- Un envoi par abonné, pas de CC/Bcc de masse.
- Aucune exposition de PII au‑delà de l’adresse e‑mail destinataire.
- Limiter la journalisation aux IDs d’article et d’abonné; ne pas logguer de secrets SMTP.
- Gestion des erreurs SMTP: capture MailException, backoff/retry contrôlé, fallback JavaMailSender si activé.

## 7. Performance & scalabilité

- Pagination côté repository si nécessaire pour les abonnés (ex: taille 50).
- Throttling d’envoi (ex: petite pause configurable entre messages).
- Possibilité d’asynchronisme par lot si besoin, en maintenant la corrélation d’exécution.
- Métriques d’exécution: latence moyenne par envoi, taux de succès/échec, temps total.

## 8. Observabilité

- Compteurs: total articles sélectionnés (0 ou 1), e‑mails tentés, succès, échecs, retry/fallback.
- Journaux: par abonné et par article, corrélation par UUID d’exécution de batch.
- Alertes: déclenchement si taux d’échec >10% sur une exécution; exposer métriques via Actuator/Prometheus.

## 9. Plan de tests et validation

- Commandes de tests:
  - Backend: npm run backend:unit:test
  - Frontend: npm run vitest-run
- Validation locale:
  - Rendu du template Thymeleaf articleDigestEmail.html avec baseUrl local.
  - Construction des slugs FR/EN et des liens absolus.
- Cas de test:
  - Aucun article, un article, plusieurs (ne doit en prendre qu’un), erreurs SMTP, fallback activé/désactivé, i18n du sujet (fr/en).
- Tests d’intégration GreenMail (scope test Maven):

```xml
<dependency>
  <groupId>com.icegreen</groupId>
  <artifactId>greenmail-junit5</artifactId>
  <version>1.6.13</version>
  <scope>test</scope>
</dependency>
```

- Désactiver le scheduling en test et appeler le service directement:

```yaml
spring:
  task:
    scheduling:
      enabled: false
app:
  article-digest:
    zone: 'Europe/Paris'
```

## 10. Risques, limites et alternatives

- Duplication d’envois: mitigée par newsletter=true.
- Erreurs SMTP/transitoires: backoff et retry, fallback activable.
- Slugs incorrects: normalisation stricte et sélection FR/EN via langKey.
- Charge mail: throttling et pagination, observabilité pour surveiller les pics.
- Alternative: diffusions multi‑articles ou digest hebdomadaire agrégé (hors périmètre V1).

## 11. Annexes

- Règles de slug (sans code): lowercase, normalisation NFD, suppression des diacritiques, remplacement non‑alphanum par '-', trim des tirets en bord, longueur max 80, source FR/EN choisie selon langKey.
- Construction du lien absolu:
  - Prod: baseUrl=https://devalgas.net
  - Autres profils: baseUrl=http://127.0.0.1:8080
  - Format: ${baseUrl}/v1/articles/${id}-${slug}/view
- Scheduling:
  - Prod: "0 0 18 \* \* MON" (Europe/Paris)
  - Dev/Local/Azure: "0 _/5 _ \* \* \*" (Europe/Paris)
- Liste des fichiers à créer/modifier:
  - src/main/resources/templates/mail/articleDigestEmail.html (créer)
  - src/main/java/com/devalgas/blog/service/ArticleDigestService.java (créer)
  - src/main/java/com/devalgas/blog/config/ArticleDigestScheduler.java (créer)
  - src/main/java/com/devalgas/blog/repository/v1/ArticleRepositoryV1.java (ajouter méthodes dérivées)
  - src/main/resources/config/application-prod.yml (ajouter app.article-digest + jhipster.mail)
  - src/main/resources/config/application-local.yml (ajouter app.article-digest)
  - src/main/resources/config/application-azure.yml (ajouter app.article-digest)
- Références de code:
  - Article: src/main/java/com/devalgas/blog/domain/Article.java
  - Repository: src/main/java/com/devalgas/blog/repository/v1/ArticleRepositoryV1.java
  - Subscribe: src/main/java/com/devalgas/blog/domain/Subscribe.java, src/main/java/com/devalgas/blog/repository/v1/SubscribeRepositoryV1.java
  - Service mail: src/main/java/com/devalgas/blog/service/impl/v1/MailServiceImplV1.java, src/main/java/com/devalgas/blog/service/MailService.java
  - Template référence: src/main/resources/templates/mail/subscribeEmail.html
