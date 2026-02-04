package com.devalgas.blog.service.impl;

import com.devalgas.blog.domain.CategoryArticle;
import com.devalgas.blog.repository.CategoryArticleRepository;
import com.devalgas.blog.service.CategoryArticleService;
import com.devalgas.blog.service.dto.CategoryArticleDTO;
import com.devalgas.blog.service.mapper.CategoryArticleMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.devalgas.blog.domain.CategoryArticle}.
 */
@Service
@Transactional
public class CategoryArticleServiceImpl implements CategoryArticleService {

    private static final Logger LOG = LoggerFactory.getLogger(CategoryArticleServiceImpl.class);

    private final CategoryArticleRepository categoryArticleRepository;

    private final CategoryArticleMapper categoryArticleMapper;

    public CategoryArticleServiceImpl(CategoryArticleRepository categoryArticleRepository, CategoryArticleMapper categoryArticleMapper) {
        this.categoryArticleRepository = categoryArticleRepository;
        this.categoryArticleMapper = categoryArticleMapper;
    }

    @Override
    public CategoryArticleDTO save(CategoryArticleDTO categoryArticleDTO) {
        LOG.debug("Request to save CategoryArticle : {}", categoryArticleDTO);
        CategoryArticle categoryArticle = categoryArticleMapper.toEntity(categoryArticleDTO);
        categoryArticle = categoryArticleRepository.save(categoryArticle);
        return categoryArticleMapper.toDto(categoryArticle);
    }

    @Override
    public CategoryArticleDTO update(CategoryArticleDTO categoryArticleDTO) {
        LOG.debug("Request to update CategoryArticle : {}", categoryArticleDTO);
        CategoryArticle categoryArticle = categoryArticleMapper.toEntity(categoryArticleDTO);
        categoryArticle = categoryArticleRepository.save(categoryArticle);
        return categoryArticleMapper.toDto(categoryArticle);
    }

    @Override
    public Optional<CategoryArticleDTO> partialUpdate(CategoryArticleDTO categoryArticleDTO) {
        LOG.debug("Request to partially update CategoryArticle : {}", categoryArticleDTO);

        return categoryArticleRepository
            .findById(categoryArticleDTO.getId())
            .map(existingCategoryArticle -> {
                categoryArticleMapper.partialUpdate(existingCategoryArticle, categoryArticleDTO);

                return existingCategoryArticle;
            })
            .map(categoryArticleRepository::save)
            .map(categoryArticleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryArticleDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all CategoryArticles");
        return categoryArticleRepository.findAll(pageable).map(categoryArticleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoryArticleDTO> findOne(Long id) {
        LOG.debug("Request to get CategoryArticle : {}", id);
        return categoryArticleRepository.findById(id).map(categoryArticleMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete CategoryArticle : {}", id);
        categoryArticleRepository.deleteById(id);
    }
}
