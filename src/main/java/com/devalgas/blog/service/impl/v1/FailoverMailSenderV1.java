package com.devalgas.blog.service.impl.v1;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class FailoverMailSenderV1 {

    private static final Logger LOG = LoggerFactory.getLogger(FailoverMailSenderV1.class);

    private final JavaMailSender primaryMailSender;
    private final ObjectProvider<JavaMailSender> fallbackMailSenderProvider;

    public FailoverMailSenderV1(
        JavaMailSender primaryMailSender,
        @Qualifier("fallbackJavaMailSender") ObjectProvider<JavaMailSender> fallbackMailSenderProvider
    ) {
        this.primaryMailSender = primaryMailSender;
        this.fallbackMailSenderProvider = fallbackMailSenderProvider;
    }

    public void send(MimeMessage mimeMessage) {
        try {
            primaryMailSender.send(mimeMessage);
        } catch (MailException primaryException) {
            JavaMailSender fallback = fallbackMailSenderProvider.getIfAvailable();
            if (fallback != null) {
                try {
                    fallback.send(mimeMessage);
                    LOG.warn("Primary mail send failed, message sent via fallback provider");
                } catch (MailException fallbackException) {
                    LOG.error("Fallback mail send failed", fallbackException);
                    throw fallbackException;
                }
            } else {
                LOG.error("Primary mail send failed and no fallback provider configured", primaryException);
                throw primaryException;
            }
        }
    }
}
