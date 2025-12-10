package com.devalgas.blog.service.v1;

import com.devalgas.blog.service.dto.CategoryArticleDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.devalgas.blog.domain.CategoryArticle}.
 */
public interface CategoryArticleServiceV1 {
    /**
     * Get the "id" categoryArticle.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CategoryArticleDTO> findOneV1(Long id);

    /**
     * Get all the categoryArticles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CategoryArticleDTO> findAll(Pageable pageable);

    /**
     * Get all the categoryArticles with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CategoryArticleDTO> findAllWithEagerRelationshipsV1(Pageable pageable);
}
