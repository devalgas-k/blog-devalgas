Titre: Conception Technique Détaillée (CTD) — Batch e‑mail d’articles COMPLETED non affichés (V1)

Rôle: Tu es un architecte logiciel senior. Produis en français une CTD exhaustive pour concevoir un batch qui récupère les articles V1 avec status=COMPLETED et display=false, puis envoie un e‑mail aux abonnés (Subscribe) avec un nouveau template dérivé de subscribeEmail.html. Ne pas implémenter; livrer uniquement la CTD et les blocs/configs prêts à l’emploi.

Objectif: Définir l’architecture, les choix techniques, les spécifications détaillées et les impacts d’un batch qui:

- Récupère les ArticleHomeV1 via articleHomeRepositoryV1.findByStatusAndDisplayWithEagerRelationships(Status.COMPLETED, false, pageable)
- Construit un lien absolu vers la page article (slug FR/EN) en fonction du profil (prod: devalgas.net; autres: localhost/127.0.0.1)
- Envoie un e‑mail aux abonnés (Subscribe) en s’inspirant de MailServiceImplV1.sendEmailNewSubscribe, avec un nouveau template comprenant label, description, date et lien vers l’article
- Assure l’idempotence (pas d’envoi répété) et BASCULE OBLIGATOIREMENT display à true à la fin du batch

Entrées (à fournir et à intégrer dans la CTD):

- Contexte et objectifs métier: informer les abonnés des nouveaux articles publiés (status COMPLETED) non encore diffusés (display=false)
- Hypothèses et périmètre: batch interne, pas d’UI; un e‑mail par abonné; slug calculé à la volée
- Contraintes et dépendances: Spring Boot 3.5.x, Java 17, Thymeleaf, JHipsterProperties.mail.base-url, Brevo SMTP + fallback
- Volumétrie et SLA attendus: faible à modéré; envois séquentiels/asynchrones avec limite de débit si nécessaire
- Risques pressentis: duplication d’envois, erreurs SMTP, slugs incorrects, charge mail

Livrables attendus:

- CTD complète au format Markdown suivant la structure ci‑dessous
- Spécifications détaillées (API interne, modèle de données, configuration, scheduling)
- Liste des fichiers à créer/modifier avec chemins absolus
- Blocs de configuration exacts et testables par profil (prod/dev/local/azure)
- Plan de tests et validation locale (sans implémentation)

Contraintes et contexte (adapter depuis le projet):

- Stack: Java 17, Spring Boot 3.5.x, Maven
- Mail: JHipsterProperties.mail.{from,base-url}; SMTP Brevo; fallback activable
- Repositories V1 avec fetch join via WithBagRelationships (ManyToMany LAZY)
- Aucune fuite de secrets; ne pas modifier des changesets Liquibase existants

Structure et contenu requis de la CTD (squelette à respecter):

1. Résumé exécutif
   - Contexte, objectifs, portée, non‑objectifs
2. Architecture & choix techniques
   - Composants, patterns (Service + Scheduler), justification des décisions
3. Modèle de domaine et données
   - Entités utilisées: ArticleHomeV1, Subscribe; invariants (status, display); stratégie d’idempotence (bascule display à true)
4. Interfaces & contrats
   - Service batch, dépendances repository, contrat de template (variables: articles, baseUrl, lang)
5. Spécification de configuration
   - Propriétés applicatives exactes (mail.base-url par profil; SMTP; fallback.\*)
   - Blocs prêts à l’emploi (sans commentaires), alignés sur les fichiers du projet
6. Sécurité & durcissement
   - Pas de CC/Bcc massif; un envoi par abonné; pas d’exposition PII; gestion d’erreurs SMTP
7. Performance & scalabilité
   - Pagination côté repository; throttling; asynchronisme; métriques
8. Observabilité
   - Journaux, compteurs d’envoi/succès/échec, traces; alertes si taux d’échec élevé
9. Plan de tests et validation
   - Stratégie, cas nominaux/erreurs; validation locale via build/tests existants du projet
10. Risques, limites et alternatives

- Mitigation duplication, backoff SMTP, fallback provider

11. Annexes

- Références de code et chemins de fichiers

Spécifications à intégrer (exacts et contextualisés):

- Sélection des articles:
  - Méthode: ArticleRepositoryV1.findByStatusAndDisplayWithEagerRelationships(Status.COMPLETED, false, pageable)
  - Fichier: [ArticleRepositoryV1.java](src/main/java/com/devalgas/blog/repository/v1/ArticleRepositoryV1.java)
  - Domaine: [Article.java](src/main/java/com/devalgas/blog/domain/Article.java)
- Abonnés:
  - Repository: [SubscribeRepositoryV1.java](src/main/java/com/devalgas/blog/repository/v1/SubscribeRepositoryV1.java)
  - Entité: [Subscribe.java](src/main/java/com/devalgas/blog/domain/Subscribe.java)
