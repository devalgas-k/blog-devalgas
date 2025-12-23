package com.devalgas.blog.service.impl;

import com.devalgas.blog.domain.Subject;
import com.devalgas.blog.repository.SubjectRepository;
import com.devalgas.blog.service.SubjectService;
import com.devalgas.blog.service.dto.SubjectDTO;
import com.devalgas.blog.service.mapper.SubjectMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.devalgas.blog.domain.Subject}.
 */
@Service
@Transactional
public class SubjectServiceImpl implements SubjectService {

    private static final Logger LOG = LoggerFactory.getLogger(SubjectServiceImpl.class);

    private final SubjectRepository subjectRepository;

    private final SubjectMapper subjectMapper;

    public SubjectServiceImpl(SubjectRepository subjectRepository, SubjectMapper subjectMapper) {
        this.subjectRepository = subjectRepository;
        this.subjectMapper = subjectMapper;
    }

    @Override
    public SubjectDTO save(SubjectDTO subjectDTO) {
        LOG.debug("Request to save Subject : {}", subjectDTO);
        Subject subject = subjectMapper.toEntity(subjectDTO);
        subject = subjectRepository.save(subject);
        return subjectMapper.toDto(subject);
    }

    @Override
    public SubjectDTO update(SubjectDTO subjectDTO) {
        LOG.debug("Request to update Subject : {}", subjectDTO);
        Subject subject = subjectMapper.toEntity(subjectDTO);
        subject = subjectRepository.save(subject);
        return subjectMapper.toDto(subject);
    }

    @Override
    public Optional<SubjectDTO> partialUpdate(SubjectDTO subjectDTO) {
        LOG.debug("Request to partially update Subject : {}", subjectDTO);

        return subjectRepository
            .findById(subjectDTO.getId())
            .map(existingSubject -> {
                subjectMapper.partialUpdate(existingSubject, subjectDTO);

                return existingSubject;
            })
            .map(subjectRepository::save)
            .map(subjectMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubjectDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Subjects");
        return subjectRepository.findAll(pageable).map(subjectMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SubjectDTO> findOne(Long id) {
        LOG.debug("Request to get Subject : {}", id);
        return subjectRepository.findById(id).map(subjectMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Subject : {}", id);
        subjectRepository.deleteById(id);
    }
}
