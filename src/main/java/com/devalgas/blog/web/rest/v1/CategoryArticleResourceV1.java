package com.devalgas.blog.web.rest.v1;

import com.devalgas.blog.service.dto.CategoryArticleDTO;
import com.devalgas.blog.service.v1.CategoryArticleServiceV1;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.devalgas.blog.domain.CategoryArticle}.
 */
@RestController
@RequestMapping("/api/v1/category-articles")
public class CategoryArticleResourceV1 {

    private static final Logger log = LoggerFactory.getLogger(CategoryArticleResourceV1.class);

    private final CategoryArticleServiceV1 categoryArticleServiceV1;

    public CategoryArticleResourceV1(CategoryArticleServiceV1 categoryArticleServiceV1) {
        this.categoryArticleServiceV1 = categoryArticleServiceV1;
    }

    /**
     * {@code GET  /category-articles} : get all the categoryArticles.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of categoryArticles in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CategoryArticleDTO>> findAllWithEagerRelationshipsV1(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of CategoryArticles");
        Page<CategoryArticleDTO> page = categoryArticleServiceV1.findAllWithEagerRelationshipsV1(pageable);
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
        log.debug("REST request to get CategoryArticle : {}", id);
        Optional<CategoryArticleDTO> categoryArticleDTO = categoryArticleServiceV1.findOneV1(id);
        return ResponseUtil.wrapOrNotFound(categoryArticleDTO);
    }
}
