package com.devalgas.blog.service.impl;

import com.devalgas.blog.domain.Subscribe;
import com.devalgas.blog.repository.SubscribeRepository;
import com.devalgas.blog.service.SubscribeService;
import com.devalgas.blog.service.dto.SubscribeDTO;
import com.devalgas.blog.service.mapper.SubscribeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.devalgas.blog.domain.Subscribe}.
 */
@Service
@Transactional
public class SubscribeServiceImpl implements SubscribeService {

    private static final Logger LOG = LoggerFactory.getLogger(SubscribeServiceImpl.class);

    private final SubscribeRepository subscribeRepository;

    private final SubscribeMapper subscribeMapper;

    public SubscribeServiceImpl(SubscribeRepository subscribeRepository, SubscribeMapper subscribeMapper) {
        this.subscribeRepository = subscribeRepository;
        this.subscribeMapper = subscribeMapper;
    }

    @Override
    public SubscribeDTO save(SubscribeDTO subscribeDTO) {
        LOG.debug("Request to save Subscribe : {}", subscribeDTO);
        Subscribe subscribe = subscribeMapper.toEntity(subscribeDTO);
        subscribe = subscribeRepository.save(subscribe);
        return subscribeMapper.toDto(subscribe);
    }

    @Override
    public SubscribeDTO update(SubscribeDTO subscribeDTO) {
        LOG.debug("Request to update Subscribe : {}", subscribeDTO);
        Subscribe subscribe = subscribeMapper.toEntity(subscribeDTO);
        subscribe = subscribeRepository.save(subscribe);
        return subscribeMapper.toDto(subscribe);
    }

    @Override
    public Optional<SubscribeDTO> partialUpdate(SubscribeDTO subscribeDTO) {
        LOG.debug("Request to partially update Subscribe : {}", subscribeDTO);

        return subscribeRepository
            .findById(subscribeDTO.getId())
            .map(existingSubscribe -> {
                subscribeMapper.partialUpdate(existingSubscribe, subscribeDTO);

                return existingSubscribe;
            })
            .map(subscribeRepository::save)
            .map(subscribeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubscribeDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Subscribes");
        return subscribeRepository.findAll(pageable).map(subscribeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SubscribeDTO> findOne(Long id) {
        LOG.debug("Request to get Subscribe : {}", id);
        return subscribeRepository.findById(id).map(subscribeMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Subscribe : {}", id);
        subscribeRepository.deleteById(id);
    }
}
