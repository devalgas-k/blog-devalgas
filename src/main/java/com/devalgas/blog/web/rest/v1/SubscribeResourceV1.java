package com.devalgas.blog.web.rest.v1;

import com.devalgas.blog.service.dto.SubscribeDTO;
import com.devalgas.blog.service.v1.MailServiceV1;
import com.devalgas.blog.service.v1.SubscribeServiceV1;
import com.devalgas.blog.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.jhipster.web.util.HeaderUtil;

/**
 * REST controller for managing {@link com.devalgas.blog.domain.Subscribe}.
 */
@RestController
@RequestMapping("/api/v1/subscribes")
public class SubscribeResourceV1 {

    private static final Logger LOG = LoggerFactory.getLogger(SubscribeResourceV1.class);

    private static final String ENTITY_NAME = "subscribe";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SubscribeServiceV1 subscribeService;

    private final MailServiceV1 mailService;

    public SubscribeResourceV1(SubscribeServiceV1 subscribeService, MailServiceV1 mailService) {
        this.subscribeService = subscribeService;
        this.mailService = mailService;
    }

    /**
     * {@code POST  /v1/public/subscribes} : Create a new subscribe for followers blog.
     *
     * @param subscribeDTO the subscribeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new subscribeDTO, or with status {@code 400 (Bad Request)} if the subscribe has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SubscribeDTO> registerSubscribe(@Valid @RequestBody SubscribeDTO subscribeDTO) throws URISyntaxException {
        LOG.debug("REST request to save Subscribe : {}", subscribeDTO);
        if (subscribeDTO.getId() != null) {
            throw new BadRequestAlertException("A new subscribe cannot already have an ID", ENTITY_NAME, "idexists");
        }
        subscribeDTO = subscribeService.registerSubscribe(subscribeDTO);
        mailService.sendEmailNewSubscribe(subscribeDTO);
        return ResponseEntity.created(new URI("/api/v1/public/subscribes/" + subscribeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, subscribeDTO.getId().toString()))
            .body(subscribeDTO);
    }
}
