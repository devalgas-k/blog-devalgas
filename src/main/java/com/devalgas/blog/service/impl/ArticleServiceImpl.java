package com.devalgas.blog.service.impl;

import com.devalgas.blog.domain.Article;
import com.devalgas.blog.repository.ArticleRepository;
import com.devalgas.blog.service.ArticleService;
import com.devalgas.blog.service.dto.ArticleDTO;
import com.devalgas.blog.service.mapper.ArticleMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.devalgas.blog.domain.Article}.
 */
@Service
@Transactional
public class ArticleServiceImpl implements ArticleService {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleServiceImpl.class);

    private final ArticleRepository articleRepository;

    private final ArticleMapper articleMapper;

    @PersistenceContext
    private EntityManager entityManager;

    public ArticleServiceImpl(ArticleRepository articleRepository, ArticleMapper articleMapper) {
        this.articleRepository = articleRepository;
        this.articleMapper = articleMapper;
    }

    @Override
    public ArticleDTO save(ArticleDTO articleDTO) {
        LOG.debug("Request to save Article : {}", articleDTO);
        Article article = articleMapper.toEntity(articleDTO);
        article = articleRepository.save(article);
        evictV1Caches(article.getId());
        return articleMapper.toDto(article);
    }

    @Override
    public ArticleDTO update(ArticleDTO articleDTO) {
        LOG.debug("Request to update Article : {}", articleDTO);
        Article article = articleMapper.toEntity(articleDTO);
        article = articleRepository.save(article);
        evictV1Caches(article.getId());
        return articleMapper.toDto(article);
    }

    @Override
    public Optional<ArticleDTO> partialUpdate(ArticleDTO articleDTO) {
        LOG.debug("Request to partially update Article : {}", articleDTO);

        return articleRepository
            .findById(articleDTO.getId())
            .map(existingArticle -> {
                articleMapper.partialUpdate(existingArticle, articleDTO);

                return existingArticle;
            })
            .map(articleRepository::save)
            .map(saved -> {
                evictV1Caches(saved.getId());
                return articleMapper.toDto(saved);
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ArticleDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Articles");
        return articleRepository.findAll(pageable).map(articleMapper::toDto);
    }

    public Page<ArticleDTO> findAllWithEagerRelationships(Pageable pageable) {
        return articleRepository.findAllWithEagerRelationships(pageable).map(articleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ArticleDTO> findOne(Long id) {
        LOG.debug("Request to get Article : {}", id);
        return articleRepository.findOneWithEagerRelationships(id).map(articleMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Article : {}", id);
        articleRepository.deleteById(id);
        evictV1Caches(id);
    }

    private void evictV1Caches(Long articleId) {
        if (articleId == null) return;
        var cache = entityManager.getEntityManagerFactory().getCache();
        cache.evict(com.devalgas.blog.domain.v1.ArticleHomeV1.class, articleId);
        cache.evict(com.devalgas.blog.domain.v1.ArticleDetailV1.class, articleId);
        cache.evict(com.devalgas.blog.domain.v1.CategoryArticleHomeV1.class);
    }
}
