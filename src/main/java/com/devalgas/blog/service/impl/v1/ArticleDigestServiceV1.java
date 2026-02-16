package com.devalgas.blog.service.impl.v1;

import com.devalgas.blog.domain.Article;
import com.devalgas.blog.domain.Subscribe;
import com.devalgas.blog.domain.enumeration.Status;
import com.devalgas.blog.repository.v1.ArticleRepositoryV1;
import com.devalgas.blog.repository.v1.SubscribeRepositoryV1;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import tech.jhipster.config.JHipsterProperties;

@Service
public class ArticleDigestServiceV1 {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleDigestServiceV1.class);

    private static final String TEMPLATE = "mail/articleDigestEmail";

    private final ArticleRepositoryV1 articleRepository;
    private final SubscribeRepositoryV1 subscribeRepository;
    private final JHipsterProperties jhipster;
    private final SpringTemplateEngine templateEngine;
    private final JavaMailSender mailSender;
    private final MessageSource messageSource;
    private final FailoverMailSenderV1 failoverMailSender;

    public ArticleDigestServiceV1(
        ArticleRepositoryV1 articleRepository,
        SubscribeRepositoryV1 subscribeRepository,
        JHipsterProperties jhipster,
        SpringTemplateEngine templateEngine,
        JavaMailSender mailSender,
        MessageSource messageSource,
        FailoverMailSenderV1 failoverMailSender
    ) {
        this.articleRepository = articleRepository;
        this.subscribeRepository = subscribeRepository;
        this.jhipster = jhipster;
        this.templateEngine = templateEngine;
        this.mailSender = mailSender;
        this.messageSource = messageSource;
        this.failoverMailSender = failoverMailSender;
    }

    public void processDigest() {
        String runId = UUID.randomUUID().toString();
        Article article = articleRepository
            .findFirstByStatusAndDisplayAndNewsletterWithEagerRelationships(Status.COMPLETED, true, false)
            .orElse(null);
        if (article == null) {
            LOG.info("ArticleDigest[{}] no article to send (COMPLETED, display=true, newsletter=false)", runId);
            return;
        }
        List<Subscribe> subscribers = subscribeRepository.findAll();
        if (subscribers.isEmpty()) {
            LOG.info("ArticleDigest[{}] no subscribers found, skipping send", runId);
            return;
        }
        String baseUrl = jhipster.getMail().getBaseUrl();

        boolean allSucceeded = true;
        int attempted = 0;
        int succeeded = 0;

        for (Subscribe s : subscribers) {
            attempted++;
            Locale locale = localeFor(s.getLangKey());
            boolean useFr = "fr".equalsIgnoreCase(locale.getLanguage());
            String title = useFr ? article.getLabelFr() : article.getLabelEn();
            String slug = slug(title);

            Context ctx = new Context(locale);
            ctx.setVariable("article", article);
            ctx.setVariable("baseUrl", baseUrl);
            ctx.setVariable("slug", slug);

            String html = templateEngine.process(TEMPLATE, ctx);
            String subject = messageSource.getMessage("email.digest.title", null, defaultSubject(locale), locale);
            try {
                send(html, subject, s.getEmail());
                succeeded++;
            } catch (RuntimeException ex) {
                allSucceeded = false;
                LOG.error("ArticleDigest[{}] failed to send to {}: {}", runId, s.getEmail(), ex.getMessage());
            }
        }

        LOG.info("ArticleDigest[{}] attempted={}, succeeded={}", runId, attempted, succeeded);
        if (allSucceeded) {
            article.setNewsletter(true);
            articleRepository.save(article);
            LOG.info("ArticleDigest[{}] newsletter flag set to true for article id={}", runId, article.getId());
        } else {
            LOG.warn("ArticleDigest[{}] not all messages were sent successfully; newsletter flag NOT updated", runId);
        }
    }

    private void send(String html, String subject, String to) {
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, StandardCharsets.UTF_8.name());
            helper.setTo(to);
            helper.setFrom(jhipster.getMail().getFrom());
            helper.setSubject(subject);
            helper.setText(html, true);
            failoverMailSender.send(mime);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to build or send mail", e);
        }
    }

    private static Locale localeFor(String langKey) {
        if (langKey == null || langKey.isBlank()) {
            return Locale.forLanguageTag("fr");
        }
        return Locale.forLanguageTag(langKey);
    }

    private static String defaultSubject(Locale locale) {
        return "fr".equalsIgnoreCase(locale.getLanguage()) ? "Nouvel article publié" : "New article published";
    }

    private static String slug(String input) {
        String v = Optional.ofNullable(input).orElse("").toLowerCase(Locale.ROOT);
        v = Normalizer.normalize(v, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        v = v.replaceAll("[^a-z0-9]+", "-").replaceAll("^-+|-+$", "");
        return v.length() > 80 ? v.substring(0, 80) : v;
    }
}
