package com.devalgas.blog.service;

import com.devalgas.blog.service.dto.HeadersDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.devalgas.blog.domain.Headers}.
 */
public interface HeadersService {
    /**
     * Save a headers.
     *
     * @param headersDTO the entity to save.
     * @return the persisted entity.
     */
    HeadersDTO save(HeadersDTO headersDTO);

    /**
     * Updates a headers.
     *
     * @param headersDTO the entity to update.
     * @return the persisted entity.
     */
    HeadersDTO update(HeadersDTO headersDTO);

    /**
     * Partially updates a headers.
     *
     * @param headersDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<HeadersDTO> partialUpdate(HeadersDTO headersDTO);

    /**
     * Get all the headers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<HeadersDTO> findAll(Pageable pageable);

    /**
     * Get the "id" headers.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<HeadersDTO> findOne(Long id);

    /**
     * Delete the "id" headers.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
