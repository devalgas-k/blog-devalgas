package com.devalgas.blog.service;

import com.devalgas.blog.service.dto.AppInfoDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.devalgas.blog.domain.AppInfo}.
 */
public interface AppInfoService {
    /**
     * Save a appInfo.
     *
     * @param appInfoDTO the entity to save.
     * @return the persisted entity.
     */
    AppInfoDTO save(AppInfoDTO appInfoDTO);

    /**
     * Updates a appInfo.
     *
     * @param appInfoDTO the entity to update.
     * @return the persisted entity.
     */
    AppInfoDTO update(AppInfoDTO appInfoDTO);

    /**
     * Partially updates a appInfo.
     *
     * @param appInfoDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AppInfoDTO> partialUpdate(AppInfoDTO appInfoDTO);

    /**
     * Get all the appInfos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AppInfoDTO> findAll(Pageable pageable);

    /**
     * Get the "id" appInfo.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AppInfoDTO> findOne(Long id);

    /**
     * Delete the "id" appInfo.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
