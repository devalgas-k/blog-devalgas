package com.devalgas.blog.service.v1;

import com.devalgas.blog.service.dto.ArticleDTO;
import com.devalgas.blog.service.dto.v1.ArticleDetailV1DTO;
import com.devalgas.blog.service.dto.v1.ArticleHomeV1DTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.devalgas.blog.domain.Article}.
 */
public interface ArticleServiceV1 {
    /**
     * Delete an article by id and evict related caches.
     *
     * @param id the id of the article to delete.
     */
    void delete(Long id);
    /**
     * Partially update an article.
     *
     * @param articleDTO the article data with fields to update.
     * @return an optional containing the updated article, or empty if not found.
     */
    Optional<ArticleDTO> partialUpdate(ArticleDTO articleDTO);
    /**
     * Update an existing article.
     *
     * @param articleDTO the article data to update.
     * @return the updated article.
     */
    ArticleDTO update(ArticleDTO articleDTO);
    /**
     * Save a new article.
     *
     * @param articleDTO the article data to save.
     * @return the persisted article.
     */
    ArticleDTO save(ArticleDTO articleDTO);

    Page<ArticleHomeV1DTO> findAllArticlesHome(Pageable pageable);

    /**
     * Get the "id" article.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ArticleDetailV1DTO> findOneArticleDetails(Long id);
}
