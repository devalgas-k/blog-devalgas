package com.devalgas.blog.service.impl.v1;

import com.devalgas.blog.repository.v1.CategoryArticleRepositoryV1;
import com.devalgas.blog.service.dto.CategoryArticleDTO;
import com.devalgas.blog.service.mapper.CategoryArticleMapper;
import com.devalgas.blog.service.mapper.v1.CategoryArticleMapperV1;
import com.devalgas.blog.service.v1.CategoryArticleServiceV1;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.devalgas.blog.domain.CategoryArticle}.
 */
@Service
@Transactional
public class CategoryArticleServiceV1Impl implements CategoryArticleServiceV1 {

    private static final Logger log = LoggerFactory.getLogger(CategoryArticleServiceV1Impl.class);

    private final CategoryArticleRepositoryV1 categoryArticleRepository;

    private final CategoryArticleMapperV1 categoryArticleMapper;

    public CategoryArticleServiceV1Impl(
        CategoryArticleRepositoryV1 categoryArticleRepository,
        CategoryArticleMapperV1 categoryArticleMapper
    ) {
        this.categoryArticleRepository = categoryArticleRepository;
        this.categoryArticleMapper = categoryArticleMapper;
    }

    @Override
    public Optional<CategoryArticleDTO> findOneV1(Long id) {
        log.debug("Request to get CategoryArticle : {}", id);
        return categoryArticleRepository.findOneWithEagerRelationships(id).map(categoryArticleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryArticleDTO> findAll(Pageable pageable) {
        log.debug("Request to get all CategoryArticles");
        return categoryArticleRepository.findAll(pageable).map(categoryArticleMapper::toDto);
    }

    public Page<CategoryArticleDTO> findAllWithEagerRelationshipsV1(Pageable pageable) {
        log.debug("Request to get all Articles with eager");
        return categoryArticleRepository.findAllWithEagerRelationships(pageable).map(categoryArticleMapper::toDto);
    }
}
