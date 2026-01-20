package com.devalgas.blog.service.impl.v1;

import com.devalgas.blog.config.ApplicationProperties;
import com.devalgas.blog.service.dto.MessageDTO;
import com.devalgas.blog.service.dto.SubscribeDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
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
public class MailServiceImplV1 {

    private static final Logger LOG = LoggerFactory.getLogger(MailServiceImplV1.class);

    private static final String SUBSCRIBE = "subscribe";
    private static final String BASE_URL = "baseUrl";

    private final JHipsterProperties jHipsterProperties;

    private final JavaMailSender javaMailSender;

    private final MessageSource messageSource;

    private final SpringTemplateEngine templateEngine;
    private final FailoverMailSenderV1 failoverMailSender;
    private final ApplicationProperties applicationProperties;

    public MailServiceImplV1(
        JHipsterProperties jHipsterProperties,
        JavaMailSender javaMailSender,
        MessageSource messageSource,
        SpringTemplateEngine templateEngine,
        FailoverMailSenderV1 failoverMailSender,
        ApplicationProperties applicationProperties
    ) {
        this.jHipsterProperties = jHipsterProperties;
        this.javaMailSender = javaMailSender;
        this.messageSource = messageSource;
        this.templateEngine = templateEngine;
        this.failoverMailSender = failoverMailSender;
        this.applicationProperties = applicationProperties;
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
            message.setFrom(jHipsterProperties.getMail().getFrom(), "Devalgas.net");
            message.setSubject(subject);
            message.setText(content, isHtml);
            failoverMailSender.send(mimeMessage);
            LOG.debug("Sent email to User '{}'", to);
        } catch (MailException | MessagingException | UnsupportedEncodingException e) {
            LOG.warn("Email could not be sent to user '{}'", to, e);
            if (javaMailSender instanceof JavaMailSenderImpl impl) {
                LOG.warn("SMTP connection info: host='{}', port={}, username='{}'", impl.getHost(), impl.getPort(), impl.getUsername());
            }
        }
    }

    private void sendEmailSyncWithFrom(String to, String subject, String content, boolean isMultipart, boolean isHtml, String from) {
        LOG.debug(
            "Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
            isMultipart,
            isHtml,
            to,
            subject,
            content
        );
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            String effectiveFrom = from != null && !from.isBlank() ? from : jHipsterProperties.getMail().getFrom();
            message.setFrom(effectiveFrom, "Devalgas.net");
            message.setSubject(subject);
            message.setText(content, isHtml);
            failoverMailSender.send(mimeMessage);
            LOG.debug("Sent email to '{}'", to);
        } catch (MailException | MessagingException | UnsupportedEncodingException e) {
            LOG.warn("Email could not be sent to '{}'", to, e);
            if (javaMailSender instanceof JavaMailSenderImpl impl) {
                LOG.warn("SMTP connection info: host='{}', port={}, username='{}'", impl.getHost(), impl.getPort(), impl.getUsername());
            }
        }
    }

    private void sendEmailSyncWithFromAlt(String to, String subject, String plainText, String htmlContent, String from) {
        LOG.debug("Send email (alt) to '{}' with subject '{}'", to, subject);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
            message.setTo(to);
            String effectiveFrom = from != null && !from.isBlank() ? from : jHipsterProperties.getMail().getFrom();
            message.setFrom(effectiveFrom, "Devalgas.net");
            message.setSubject(subject);
            String pt = plainText != null ? plainText : "";
            String hc = htmlContent != null ? htmlContent : "";
            message.setText(pt, hc);
            failoverMailSender.send(mimeMessage);
            LOG.debug("Sent email to '{}'", to);
        } catch (MailException | MessagingException | UnsupportedEncodingException e) {
            LOG.warn("Email could not be sent to '{}'", to, e);
            if (javaMailSender instanceof JavaMailSenderImpl impl) {
                LOG.warn("SMTP connection info: host='{}', port={}, username='{}'", impl.getHost(), impl.getPort(), impl.getUsername());
            }
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
        this.sendEmailFromTemplateSync(subscribe, "mail/subscribeEmail", "email.subscribe.title");
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
        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
        String content = templateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, null, locale);
        this.sendEmailSync(subscribe.getEmail(), subject, content, false, true);
    }

    @Async
    public void sendEmailNewMessage(MessageDTO message) {
        LOG.debug("Sending contact message email for '{}'", message.getEmail());
        String to = message.getEmail();
        if (to == null || to.isBlank()) {
            LOG.debug("Skip sending contact confirmation: recipient email missing");
            return;
        }
        String lang = message.getLangKey();
        Locale locale = (lang != null && !lang.isBlank()) ? Locale.forLanguageTag(lang) : Locale.ENGLISH;
        Context context = new Context(locale);
        context.setVariable("message", message);
        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
        String content = templateEngine.process("mail/contactMessageEmail", context);
        String subject = messageSource.getMessage("email.message.title", null, "Confirmation of receipt", locale);
        String from = applicationProperties.getMail() != null ? applicationProperties.getMail().getContactFrom() : null;
        String welcome = messageSource.getMessage("email.message.welcome", null, "Hello,", locale);
        String text1 = messageSource.getMessage(
            "email.message.text1",
            null,
            "Thank you for your message 🌟 \n We have received it and will respond as soon as possible.",
            locale
        );
        String regards = messageSource.getMessage("email.activation.text2", null, "Regards, ", locale);
        String signature = messageSource.getMessage("email.signature", null, "devalgas Team.", locale);
        String plain = welcome + "\n" + text1 + "\n\n" + regards + "\n" + signature;
        if (content == null || content.isBlank()) {
            StringBuilder sb = new StringBuilder();
            sb.append("<!doctype html><html><body>");
            sb.append("<p>").append(welcome).append("</p>");
            sb.append("<p style=\"white-space: pre-line\">").append(text1).append("</p>");
            sb.append("<p>").append(regards).append("<br/><em>").append(signature).append("</em></p>");
            sb.append("</body></html>");
            content = sb.toString();
        }
        this.sendEmailSyncWithFromAlt(to, subject, plain, content, from);
    }

    @Async
    public void sendEmailNewMessageNotification(MessageDTO message) {
        String to = applicationProperties.getContact() != null ? applicationProperties.getContact().getEmail() : null;
        if (to == null || to.isBlank()) {
            LOG.debug("Skip sending admin notification: contact email missing");
            return;
        }
        String lang = message.getLangKey();
        Locale locale = (lang != null && !lang.isBlank()) ? Locale.forLanguageTag(lang) : Locale.ENGLISH;
        Context context = new Context(locale);
        String idVal = message.getId() != null ? message.getId().toString() : "";
        String nameVal = message.getName() != null ? message.getName() : "";
        String emailVal = message.getEmail() != null ? message.getEmail() : "";
        String phoneVal = message.getPhone() != null ? message.getPhone() : "";
        String dateVal = message.getDate() != null ? message.getDate().toString() : "";
        String langVal = message.getLangKey() != null ? message.getLangKey() : "";
        String subjectFrVal = message.getSubject() != null ? message.getSubject().getTitleFr() : "";
        String bodyVal = message.getMessage() != null ? message.getMessage() : "";
        context.setVariable("id", idVal);
        context.setVariable("name", nameVal);
        context.setVariable("email", emailVal);
        context.setVariable("phone", phoneVal);
        context.setVariable("date", dateVal);
        context.setVariable("langKey", langVal);
        context.setVariable("subjectTitleFr", subjectFrVal);
        context.setVariable("messageBody", bodyVal);
        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
        String content = templateEngine.process("mail/contactNewMessageEmail", context);
        String subject = messageSource.getMessage("email.message.notify.title", null, "New message received", locale);
        String from = applicationProperties.getMail() != null ? applicationProperties.getMail().getContactFrom() : null;
        String plain =
            "Id: " +
            idVal +
            "\nNom: " +
            nameVal +
            "\nEmail: " +
            emailVal +
            "\nTel: " +
            phoneVal +
            "\nDate: " +
            dateVal +
            "\nCode langue: " +
            langVal +
            "\nObjet: " +
            subjectFrVal +
            "\nMessage: " +
            bodyVal;
        this.sendEmailSyncWithFromAlt(to, subject, plain, content, from);
    }
}
