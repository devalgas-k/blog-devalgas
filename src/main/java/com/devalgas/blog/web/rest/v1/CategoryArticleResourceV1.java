package com.devalgas.blog.web.rest.v1;

import com.devalgas.blog.service.dto.v1.CategoryArticleHomeV1DTO;
import com.devalgas.blog.service.dto.v1.CategoryArticleSummaryDTOV1;
import com.devalgas.blog.service.v1.CategoryArticleServiceV1;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/summary")
    /**
     * {@code GET  /category-articles/summary} : get category articles summary.
     *
     * Retrieves all categories with a basic summary of their related articles.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of category summaries in body.
     */
    public ResponseEntity<List<CategoryArticleHomeV1DTO>> findAllSummaryV1() {
        log.debug("REST request to get CategoryArticles summary");
        List<CategoryArticleHomeV1DTO> content = categoryArticleServiceV1.findAllCategoriesArticleHome(Pageable.unpaged()).getContent();
        return ResponseEntity.ok().body(content);
    }
}
