package com.devalgas.blog.web.rest.v1;

import com.devalgas.blog.service.MessageService;
import com.devalgas.blog.service.dto.MessageDTO;
import com.devalgas.blog.service.impl.v1.MailServiceImplV1;
import com.devalgas.blog.service.security.RecaptchaService;
import com.devalgas.blog.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;

/**
 * REST controller for managing {@link com.devalgas.blog.domain.Message}.
 */
@RestController
@RequestMapping("/api/v1/messages")
public class MessageResourceV1 {

    private static final Logger LOG = LoggerFactory.getLogger(MessageResourceV1.class);

    private static final String ENTITY_NAME = "message";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MessageService messageService;
    private final RecaptchaService recaptchaService;
    private final MailServiceImplV1 mailService;

    public MessageResourceV1(MessageService messageService, RecaptchaService recaptchaService, MailServiceImplV1 mailService) {
        this.messageService = messageService;
        this.recaptchaService = recaptchaService;
        this.mailService = mailService;
    }

    /**
     * {@code POST  /messages} : Create a new message.
     *
     * @param messageDTO the messageDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new messageDTO, or with status {@code 400 (Bad Request)} if the message has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MessageDTO> createMessage(@Valid @RequestBody MessageDTO messageDTO) throws URISyntaxException {
        LOG.debug("REST request to save Message : {}", messageDTO);
        if (messageDTO.getId() != null) {
            throw new BadRequestAlertException("A new message cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (!recaptchaService.verify(messageDTO.getRecaptchaToken())) {
            throw new BadRequestAlertException("Invalid reCAPTCHA", ENTITY_NAME, "recaptchaInvalid");
        }
        String acceptLanguage = org.springframework.web.context.request.RequestContextHolder.getRequestAttributes() instanceof
            org.springframework.web.context.request.ServletRequestAttributes attrs
            ? attrs.getRequest().getHeader("Accept-Language")
            : null;
        if (messageDTO.getLangKey() == null || messageDTO.getLangKey().isBlank()) {
            String resolved = null;
            if (acceptLanguage != null && !acceptLanguage.isBlank()) {
                String first = acceptLanguage.split(",")[0];
                String base = first.split("[-_]")[0].toLowerCase();
                if ("fr".equals(base)) {
                    resolved = "fr";
                } else if ("en".equals(base)) {
                    resolved = "en";
                }
            }
            if (resolved != null) {
                messageDTO.setLangKey(resolved);
            }
        }
        messageDTO = messageService.save(messageDTO);
        mailService.sendEmailNewMessage(messageDTO);
        mailService.sendEmailNewMessageNotification(messageDTO);
        return ResponseEntity.created(new URI("/api/messages/" + messageDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, messageDTO.getId().toString()))
            .body(messageDTO);
    }
}
