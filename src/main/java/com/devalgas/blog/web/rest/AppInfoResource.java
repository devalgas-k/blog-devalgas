package com.devalgas.blog.web.rest;

import com.devalgas.blog.repository.AppInfoRepository;
import com.devalgas.blog.service.AppInfoService;
import com.devalgas.blog.service.dto.AppInfoDTO;
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
 * REST controller for managing {@link com.devalgas.blog.domain.AppInfo}.
 */
@RestController
@RequestMapping("/api/app-infos")
public class AppInfoResource {

    private static final Logger LOG = LoggerFactory.getLogger(AppInfoResource.class);

    private static final String ENTITY_NAME = "appInfo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AppInfoService appInfoService;

    private final AppInfoRepository appInfoRepository;

    public AppInfoResource(AppInfoService appInfoService, AppInfoRepository appInfoRepository) {
        this.appInfoService = appInfoService;
        this.appInfoRepository = appInfoRepository;
    }

    /**
     * {@code POST  /app-infos} : Create a new appInfo.
     *
     * @param appInfoDTO the appInfoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new appInfoDTO, or with status {@code 400 (Bad Request)} if the appInfo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AppInfoDTO> createAppInfo(@RequestBody AppInfoDTO appInfoDTO) throws URISyntaxException {
        LOG.debug("REST request to save AppInfo : {}", appInfoDTO);
        if (appInfoDTO.getId() != null) {
            throw new BadRequestAlertException("A new appInfo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        appInfoDTO = appInfoService.save(appInfoDTO);
        return ResponseEntity.created(new URI("/api/app-infos/" + appInfoDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, appInfoDTO.getId().toString()))
            .body(appInfoDTO);
    }

    /**
     * {@code PUT  /app-infos/:id} : Updates an existing appInfo.
     *
     * @param id the id of the appInfoDTO to save.
     * @param appInfoDTO the appInfoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated appInfoDTO,
     * or with status {@code 400 (Bad Request)} if the appInfoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the appInfoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AppInfoDTO> updateAppInfo(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AppInfoDTO appInfoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AppInfo : {}, {}", id, appInfoDTO);
        if (appInfoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, appInfoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!appInfoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        appInfoDTO = appInfoService.update(appInfoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, appInfoDTO.getId().toString()))
            .body(appInfoDTO);
    }

    /**
     * {@code PATCH  /app-infos/:id} : Partial updates given fields of an existing appInfo, field will ignore if it is null
     *
     * @param id the id of the appInfoDTO to save.
     * @param appInfoDTO the appInfoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated appInfoDTO,
     * or with status {@code 400 (Bad Request)} if the appInfoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the appInfoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the appInfoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AppInfoDTO> partialUpdateAppInfo(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AppInfoDTO appInfoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update AppInfo partially : {}, {}", id, appInfoDTO);
        if (appInfoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, appInfoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!appInfoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AppInfoDTO> result = appInfoService.partialUpdate(appInfoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, appInfoDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /app-infos} : get all the appInfos.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of appInfos in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AppInfoDTO>> getAllAppInfos(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of AppInfos");
        Page<AppInfoDTO> page = appInfoService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /app-infos/:id} : get the "id" appInfo.
     *
     * @param id the id of the appInfoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the appInfoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AppInfoDTO> getAppInfo(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AppInfo : {}", id);
        Optional<AppInfoDTO> appInfoDTO = appInfoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(appInfoDTO);
    }

    /**
     * {@code DELETE  /app-infos/:id} : delete the "id" appInfo.
     *
     * @param id the id of the appInfoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppInfo(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AppInfo : {}", id);
        appInfoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
