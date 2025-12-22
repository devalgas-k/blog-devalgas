package com.devalgas.blog.service;

import com.devalgas.blog.service.dto.SubscribeDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.devalgas.blog.domain.Subscribe}.
 */
public interface SubscribeService {
    /**
     * Save a subscribe.
     *
     * @param subscribeDTO the entity to save.
     * @return the persisted entity.
     */
    SubscribeDTO save(SubscribeDTO subscribeDTO);

    /**
     * Updates a subscribe.
     *
     * @param subscribeDTO the entity to update.
     * @return the persisted entity.
     */
    SubscribeDTO update(SubscribeDTO subscribeDTO);

    /**
     * Partially updates a subscribe.
     *
     * @param subscribeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SubscribeDTO> partialUpdate(SubscribeDTO subscribeDTO);

    /**
     * Get all the subscribes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SubscribeDTO> findAll(Pageable pageable);

    /**
     * Get the "id" subscribe.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SubscribeDTO> findOne(Long id);

    /**
     * Delete the "id" subscribe.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
