package com.devalgas.blog.service.impl;

import com.devalgas.blog.domain.Footers;
import com.devalgas.blog.repository.FootersRepository;
import com.devalgas.blog.service.FootersService;
import com.devalgas.blog.service.dto.FootersDTO;
import com.devalgas.blog.service.mapper.FootersMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.devalgas.blog.domain.Footers}.
 */
@Service
@Transactional
public class FootersServiceImpl implements FootersService {

    private static final Logger LOG = LoggerFactory.getLogger(FootersServiceImpl.class);

    private final FootersRepository footersRepository;

    private final FootersMapper footersMapper;

    public FootersServiceImpl(FootersRepository footersRepository, FootersMapper footersMapper) {
        this.footersRepository = footersRepository;
        this.footersMapper = footersMapper;
    }

    @Override
    public FootersDTO save(FootersDTO footersDTO) {
        LOG.debug("Request to save Footers : {}", footersDTO);
        Footers footers = footersMapper.toEntity(footersDTO);
        footers = footersRepository.save(footers);
        return footersMapper.toDto(footers);
    }

    @Override
    public FootersDTO update(FootersDTO footersDTO) {
        LOG.debug("Request to update Footers : {}", footersDTO);
        Footers footers = footersMapper.toEntity(footersDTO);
        footers = footersRepository.save(footers);
        return footersMapper.toDto(footers);
    }

    @Override
    public Optional<FootersDTO> partialUpdate(FootersDTO footersDTO) {
        LOG.debug("Request to partially update Footers : {}", footersDTO);

        return footersRepository
            .findById(footersDTO.getId())
            .map(existingFooters -> {
                footersMapper.partialUpdate(existingFooters, footersDTO);

                return existingFooters;
            })
            .map(footersRepository::save)
            .map(footersMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FootersDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Footers");
        return footersRepository.findAll(pageable).map(footersMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FootersDTO> findOne(Long id) {
        LOG.debug("Request to get Footers : {}", id);
        return footersRepository.findById(id).map(footersMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Footers : {}", id);
        footersRepository.deleteById(id);
    }
}
