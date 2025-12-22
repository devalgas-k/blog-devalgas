package com.devalgas.blog.service.v1;

import com.devalgas.blog.service.dto.SubscribeDTO;

/**
 * Service Interface for managing {@link com.devalgas.blog.domain.Subscribe}.
 */
public interface SubscribeServiceV1 {
    /**
     * Save a subscribe.
     *
     * @param subscribeDTO the entity to register.
     * @return the persisted entity.
     */
    SubscribeDTO registerSubscribe(SubscribeDTO subscribeDTO);
}