- Service de mail (référence pour conception):
  - Méthode existante: sendEmailNewSubscribe(SubscribeDTO) dans [MailServiceImplV1.java](src/main/java/com/devalgas/blog/service/impl/v1/MailServiceImplV1.java#L154-L163)
  - Base URL injectée: jHipsterProperties.getMail().getBaseUrl() via le contexte Thymeleaf
- Nouveau template à créer (dérivé de subscribeEmail.html):
  - Emplacement: src/main/resources/templates/mail/articlesDigestEmail.html
  - Variables: articles (List<Article>), baseUrl, langKey
  - Contenu: pour chaque article, afficher label (FR/EN selon abonné), description, date (ISO‑8601 formaté), lien absolu vers /v1/articles/{id}-{slug}/view
  - Référence visuelle: [subscribeEmail.html](src/main/resources/templates/mail/subscribeEmail.html)
- Calcul du slug (à décrire dans la CTD, sans code):
  - Règles: lowercase, normalisation NFD, suppression diacritiques, remplacement non‑alphanum par ‘-’, trim des tirets, longueur max 80
  - Sélection FR/EN: utiliser langKey de l’abonné pour choisir labelFr/labelEn
- Construction du lien:
  - En prod: baseUrl=https://devalgas.net (JHIPSTER_MAIL_BASE_URL)
  - Autres profils: baseUrl=http://127.0.0.1:8080
  - Format: ${baseUrl}/v1/articles/${id}-${slug}/view
- Scheduling (à définir):
  - Prod: @Scheduled(cron = "0 0 18 \* \* \*", zone="Europe/Paris") — quotidien 18:00
  - Dev/Local/Azure: @Scheduled(cron = "0 _/5 _ \* \* \*", zone="Europe/Paris") — toutes les 5 minutes
  - Pagination: taille raisonnable (ex.: 50) pour éviter de longs envois
- Post‑traitement:
  - À la fin du batch et après succès global d’envoi, basculer article.display à true et persister la mise à jour
- Idempotence:
  - Basculer article.display à true à la fin du batch après succès d’envoi
- Fallback SMTP:
  - Si envoi échoue (MailException), retenter via un JavaMailSender “fallback” si activé
  - Propriétés fallback.\* présentes dans application‑prod.yml

Blocs de configuration exacts (à inclure tels quels dans la CTD):

- application-prod.yml:
  - mail.base-url: ${JHIPSTER_MAIL_BASE_URL:https://devalgas.net}
  - mail.from: ${JHIPSTER_MAIL_FROM:newsletter@devalgas.net}
  - SPRING*MAIL*_ pour Brevo; fallback.mail._ activables
  - Fichier: [application-prod.yml](src/main/resources/config/application-prod.yml#L115-L117)
- application-local.yml:
  - mail.base-url: http://127.0.0.1:8080
  - Fichier: [application-local.yml](src/main/resources/config/application-local.yml#L109-L111)
- application-azure.yml:
  - mail.base-url: http://127.0.0.1:8080
  - Fichier: [application-azure.yml](src/main/resources/config/application-azure.yml#L104-L105)

Observabilité (à détailler dans la CTD):

- Compteurs: total articles sélectionnés, e‑mails tentés, succès, échecs, retry/fallback
- Logs: par abonné, par article, avec corrélation; latence moyenne
- Alertes: seuil d’échec >10% sur une exécution

Plan de tests et validation (sans implémentation):

- Tests backend existants: “npm run backend:unit:test” et “npm run vitest-run”
- Validation locale: gabarits Thymeleaf sur baseUrl local; vérification du rendu du nouveau template
- Cas: aucun article, un article, plusieurs, erreurs SMTP, fallback activé/désactivé

Références de code:
Stratégies de tests pour le scheduler

- Approche recommandée (fiable et rapide):

  - Désactiver le scheduling en test et appeler le service directement (pas d’attente temps réel).
  - application-test.yml:
    ```
    spring:
      task:
        scheduling:
          enabled: false
    app:
      article-digest:
        zone: "Europe/Paris"
    ```
  - Test d’intégration:

    ```java
    @SpringBootTest(properties = { "spring.task.scheduling.enabled=false" })
    class ArticleDigestServiceIT {

      @Autowired
      ArticleDigestService service;

      @Autowired
      ArticleHomeRepositoryV1 articleRepo;

      @Autowired
      SubscribeRepositoryV1 subscribeRepo;

      @MockBean
      JavaMailSender mailSender;

      @Test
      void processDigest_setsDisplayTrue_andSendsEmails() {
        service.processDigest();
        // assertions sur display=true et vérification d'appels mailSender.send(...)
      }
    }

    ```

- Variante avec scheduler à 5 secondes (possible, pour tests manuels):
  - application-test.yml:
    ```
    spring:
      task:
        scheduling:
          enabled: true
    app:
      article-digest:
        cron: "*/5 * * * * *"
        zone: "Europe/Paris"
    ```
  - Risques: tests non déterministes, attente active, flakiness. À réserver aux validations manuelles ou environnements de démo.

Tests SMTP avec GreenMail (intégration sûre)

- Dépendance Maven (scope test):

```
<dependency>
  <groupId>com.icegreen</groupId>
  <artifactId>greenmail-junit5</artifactId>
  <version>1.6.13</version>
  <scope>test</scope>
</dependency>
```

- Configuration test (Spring Mail → GreenMail):

```
spring:
  mail:
    host: localhost
    port: ${GREENMAIL_SMTP_PORT:3025}
    username:
    password:
    properties:
      mail:
        smtp:
          auth: false
          starttls:
            enable: false
app:
  article-digest:
    zone: "Europe/Paris"
```

- Exemple JUnit 5:

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(
  properties = {
    "spring.mail.host=localhost",
    "spring.mail.port=3025",
    "spring.mail.properties.mail.smtp.auth=false",
    "spring.mail.properties.mail.smtp.starttls.enable=false",
    "spring.task.scheduling.enabled=false",
  }
)
class ArticleDigestGreenMailIT {

  @RegisterExtension
  static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP).withConfiguration(
    GreenMailConfiguration.aConfig().withDisabledAuthentication()
  );

  @Autowired
  ArticleDigestService service;

  @Test
  void processDigest_sendsEmails_andReceivesWithGreenMail() throws Exception {
    service.processDigest();
    assertTrue(greenMail.waitForIncomingEmail(5000, 1)); // adapter au nombre d’abonnés
    MimeMessage[] received = greenMail.getReceivedMessages();
    assertTrue(received.length >= 1);
    String subject = received[0].getSubject();
    assertNotNull(subject);
    Object content = received[0].getContent();
    String html = content instanceof String ? (String) content : "";
    assertTrue(html.contains("/v1/articles/"));
  }
}

```

Vérification i18n du sujet (fr/en)

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(
  properties = {
    "spring.mail.host=localhost",
    "spring.mail.port=3025",
    "spring.mail.properties.mail.smtp.auth=false",
    "spring.mail.properties.mail.smtp.starttls.enable=false",
    "spring.task.scheduling.enabled=false",
  }
)
class ArticleDigestI18nSubjectIT {

  @RegisterExtension
  static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP).withConfiguration(
    GreenMailConfiguration.aConfig().withDisabledAuthentication()
  );

  @Autowired
  ArticleDigestService service;

  @Autowired
  MessageSource messageSource;

  @Test
  void fr_and_en_subjects_match_i18n() throws Exception {
    service.processDigest();
    assertTrue(greenMail.waitForIncomingEmail(5000, 2));
    MimeMessage[] msgs = greenMail.getReceivedMessages();
    List<String> subjects = Arrays.stream(msgs)
      .map(m -> {
        try {
          return m.getSubject();
        } catch (MessagingException e) {
          return "";
        }
      })
      .toList();
    String frExpected = messageSource.getMessage("email.digest.title", null, Locale.forLanguageTag("fr"));
    String enExpected = messageSource.getMessage("email.digest.title", null, Locale.forLanguageTag("en"));
    assertTrue(subjects.contains(frExpected));
    assertTrue(subjects.contains(enExpected));
  }
}

```

Exemples de code (indicatifs, à adapter; ne pas implémenter tels quels):

- Variante monofichier via propriétés (recommandée):

```java
@Configuration
@EnableScheduling
public class ArticleDigestScheduler {

  private final ArticleDigestService service;

  public ArticleDigestScheduler(ArticleDigestService service) {
    this.service = service;
  }

  @Scheduled(cron = "${app.article-digest.cron}", zone = "${app.article-digest.zone:Europe/Paris}")
  public void run() {
    service.processDigest();
  }
}

```

Propriétés par profil:

application-prod.yml

```
app:
  article-digest:
    cron: "0 0 18 * * *"
    zone: "Europe/Paris"
```

application-local.yml (et dev/azure)

```
app:
  article-digest:
    cron: "0 */5 * * * *"
    zone: "Europe/Paris"
```

- Service batch (sélection, envoi, bascule display=true):

```java
@Service
public class ArticleDigestService {

  private final ArticleRepositoryV1 articleRepo;
  private final SubscribeRepositoryV1 subscribeRepo;
  private final JHipsterProperties jhipster;
  private final SpringTemplateEngine templateEngine;
  private final JavaMailSender mailSender;
  private final MessageSource messageSource;

  public ArticleDigestService(
    ArticleRepositoryV1 articleRepo,
    SubscribeRepositoryV1 subscribeRepo,
    JHipsterProperties jhipster,
    SpringTemplateEngine templateEngine,
    JavaMailSender mailSender,
    MessageSource messageSource
  ) {
    this.articleRepo = articleRepo;
    this.subscribeRepo = subscribeRepo;
    this.jhipster = jhipster;
    this.templateEngine = templateEngine;
    this.mailSender = mailSender;
    this.messageSource = messageSource;
  }

  public void processDigest() {
    Pageable page = PageRequest.of(0, 50);
    Page<Article> articles = articleRepo.findByStatusAndDisplayWithEagerRelationships(Status.COMPLETED, false, page);
    List<Subscribe> subscribers = subscribeRepo.findAll();
    String baseUrl = jhipster.getMail().getBaseUrl();
    for (Subscribe s : subscribers) {
      Locale locale = Locale.forLanguageTag(Optional.ofNullable(s.getLangKey()).orElse("fr"));
      Context ctx = new Context(locale);
      ctx.setVariable("articles", articles.getContent());
      ctx.setVariable("baseUrl", baseUrl);
      ctx.setVariable("subscribe", s);
      String content = templateEngine.process("mail/articlesDigestEmail", ctx);
      String subject = messageSource.getMessage("email.digest.title", null, locale);
      send(content, subject, s.getEmail());
    }
    List<Long> ids = articles.getContent().stream().map(Article::getId).toList();
    articleRepo.bulkSetDisplayTrue(ids);
  }

  private void send(String content, String subject, String to) {
    MimeMessage mime = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mime, true, StandardCharsets.UTF_8.name());
    helper.setTo(to);
    helper.setFrom(jhipster.getMail().getFrom());
    helper.setSubject(subject);
    helper.setText(content, true);
    mailSender.send(mime);
  }
}

```

- Méthodes repository à prévoir (sélection + update bulk):

```java
@Repository
public interface ArticleRepositoryV1 extends ArticleRepositoryWithBagRelationships, JpaRepository<Article, Long> {
  Page<Article> findByStatusAndDisplay(Status status, Boolean display, Pageable pageable);

  default Page<Article> findByStatusAndDisplayWithEagerRelationships(Status status, Boolean display, Pageable pageable) {
    return this.fetchBagRelationships(this.findByStatusAndDisplay(status, display, pageable));
  }

  @Modifying
  @Query("update Article a set a.display = true where a.id in :ids")
  int bulkSetDisplayTrue(@Param("ids") List<Long> ids);
}

```

- Construction de slug et d’URL:

```java
private String slug(String s) {
  String v = Optional.ofNullable(s).orElse("").toLowerCase(Locale.ROOT);
  v = java.text.Normalizer.normalize(v, java.text.Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
  v = v.replaceAll("[^a-z0-9]+", "-").replaceAll("^-+|-+$", "");
  return v.length() > 80 ? v.substring(0, 80) : v;
}

private String articleUrl(String baseUrl, Article a, boolean useFr) {
  String title = useFr ? a.getLabelFr() : a.getLabelEn();
  return baseUrl + "/v1/articles/" + a.getId() + "-" + slug(title) + "/view";
}

```

- Snippet Thymeleaf (extrait articlesDigestEmail.html):

```html
<ul>
  <li th:each="a : ${articles}">
    <a th:href="${baseUrl} + '/v1/articles/' + ${a.id} + '/view'">
      <span th:text="${#locale.language == 'fr' ? a.labelFr : a.labelEn}"></span>
    </a>
    <p th:text="${#locale.language == 'fr' ? a.descriptionFr : a.descriptionEn}"></p>
    <time th:text="${#temporals.format(a.date, 'yyyy-MM-dd')}"></time>
  </li>
</ul>
```

- Article: [Article.java](src/main/java/com/devalgas/blog/domain/Article.java)
- Repository: [ArticleRepositoryV1.java](src/main/java/com/devalgas/blog/repository/v1/ArticleRepositoryV1.java)
- Subscribe: [Subscribe.java](src/main/java/com/devalgas/blog/domain/Subscribe.java), [SubscribeRepositoryV1.java](src/main/java/com/devalgas/blog/repository/v1/SubscribeRepositoryV1.java)
- Service Mail: [MailServiceImplV1.java](src/main/java/com/devalgas/blog/service/impl/v1/MailServiceImplV1.java), [MailService.java](src/main/java/com/devalgas/blog/service/MailService.java)
- Template de référence: [subscribeEmail.html](src/main/resources/templates/mail/subscribeEmail.html)

Rappel important:

- Ne produire aucune implémentation dans cette CTD; uniquement design, choix techniques, blocs de config exacts, et plan de tests.
