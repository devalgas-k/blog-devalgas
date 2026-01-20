package com.devalgas.blog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Devalgas.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final Liquibase liquibase = new Liquibase();
    private final Recaptcha recaptcha = new Recaptcha();
    private final Mail mail = new Mail();
    private final Contact contact = new Contact();

    // jhipster-needle-application-properties-property

    public Liquibase getLiquibase() {
        return liquibase;
    }

    // jhipster-needle-application-properties-property-getter
    public Recaptcha getRecaptcha() {
        return recaptcha;
    }

    public Mail getMail() {
        return mail;
    }

    public Contact getContact() {
        return contact;
    }

    public static class Liquibase {

        private Boolean asyncStart = true;

        public Boolean getAsyncStart() {
            return asyncStart;
        }

        public void setAsyncStart(Boolean asyncStart) {
            this.asyncStart = asyncStart;
        }
    }

    // jhipster-needle-application-properties-property-class

    public static class Recaptcha {

        private Boolean enabled = true;
        private String secret;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }
    }

    public static class Mail {

        private String contactFrom;

        public String getContactFrom() {
            return contactFrom;
        }

        public void setContactFrom(String contactFrom) {
            this.contactFrom = contactFrom;
        }
    }

    public static class Contact {

        private String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
