package com.devalgas.blog.service.security;

import com.devalgas.blog.config.ApplicationProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class RecaptchaService {

    private static final Logger LOG = LoggerFactory.getLogger(RecaptchaService.class);
    private static final URI VERIFY_URI = URI.create("https://www.google.com/recaptcha/api/siteverify");

    private final ApplicationProperties applicationProperties;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final Environment environment;

    public RecaptchaService(ApplicationProperties applicationProperties, ObjectMapper objectMapper, Environment environment) {
        this.applicationProperties = applicationProperties;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder().build();
        this.environment = environment;
    }

    public boolean verify(String token) {
        if (!environment.acceptsProfiles("prod")) {
            return true;
        }
        ApplicationProperties.Recaptcha cfg = applicationProperties.getRecaptcha();
        boolean enabled = cfg != null && Boolean.TRUE.equals(cfg.getEnabled());
        String secret = cfg != null ? cfg.getSecret() : null;
        if (!enabled) {
            return true;
        }
        if (!StringUtils.hasText(secret)) {
            LOG.warn("reCAPTCHA verification enabled but secret not configured");
            return false;
        }
        if (!StringUtils.hasText(token)) {
            return false;
        }
        String body = "secret=" + urlEncode(secret) + "&response=" + urlEncode(token);
        HttpRequest request = HttpRequest.newBuilder(VERIFY_URI)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
            .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() != 200) {
                LOG.warn("reCAPTCHA verify HTTP {}", response.statusCode());
                return false;
            }
            JsonNode node = objectMapper.readTree(Objects.requireNonNullElse(response.body(), ""));
            JsonNode successNode = node.get("success");
            boolean success = successNode != null && successNode.asBoolean(false);
            if (!success) {
                LOG.warn("reCAPTCHA verify failed: {}", node);
            }
            return success;
        } catch (Exception e) {
            LOG.error("reCAPTCHA verify error", e);
            return false;
        }
    }

    private static String urlEncode(String s) {
        return java.net.URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
