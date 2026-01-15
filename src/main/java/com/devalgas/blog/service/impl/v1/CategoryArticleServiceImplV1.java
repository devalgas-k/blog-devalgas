package com.devalgas.blog.service.impl.v1;

import com.devalgas.blog.repository.v1.CategoryArticleHomeRepositoryV1;
import com.devalgas.blog.repository.v1.CategoryArticleRepositoryV1;
import com.devalgas.blog.service.dto.v1.CategoryArticleHomeV1DTO;
import com.devalgas.blog.service.dto.v1.CategoryArticleSummaryDTOV1;
import com.devalgas.blog.service.mapper.v1.ArticleProjectionMapperV1;
import com.devalgas.blog.service.mapper.v1.CategoryArticleHomeV1Mapper;
import com.devalgas.blog.service.v1.CategoryArticleServiceV1;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
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

    private final CategoryArticleRepositoryV1 categoryArticleRepository;

    private final CategoryArticleHomeRepositoryV1 categoryArticleHomeRepositoryV1;

    private final ArticleProjectionMapperV1 articleProjectionMapperV1;

    private final CategoryArticleHomeV1Mapper categoryArticleHomeV1Mapper;

    private final Environment environment;

    public CategoryArticleServiceImplV1(
        CategoryArticleRepositoryV1 categoryArticleRepository,
        CategoryArticleHomeRepositoryV1 categoryArticleHomeRepositoryV1,
        ArticleProjectionMapperV1 articleProjectionMapperV1,
        CategoryArticleHomeV1Mapper categoryArticleHomeV1Mapper,
        Environment environment
    ) {
        this.categoryArticleRepository = categoryArticleRepository;
        this.categoryArticleHomeRepositoryV1 = categoryArticleHomeRepositoryV1;
        this.articleProjectionMapperV1 = articleProjectionMapperV1;
        this.categoryArticleHomeV1Mapper = categoryArticleHomeV1Mapper;
        this.environment = environment;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
        cacheNames = "categoryArticlesSummaryAllV1",
        key = "T(java.util.Objects).toString(@categoryArticleRepositoryV1.count())",
        unless = "T(java.util.Arrays).asList(@environment.getActiveProfiles()).contains('test') || " +
        "T(java.util.Arrays).asList(@environment.getActiveProfiles()).contains('testdev') || " +
        "T(java.util.Arrays).asList(@environment.getActiveProfiles()).contains('testprod')"
    )
    public java.util.List<CategoryArticleSummaryDTOV1> findAllSummaryBasicObjectsV1() {
        log.debug("Request to get CategoryArticles summary basic objects (all)");
        List<com.devalgas.blog.repository.v1.projection.CategoryArticleSummaryWithArticlesProjectionV1> rows =
            categoryArticleRepository.findAllSummaryWithArticles();
        java.util.Map<Long, CategoryArticleSummaryDTOV1> map = new java.util.LinkedHashMap<>();
        rows.forEach(r -> {
            CategoryArticleSummaryDTOV1 dto = map.computeIfAbsent(r.getId(), id -> {
                CategoryArticleSummaryDTOV1 s = new CategoryArticleSummaryDTOV1();
                s.setId(id);
                s.setLabel(r.getLabel());
                s.setArticles(new java.util.HashSet<>());
                return s;
            });
            if (r.getArticleId() != null) {
                dto
                    .getArticles()
                    .add(
                        articleProjectionMapperV1.toArticleLabelBasic(
                            r.getArticleId(),
                            r.getArticleLabelFr(),
                            r.getArticleLabelEn(),
                            r.getArticleDate()
                        )
                    );
            }
        });
        return new java.util.ArrayList<>(map.values());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "categoryArticlesSummaryAllV1")
    public Page<CategoryArticleHomeV1DTO> findAllCategoriesArticleHome(Pageable pageable) {
        log.debug("Request to get all CategoryArticles");
        return categoryArticleHomeRepositoryV1.findAll(pageable).map(categoryArticleHomeV1Mapper::toDto);
    }
}
