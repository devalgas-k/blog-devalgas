package com.devalgas.blog.web.rest;

import com.devalgas.blog.repository.CategoryArticleRepository;
import com.devalgas.blog.service.CategoryArticleService;
import com.devalgas.blog.service.dto.CategoryArticleDTO;
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
 * REST controller for managing {@link com.devalgas.blog.domain.CategoryArticle}.
 */
@RestController
@RequestMapping("/api/category-articles")
public class CategoryArticleResource {

    private static final Logger LOG = LoggerFactory.getLogger(CategoryArticleResource.class);

    private static final String ENTITY_NAME = "categoryArticle";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CategoryArticleService categoryArticleService;

    private final CategoryArticleRepository categoryArticleRepository;

    public CategoryArticleResource(CategoryArticleService categoryArticleService, CategoryArticleRepository categoryArticleRepository) {
        this.categoryArticleService = categoryArticleService;
        this.categoryArticleRepository = categoryArticleRepository;
    }

    /**
     * {@code POST  /category-articles} : Create a new categoryArticle.
     *
     * @param categoryArticleDTO the categoryArticleDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new categoryArticleDTO, or with status {@code 400 (Bad Request)} if the categoryArticle has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CategoryArticleDTO> createCategoryArticle(@Valid @RequestBody CategoryArticleDTO categoryArticleDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save CategoryArticle : {}", categoryArticleDTO);
        if (categoryArticleDTO.getId() != null) {
            throw new BadRequestAlertException("A new categoryArticle cannot already have an ID", ENTITY_NAME, "idexists");
        }
        categoryArticleDTO = categoryArticleService.save(categoryArticleDTO);
        return ResponseEntity.created(new URI("/api/category-articles/" + categoryArticleDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, categoryArticleDTO.getId().toString()))
            .body(categoryArticleDTO);
    }

    /**
     * {@code PUT  /category-articles/:id} : Updates an existing categoryArticle.
     *
     * @param id the id of the categoryArticleDTO to save.
     * @param categoryArticleDTO the categoryArticleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated categoryArticleDTO,
     * or with status {@code 400 (Bad Request)} if the categoryArticleDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the categoryArticleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryArticleDTO> updateCategoryArticle(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CategoryArticleDTO categoryArticleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CategoryArticle : {}, {}", id, categoryArticleDTO);
        if (categoryArticleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, categoryArticleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!categoryArticleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        categoryArticleDTO = categoryArticleService.update(categoryArticleDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, categoryArticleDTO.getId().toString()))
            .body(categoryArticleDTO);
    }

    /**
     * {@code PATCH  /category-articles/:id} : Partial updates given fields of an existing categoryArticle, field will ignore if it is null
     *
     * @param id the id of the categoryArticleDTO to save.
     * @param categoryArticleDTO the categoryArticleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated categoryArticleDTO,
     * or with status {@code 400 (Bad Request)} if the categoryArticleDTO is not valid,
     * or with status {@code 404 (Not Found)} if the categoryArticleDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the categoryArticleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CategoryArticleDTO> partialUpdateCategoryArticle(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CategoryArticleDTO categoryArticleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CategoryArticle partially : {}, {}", id, categoryArticleDTO);
        if (categoryArticleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, categoryArticleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!categoryArticleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CategoryArticleDTO> result = categoryArticleService.partialUpdate(categoryArticleDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, categoryArticleDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /category-articles} : get all the categoryArticles.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of categoryArticles in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CategoryArticleDTO>> getAllCategoryArticles(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of CategoryArticles");
        Page<CategoryArticleDTO> page = categoryArticleService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /category-articles/:id} : get the "id" categoryArticle.
     *
     * @param id the id of the categoryArticleDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the categoryArticleDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryArticleDTO> getCategoryArticle(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CategoryArticle : {}", id);
        Optional<CategoryArticleDTO> categoryArticleDTO = categoryArticleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(categoryArticleDTO);
    }

    /**
     * {@code DELETE  /category-articles/:id} : delete the "id" categoryArticle.
     *
     * @param id the id of the categoryArticleDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategoryArticle(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CategoryArticle : {}", id);
        categoryArticleService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
