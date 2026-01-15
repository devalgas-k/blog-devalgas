package com.devalgas.blog.web.rest.v1;

import com.devalgas.blog.service.ArticleService;
import com.devalgas.blog.service.dto.ArticleDTO;
import com.devalgas.blog.service.dto.v1.ArticleDetailsBasicDTOV1;
import com.devalgas.blog.service.dto.v1.ArticleHomeV1DTO;
import com.devalgas.blog.service.v1.ArticleServiceV1;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;

/**
 * REST controller for managing {@link com.devalgas.blog.domain.Article}.
 */
@RestController
@RequestMapping("/api/v1/articles")
public class ArticleResourceV1 {

    private static final Logger log = LoggerFactory.getLogger(ArticleResourceV1.class);

    private final ArticleService articleService;
    private final ArticleServiceV1 articleServiceV1;

    public ArticleResourceV1(ArticleService articleService, ArticleServiceV1 articleServiceV1) {
        this.articleService = articleService;
        this.articleServiceV1 = articleServiceV1;
    }

    /**
     * {@code GET  /v1/articles/:id} : get the "id" article.
     *
     * @param id the id of the articleDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the articleDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ArticleDTO> getArticle(@PathVariable("id") Long id) {
        log.debug("REST request to get Article : {}", id);
        ArticleDTO articleDTO = articleService.findOne(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok().body(articleDTO);
    }

    /**
     * {@code GET  /articles} : get all the articles.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of articles in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ArticleDTO>> getAllArticles(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get a page of Articles");
        Page<ArticleDTO> page;
        if (eagerload) {
            page = articleService.findAllWithEagerRelationships(pageable);
        } else {
            page = articleService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        // Thread.getAllStackTraces();
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /articles/summary} : get projected summary of articles.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of projected articles in body.
     */
    @GetMapping("/summary")
    public ResponseEntity<List<ArticleHomeV1DTO>> getAllArticlesV1(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of optimized Articles V1");
        Page<ArticleHomeV1DTO> page = articleServiceV1.findAllArticlesHome(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/summary/{id}")
    /**
     * {@code GET  /articles/summary/:id} : get optimized article details.
     *
     * Retrieves an optimized projection of an article with minimal fields, optionally localized.
     *
     * @param id the id of the article to retrieve.
     * @param lang optional language code ("fr" or "en") to select localized fields.
     * @param acceptLanguage optional HTTP header used to resolve language when {@code lang} is not provided.
     * @param ifNoneMatch optional ETag header used to return {@code 304 (Not Modified)} when content hasn't changed.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the optimized article details,
     *         or with status {@code 304 (Not Modified)} when ETag matches, or {@code 404 (Not Found)} when the article doesn't exist.
     */
    public ResponseEntity<ArticleDetailsBasicDTOV1> getArticleV1(
        @PathVariable("id") Long id,
        @RequestParam(value = "lang", required = false) String lang,
        @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage
    ) {
        log.debug("REST request to get optimized Article V1 : {}, lang={}", id, lang);
        ArticleDetailsBasicDTOV1 article = articleServiceV1
            .findOneDetailsBasicProjectedV1(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok().body(article);
    }
}
