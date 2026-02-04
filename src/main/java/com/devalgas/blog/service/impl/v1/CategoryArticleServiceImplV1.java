package com.devalgas.blog.service.impl.v1;

import com.devalgas.blog.repository.v1.CategoryArticleHomeRepositoryV1;
import com.devalgas.blog.service.dto.v1.CategoryArticleHomeV1DTO;
import com.devalgas.blog.service.mapper.v1.CategoryArticleHomeV1Mapper;
import com.devalgas.blog.service.v1.CategoryArticleServiceV1;
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
public class CategoryArticleServiceImplV1 implements CategoryArticleServiceV1 {

    private static final Logger log = LoggerFactory.getLogger(CategoryArticleServiceImplV1.class);

    private final CategoryArticleHomeRepositoryV1 categoryArticleHomeRepositoryV1;

    private final CategoryArticleHomeV1Mapper categoryArticleHomeV1Mapper;

    public CategoryArticleServiceImplV1(
        CategoryArticleHomeRepositoryV1 categoryArticleHomeRepositoryV1,
        CategoryArticleHomeV1Mapper categoryArticleHomeV1Mapper
    ) {
        this.categoryArticleHomeRepositoryV1 = categoryArticleHomeRepositoryV1;
        this.categoryArticleHomeV1Mapper = categoryArticleHomeV1Mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryArticleHomeV1DTO> findAllCategoriesArticleHome(Pageable pageable) {
        log.debug("Request to get all CategoryArticles");
        return categoryArticleHomeRepositoryV1.findAllWithEagerRelationships(pageable).map(categoryArticleHomeV1Mapper::toDto);
    }
    // No write operations here; eviction handled in ArticleServiceImplV1 and legacy Category service
}
