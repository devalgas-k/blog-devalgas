package com.devalgas.blog.service.impl.v1;

import com.devalgas.blog.domain.Article;
import com.devalgas.blog.domain.v1.ArticleDetailV1;
import com.devalgas.blog.repository.v1.ArticleDetailRepositoryV1;
import com.devalgas.blog.repository.v1.ArticleHomeRepositoryV1;
import com.devalgas.blog.repository.v1.ArticleRepositoryV1;
import com.devalgas.blog.repository.v1.CategoryArticleRepositoryV1;
import com.devalgas.blog.repository.v1.projection.ArticleDetailsBasicProjectionV1;
import com.devalgas.blog.repository.v1.projection.ArticleSummaryWithCategoriesProjectionV1;
import com.devalgas.blog.service.dto.ArticleDTO;
import com.devalgas.blog.service.dto.v1.*;
import com.devalgas.blog.service.mapper.ArticleMapper;
import com.devalgas.blog.service.mapper.v1.ArticleDetailV1Mapper;
import com.devalgas.blog.service.mapper.v1.ArticleHomeV1Mapper;
import com.devalgas.blog.service.mapper.v1.ArticleProjectionMapperV1;
import com.devalgas.blog.service.v1.ArticleServiceV1;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Article}.
 */
@Service
public class ArticleServiceV1Impl implements ArticleServiceV1 {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleServiceV1Impl.class);

    private final ArticleRepositoryV1 articleRepository;
    private final ArticleHomeRepositoryV1 articleHomeRepositoryV1;
    private final ArticleDetailRepositoryV1 articleDetailRepositoryV1;
    private final ArticleProjectionMapperV1 articleProjectionMapperV1;
    private final ArticleMapper articleMapper;
    private final ArticleHomeV1Mapper articleHomeV1Mapper;
    private final ArticleDetailV1Mapper articleDetailV1Mapper;
    private final CategoryArticleRepositoryV1 categoryArticleRepositoryV1;

    @PersistenceContext
    private EntityManager entityManager;

    public ArticleServiceV1Impl(
        ArticleRepositoryV1 articleRepository,
        ArticleHomeRepositoryV1 articleHomeRepositoryV1,
        ArticleDetailRepositoryV1 articleDetailRepositoryV1,
        ArticleProjectionMapperV1 articleProjectionMapperV1,
        ArticleMapper articleMapper,
        ArticleHomeV1Mapper articleHomeV1Mapper,
        ArticleDetailV1Mapper articleDetailV1Mapper,
        CategoryArticleRepositoryV1 categoryArticleRepositoryV1
    ) {
        this.articleRepository = articleRepository;
        this.articleHomeRepositoryV1 = articleHomeRepositoryV1;
        this.articleDetailRepositoryV1 = articleDetailRepositoryV1;
        this.articleProjectionMapperV1 = articleProjectionMapperV1;
        this.articleMapper = articleMapper;
        this.articleHomeV1Mapper = articleHomeV1Mapper;
        this.articleDetailV1Mapper = articleDetailV1Mapper;
        this.categoryArticleRepositoryV1 = categoryArticleRepositoryV1;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "articlesSummaryV1", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort.toString()")
    public Page<ArticleSummaryBasicDTOV1> findAllSummaryBasicProjectedV1(Pageable pageable) {
        LOG.debug("Request to get projected Articles summary basic");

        Page<ArticleSummaryWithCategoriesProjectionV1> resultPage = articleRepository.findAllSummaryWithCategories(pageable);

        Map<Long, ArticleSummaryBasicDTOV1> articleMap = new LinkedHashMap<>();

        resultPage
            .getContent()
            .forEach(projection -> {
                ArticleSummaryBasicDTOV1 article = articleMap.computeIfAbsent(projection.getId(), id -> {
                    ArticleSummaryBasicDTOV1 a = new ArticleSummaryBasicDTOV1();
                    a.setId(id);
                    a.setLabelFr(projection.getLabelFr());
                    a.setLabelEn(projection.getLabelEn());
                    a.setDate(projection.getDate());
                    a.setCategoryArticles(new java.util.HashSet<>());
                    return a;
                });

                if (projection.getCategoryId() != null) {
                    CategoryLabelBasicDTOV1 category = articleProjectionMapperV1.toCategoryLabelDto(
                        projection.getCategoryId(),
                        projection.getCategoryLabel()
                    );
                    article.getCategoryArticles().add(category);
                }
            });

        return new PageImpl<>(new ArrayList<>(articleMap.values()), pageable, resultPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "articleDetailsV1", key = "#id")
    public java.util.Optional<ArticleDetailsBasicDTOV1> findOneDetailsBasicProjectedV1(Long id) {
        LOG.debug("Request to get projected Article details basic: {}", id);
        java.util.Optional<ArticleDetailsBasicProjectionV1> projection = articleRepository.findDetailsBasicById(id);

        return projection.map(p -> {
            ArticleDetailsBasicDTOV1 dto = articleProjectionMapperV1.toDto(p);
            java.util.Set<com.devalgas.blog.repository.v1.projection.CategoryArticleLabelProjectionV1> cats =
                categoryArticleRepositoryV1.findAllByArticles_Id(id);
            java.util.Set<CategoryLabelBasicDTOV1> labels = new java.util.HashSet<>();
            if (cats != null) {
                LOG.debug("Collecting categories for article {}: {} found", id, cats.size());
                for (com.devalgas.blog.repository.v1.projection.CategoryArticleLabelProjectionV1 c : cats) {
                    labels.add(articleProjectionMapperV1.toDto(c));
                }
            }
            if (labels.isEmpty()) {
                articleRepository
                    .findOneWithEagerRelationships(id)
                    .ifPresent(entity -> {
                        LOG.debug("Fallback eager relationships for article {}: {}", id, entity.getCategoryArticles().size());
                        for (com.devalgas.blog.domain.CategoryArticle ca : entity.getCategoryArticles()) {
                            labels.add(articleProjectionMapperV1.toCategoryLabelDto(ca.getId(), ca.getLabel()));
                        }
                    });
            }
            dto.setCategoryArticles(labels);
            LOG.debug("Article {} details dto categories size: {}", id, labels.size());
            return dto;
        });
    }

    @Override
    @CacheEvict(cacheNames = { "articlesSummaryV1", "articleDetailsV1" }, allEntries = true)
    public ArticleDTO save(ArticleDTO articleDTO) {
        LOG.debug("Request to save Article : {}", articleDTO);
        Article article = articleMapper.toEntity(articleDTO);
        article = articleRepository.save(article);
        return articleMapper.toDto(article);
    }

    @Override
    @CacheEvict(cacheNames = { "articlesSummaryV1", "articleDetailsV1" }, allEntries = true)
    public ArticleDTO update(ArticleDTO articleDTO) {
        LOG.debug("Request to update Article : {}", articleDTO);
        Article article = articleMapper.toEntity(articleDTO);
        article = articleRepository.save(article);
        return articleMapper.toDto(article);
    }

    @Override
    @CacheEvict(cacheNames = { "articlesSummaryV1", "articleDetailsV1" }, allEntries = true)
    public Optional<ArticleDTO> partialUpdate(ArticleDTO articleDTO) {
        LOG.debug("Request to partially update Article : {}", articleDTO);

        return articleRepository
            .findById(articleDTO.getId())
            .map(existingArticle -> {
                articleMapper.partialUpdate(existingArticle, articleDTO);

                return existingArticle;
            })
            .map(articleRepository::save)
            .map(articleMapper::toDto);
    }

    @Override
    @CacheEvict(cacheNames = { "articlesSummaryV1", "articleDetailsV1" }, allEntries = true)
    public void delete(Long id) {
        LOG.debug("Request to delete Article : {}", id);
        articleRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "articlesSummaryV1", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort.toString()")
    public Page<ArticleHomeV1DTO> findAllArticlesHome(Pageable pageable) {
        LOG.debug("Request to get all Articles");
        return articleHomeRepositoryV1.findAll(pageable).map(articleHomeV1Mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ArticleDetailV1DTO> findOneArticleDetails(Long id) {
        LOG.debug("Request to get Article : {}", id);
        return articleDetailRepositoryV1.findById(id).map(articleDetailV1Mapper::toDto);
    }
}
