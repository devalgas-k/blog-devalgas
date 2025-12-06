package com.devalgas.blog.web.rest;

import com.devalgas.blog.repository.FootersRepository;
import com.devalgas.blog.service.FootersService;
import com.devalgas.blog.service.dto.FootersDTO;
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
 * REST controller for managing {@link com.devalgas.blog.domain.Footers}.
 */
@RestController
@RequestMapping("/api/footers")
public class FootersResource {

    private static final Logger LOG = LoggerFactory.getLogger(FootersResource.class);

    private static final String ENTITY_NAME = "footers";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FootersService footersService;

    private final FootersRepository footersRepository;

    public FootersResource(FootersService footersService, FootersRepository footersRepository) {
        this.footersService = footersService;
        this.footersRepository = footersRepository;
    }

    /**
     * {@code POST  /footers} : Create a new footers.
     *
     * @param footersDTO the footersDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new footersDTO, or with status {@code 400 (Bad Request)} if the footers has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<FootersDTO> createFooters(@RequestBody FootersDTO footersDTO) throws URISyntaxException {
        LOG.debug("REST request to save Footers : {}", footersDTO);
        if (footersDTO.getId() != null) {
            throw new BadRequestAlertException("A new footers cannot already have an ID", ENTITY_NAME, "idexists");
        }
        footersDTO = footersService.save(footersDTO);
        return ResponseEntity.created(new URI("/api/footers/" + footersDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, footersDTO.getId().toString()))
            .body(footersDTO);
    }

    /**
     * {@code PUT  /footers/:id} : Updates an existing footers.
     *
     * @param id the id of the footersDTO to save.
     * @param footersDTO the footersDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated footersDTO,
     * or with status {@code 400 (Bad Request)} if the footersDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the footersDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<FootersDTO> updateFooters(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody FootersDTO footersDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Footers : {}, {}", id, footersDTO);
        if (footersDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, footersDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!footersRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        footersDTO = footersService.update(footersDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, footersDTO.getId().toString()))
            .body(footersDTO);
    }

    /**
     * {@code PATCH  /footers/:id} : Partial updates given fields of an existing footers, field will ignore if it is null
     *
     * @param id the id of the footersDTO to save.
     * @param footersDTO the footersDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated footersDTO,
     * or with status {@code 400 (Bad Request)} if the footersDTO is not valid,
     * or with status {@code 404 (Not Found)} if the footersDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the footersDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FootersDTO> partialUpdateFooters(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody FootersDTO footersDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Footers partially : {}, {}", id, footersDTO);
        if (footersDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, footersDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!footersRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FootersDTO> result = footersService.partialUpdate(footersDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, footersDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /footers} : get all the footers.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of footers in body.
     */
    @GetMapping("")
    public ResponseEntity<List<FootersDTO>> getAllFooters(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Footers");
        Page<FootersDTO> page = footersService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /footers/:id} : get the "id" footers.
     *
     * @param id the id of the footersDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the footersDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FootersDTO> getFooters(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Footers : {}", id);
        Optional<FootersDTO> footersDTO = footersService.findOne(id);
        return ResponseUtil.wrapOrNotFound(footersDTO);
    }

    /**
     * {@code DELETE  /footers/:id} : delete the "id" footers.
     *
     * @param id the id of the footersDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFooters(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Footers : {}", id);
        footersService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
