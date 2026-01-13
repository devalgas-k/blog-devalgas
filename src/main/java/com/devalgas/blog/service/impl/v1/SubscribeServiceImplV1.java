package com.devalgas.blog.service.impl.v1;

import com.devalgas.blog.domain.Subscribe;
import com.devalgas.blog.repository.v1.SubscribeRepositoryV1;
import com.devalgas.blog.service.EmailAlreadyUsedException;
import com.devalgas.blog.service.dto.SubscribeDTO;
import com.devalgas.blog.service.mapper.SubscribeMapper;
import com.devalgas.blog.service.v1.SubscribeServiceV1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Subscribe}.
 */
@Service
@Transactional
public class SubscribeServiceImplV1 implements SubscribeServiceV1 {

    private static final Logger LOG = LoggerFactory.getLogger(SubscribeServiceImplV1.class);

    private final SubscribeRepositoryV1 subscribeRepository;

    private final SubscribeMapper subscribeMapper;

    public SubscribeServiceImplV1(SubscribeRepositoryV1 subscribeRepository, SubscribeMapper subscribeMapper) {
        this.subscribeRepository = subscribeRepository;
        this.subscribeMapper = subscribeMapper;
    }

    @Override
    public SubscribeDTO registerSubscribe(SubscribeDTO subscribeDTO) {
        LOG.debug("Request to register Subscribe : {}", subscribeDTO);

        subscribeRepository
            .findOneByEmailIgnoreCase(subscribeDTO.getEmail())
            .ifPresent(existingSubscribe -> {
                throw new EmailAlreadyUsedException();
            });

        Subscribe subscribe = subscribeMapper.toEntity(subscribeDTO);
        subscribe = subscribeRepository.save(subscribe);
        return subscribeMapper.toDto(subscribe);
    }
}
