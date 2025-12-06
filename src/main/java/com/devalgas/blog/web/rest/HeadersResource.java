package com.devalgas.blog.web.rest;

import com.devalgas.blog.repository.HeadersRepository;
import com.devalgas.blog.service.HeadersService;
import com.devalgas.blog.service.dto.HeadersDTO;
import com.devalgas.blog.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.devalgas.blog.domain.Headers}.
 */
@RestController
@RequestMapping("/api/headers")
public class HeadersResource {

    private static final Logger LOG = LoggerFactory.getLogger(HeadersResource.class);

    private static final String ENTITY_NAME = "headers";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final HeadersService headersService;

    private final HeadersRepository headersRepository;

    public HeadersResource(HeadersService headersService, HeadersRepository headersRepository) {
        this.headersService = headersService;
        this.headersRepository = headersRepository;
    }

    /**
     * {@code POST  /headers} : Create a new headers.
     *
     * @param headersDTO the headersDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new headersDTO, or with status {@code 400 (Bad Request)} if the headers has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<HeadersDTO> createHeaders(@RequestBody HeadersDTO headersDTO) throws URISyntaxException {
        LOG.debug("REST request to save Headers : {}", headersDTO);
        if (headersDTO.getId() != null) {
            throw new BadRequestAlertException("A new headers cannot already have an ID", ENTITY_NAME, "idexists");
        }
        headersDTO = headersService.save(headersDTO);
        return ResponseEntity.created(new URI("/api/headers/" + headersDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, headersDTO.getId().toString()))
            .body(headersDTO);
    }

    /**
     * {@code PUT  /headers/:id} : Updates an existing headers.
     *
     * @param id the id of the headersDTO to save.
     * @param headersDTO the headersDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated headersDTO,
     * or with status {@code 400 (Bad Request)} if the headersDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the headersDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<HeadersDTO> updateHeaders(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody HeadersDTO headersDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Headers : {}, {}", id, headersDTO);
        if (headersDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, headersDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!headersRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        headersDTO = headersService.update(headersDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, headersDTO.getId().toString()))
            .body(headersDTO);
    }

    /**
     * {@code PATCH  /headers/:id} : Partial updates given fields of an existing headers, field will ignore if it is null
     *
     * @param id the id of the headersDTO to save.
     * @param headersDTO the headersDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated headersDTO,
     * or with status {@code 400 (Bad Request)} if the headersDTO is not valid,
     * or with status {@code 404 (Not Found)} if the headersDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the headersDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<HeadersDTO> partialUpdateHeaders(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody HeadersDTO headersDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Headers partially : {}, {}", id, headersDTO);
        if (headersDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, headersDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!headersRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<HeadersDTO> result = headersService.partialUpdate(headersDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, headersDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /headers} : get all the headers.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of headers in body.
     */
    @GetMapping("")
    public ResponseEntity<List<HeadersDTO>> getAllHeaders(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Headers");
        Page<HeadersDTO> page = headersService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /headers/:id} : get the "id" headers.
     *
     * @param id the id of the headersDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the headersDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<HeadersDTO> getHeaders(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Headers : {}", id);
        Optional<HeadersDTO> headersDTO = headersService.findOne(id);
        return ResponseUtil.wrapOrNotFound(headersDTO);
    }

    /**
     * {@code DELETE  /headers/:id} : delete the "id" headers.
     *
     * @param id the id of the headersDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHeaders(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Headers : {}", id);
        headersService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
