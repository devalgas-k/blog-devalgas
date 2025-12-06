package com.devalgas.blog.service;

import com.devalgas.blog.service.dto.CategoryArticleDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.devalgas.blog.domain.CategoryArticle}.
 */
public interface CategoryArticleService {
    /**
     * Save a categoryArticle.
     *
     * @param categoryArticleDTO the entity to save.
     * @return the persisted entity.
     */
    CategoryArticleDTO save(CategoryArticleDTO categoryArticleDTO);

    /**
     * Updates a categoryArticle.
     *
     * @param categoryArticleDTO the entity to update.
     * @return the persisted entity.
     */
    CategoryArticleDTO update(CategoryArticleDTO categoryArticleDTO);

    /**
     * Partially updates a categoryArticle.
     *
     * @param categoryArticleDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CategoryArticleDTO> partialUpdate(CategoryArticleDTO categoryArticleDTO);

    /**
     * Get all the categoryArticles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CategoryArticleDTO> findAll(Pageable pageable);

    /**
     * Get the "id" categoryArticle.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CategoryArticleDTO> findOne(Long id);

    /**
     * Delete the "id" categoryArticle.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
