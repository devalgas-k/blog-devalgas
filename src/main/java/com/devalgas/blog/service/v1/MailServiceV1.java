package com.devalgas.blog.service.v1;

import com.devalgas.blog.service.dto.SubscribeDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import tech.jhipster.config.JHipsterProperties;

/**
 * Service for sending emails asynchronously.
 * <p>
 * We use the {@link Async} annotation to send emails asynchronously.
 */
@Service
public class MailServiceV1 {

    private static final Logger LOG = LoggerFactory.getLogger(MailServiceV1.class);

    private static final String SUBSCRIBE = "subscribe";

    private final JHipsterProperties jHipsterProperties;

    private final JavaMailSender javaMailSender;

    private final MessageSource messageSource;

    private final SpringTemplateEngine templateEngine;

    public MailServiceV1(
        JHipsterProperties jHipsterProperties,
        JavaMailSender javaMailSender,
        MessageSource messageSource,
        SpringTemplateEngine templateEngine
    ) {
        this.jHipsterProperties = jHipsterProperties;
        this.javaMailSender = javaMailSender;
        this.messageSource = messageSource;
        this.templateEngine = templateEngine;
    }

    @Async
    /**
     * Send an email asynchronously.
     *
     * @param to the recipient email address.
     * @param subject the email subject.
     * @param content the email content.
     * @param isMultipart whether the email is multipart.
     * @param isHtml whether the content is HTML.
     */
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        this.sendEmailSync(to, subject, content, isMultipart, isHtml);
    }

    private void sendEmailSync(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        LOG.debug(
            "Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
            isMultipart,
            isHtml,
            to,
            subject,
            content
        );

        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setFrom(jHipsterProperties.getMail().getFrom());
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
            LOG.debug("Sent email to User '{}'", to);
        } catch (MailException | MessagingException e) {
            LOG.warn("Email could not be sent to user '{}'", to, e);
        }
    }

    @Async
    /**
     * Send the confirmation email for a new subscribe.
     *
     * @param subscribe the subscribe DTO containing email and localization info.
     */
    public void sendEmailNewSubscribe(SubscribeDTO subscribe) {
        LOG.debug("Sending confirmation email to '{}'", subscribe.getEmail());
        this.sendEmailFromTemplateSync(subscribe, "/mail/subscribeEmail.html", "email.subscribe.title");
    }

    @Async
    /**
     * Send an email using a template, asynchronously.
     *
     * @param subscribe the subscribe DTO containing email and localization info.
     * @param templateName the Thymeleaf template path.
     * @param titleKey the i18n key for the email subject.
     */
    public void sendEmailFromTemplate(SubscribeDTO subscribe, String templateName, String titleKey) {
        this.sendEmailFromTemplateSync(subscribe, templateName, titleKey);
    }

    private void sendEmailFromTemplateSync(SubscribeDTO subscribe, String templateName, String titleKey) {
        if (subscribe.getEmail() == null) {
            LOG.debug("Email doesn't exist for subscribe '{}'", subscribe.getEmail());
            return;
        }

        Locale locale = Locale.forLanguageTag(subscribe.getLangKey());
        Context context = new Context(locale);
        context.setVariable(SUBSCRIBE, subscribe);
        String content = templateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, null, locale);
        this.sendEmailSync(subscribe.getEmail(), subject, content, false, true);
    }
}
