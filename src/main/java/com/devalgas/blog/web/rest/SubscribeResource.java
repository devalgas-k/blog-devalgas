package com.devalgas.blog.web.rest;

import com.devalgas.blog.repository.SubscribeRepository;
import com.devalgas.blog.service.SubscribeService;
import com.devalgas.blog.service.dto.SubscribeDTO;
import com.devalgas.blog.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.devalgas.blog.domain.Subscribe}.
 */
@RestController
@RequestMapping("/api/subscribes")
public class SubscribeResource {

    private static final Logger LOG = LoggerFactory.getLogger(SubscribeResource.class);

    private static final String ENTITY_NAME = "subscribe";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SubscribeService subscribeService;

    private final SubscribeRepository subscribeRepository;

    public SubscribeResource(SubscribeService subscribeService, SubscribeRepository subscribeRepository) {
        this.subscribeService = subscribeService;
        this.subscribeRepository = subscribeRepository;
    }

    /**
     * {@code POST  /subscribes} : Create a new subscribe.
     *
     * @param subscribeDTO the subscribeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new subscribeDTO, or with status {@code 400 (Bad Request)} if the subscribe has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SubscribeDTO> createSubscribe(@Valid @RequestBody SubscribeDTO subscribeDTO) throws URISyntaxException {
        LOG.debug("REST request to save Subscribe : {}", subscribeDTO);
        if (subscribeDTO.getId() != null) {
            throw new BadRequestAlertException("A new subscribe cannot already have an ID", ENTITY_NAME, "idexists");
        }
        subscribeDTO = subscribeService.save(subscribeDTO);
        return ResponseEntity.created(new URI("/api/subscribes/" + subscribeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, subscribeDTO.getId().toString()))
            .body(subscribeDTO);
    }

    /**
     * {@code PUT  /subscribes/:id} : Updates an existing subscribe.
     *
     * @param id the id of the subscribeDTO to save.
     * @param subscribeDTO the subscribeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated subscribeDTO,
     * or with status {@code 400 (Bad Request)} if the subscribeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the subscribeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SubscribeDTO> updateSubscribe(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SubscribeDTO subscribeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Subscribe : {}, {}", id, subscribeDTO);
        if (subscribeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, subscribeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!subscribeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        subscribeDTO = subscribeService.update(subscribeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, subscribeDTO.getId().toString()))
            .body(subscribeDTO);
    }

    /**
     * {@code PATCH  /subscribes/:id} : Partial updates given fields of an existing subscribe, field will ignore if it is null
     *
     * @param id the id of the subscribeDTO to save.
     * @param subscribeDTO the subscribeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated subscribeDTO,
     * or with status {@code 400 (Bad Request)} if the subscribeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the subscribeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the subscribeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SubscribeDTO> partialUpdateSubscribe(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SubscribeDTO subscribeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Subscribe partially : {}, {}", id, subscribeDTO);
        if (subscribeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, subscribeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!subscribeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SubscribeDTO> result = subscribeService.partialUpdate(subscribeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, subscribeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /subscribes} : get all the subscribes.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of subscribes in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SubscribeDTO>> getAllSubscribes(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Subscribes");
        Page<SubscribeDTO> page = subscribeService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /subscribes/:id} : get the "id" subscribe.
     *
     * @param id the id of the subscribeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the subscribeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SubscribeDTO> getSubscribe(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Subscribe : {}", id);
        Optional<SubscribeDTO> subscribeDTO = subscribeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(subscribeDTO);
    }

    /**
     * {@code DELETE  /subscribes/:id} : delete the "id" subscribe.
     *
     * @param id the id of the subscribeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscribe(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Subscribe : {}", id);
        subscribeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
