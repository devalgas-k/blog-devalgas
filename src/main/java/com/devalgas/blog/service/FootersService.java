package com.devalgas.blog.service;

import com.devalgas.blog.service.dto.FootersDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.devalgas.blog.domain.Footers}.
 */
public interface FootersService {
    /**
     * Save a footers.
     *
     * @param footersDTO the entity to save.
     * @return the persisted entity.
     */
    FootersDTO save(FootersDTO footersDTO);

    /**
     * Updates a footers.
     *
     * @param footersDTO the entity to update.
     * @return the persisted entity.
     */
    FootersDTO update(FootersDTO footersDTO);

    /**
     * Partially updates a footers.
     *
     * @param footersDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<FootersDTO> partialUpdate(FootersDTO footersDTO);

    /**
     * Get all the footers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<FootersDTO> findAll(Pageable pageable);

    /**
     * Get the "id" footers.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<FootersDTO> findOne(Long id);

    /**
     * Delete the "id" footers.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
