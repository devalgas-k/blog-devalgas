package com.devalgas.blog.service.impl;

import com.devalgas.blog.domain.Headers;
import com.devalgas.blog.repository.HeadersRepository;
import com.devalgas.blog.service.HeadersService;
import com.devalgas.blog.service.dto.HeadersDTO;
import com.devalgas.blog.service.mapper.HeadersMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.devalgas.blog.domain.Headers}.
 */
@Service
@Transactional
public class HeadersServiceImpl implements HeadersService {

    private static final Logger LOG = LoggerFactory.getLogger(HeadersServiceImpl.class);

    private final HeadersRepository headersRepository;

    private final HeadersMapper headersMapper;

    public HeadersServiceImpl(HeadersRepository headersRepository, HeadersMapper headersMapper) {
        this.headersRepository = headersRepository;
        this.headersMapper = headersMapper;
    }

    @Override
    public HeadersDTO save(HeadersDTO headersDTO) {
        LOG.debug("Request to save Headers : {}", headersDTO);
        Headers headers = headersMapper.toEntity(headersDTO);
        headers = headersRepository.save(headers);
        return headersMapper.toDto(headers);
    }

    @Override
    public HeadersDTO update(HeadersDTO headersDTO) {
        LOG.debug("Request to update Headers : {}", headersDTO);
        Headers headers = headersMapper.toEntity(headersDTO);
        headers = headersRepository.save(headers);
        return headersMapper.toDto(headers);
    }

    @Override
    public Optional<HeadersDTO> partialUpdate(HeadersDTO headersDTO) {
        LOG.debug("Request to partially update Headers : {}", headersDTO);

        return headersRepository
            .findById(headersDTO.getId())
            .map(existingHeaders -> {
                headersMapper.partialUpdate(existingHeaders, headersDTO);

                return existingHeaders;
            })
            .map(headersRepository::save)
            .map(headersMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HeadersDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Headers");
        return headersRepository.findAll(pageable).map(headersMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<HeadersDTO> findOne(Long id) {
        LOG.debug("Request to get Headers : {}", id);
        return headersRepository.findById(id).map(headersMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Headers : {}", id);
        headersRepository.deleteById(id);
    }
}
