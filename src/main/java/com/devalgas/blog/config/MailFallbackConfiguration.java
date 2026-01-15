package com.devalgas.blog.config;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@ConditionalOnProperty(name = "fallback.mail.enabled", havingValue = "true")
public class MailFallbackConfiguration {

    @Bean(name = "fallbackJavaMailSender")
    public JavaMailSender fallbackJavaMailSender(
        @Value("${fallback.mail.host:}") String host,
        @Value("${fallback.mail.port:587}") int port,
        @Value("${fallback.mail.username:}") String username,
        @Value("${fallback.mail.password:}") String password,
        @Value("${spring.mail.properties.mail.smtp.auth:true}") boolean smtpAuth,
        @Value("${spring.mail.properties.mail.smtp.starttls.enable:true}") boolean startTls
    ) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(host);
        sender.setPort(port);
        sender.setUsername(username);
        sender.setPassword(password);
        Properties props = new Properties();
        props.setProperty("mail.smtp.auth", Boolean.toString(smtpAuth));
        props.setProperty("mail.smtp.starttls.enable", Boolean.toString(startTls));
        sender.setJavaMailProperties(props);
        return sender;
    }
}
