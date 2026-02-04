package com.devalgas.blog.service.v1;

import com.devalgas.blog.service.dto.v1.CategoryArticleHomeV1DTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.devalgas.blog.domain.CategoryArticle}.
 */
public interface CategoryArticleServiceV1 {
    /**
     * Get all categories with basic summary objects of their articles.
     *
     * @return the list of category summaries with minimal article information.
     */
    Page<CategoryArticleHomeV1DTO> findAllCategoriesArticleHome(Pageable pageable);
}
