package com.devalgas.blog.service.impl.v1;

import com.devalgas.blog.domain.Article;
import com.devalgas.blog.domain.enumeration.Status;
import com.devalgas.blog.repository.v1.ArticleDetailRepositoryV1;
import com.devalgas.blog.repository.v1.ArticleHomeRepositoryV1;
import com.devalgas.blog.repository.v1.ArticleRepositoryV1;
import com.devalgas.blog.service.dto.ArticleDTO;
import com.devalgas.blog.service.dto.v1.*;
import com.devalgas.blog.service.mapper.ArticleMapper;
import com.devalgas.blog.service.mapper.v1.ArticleDetailV1Mapper;
import com.devalgas.blog.service.mapper.v1.ArticleHomeV1Mapper;
import com.devalgas.blog.service.v1.ArticleServiceV1;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Article}.
 */
@Service
public class ArticleServiceImplV1 implements ArticleServiceV1 {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleServiceImplV1.class);

    private final ArticleRepositoryV1 articleRepository;
    private final ArticleHomeRepositoryV1 articleHomeRepositoryV1;
    private final ArticleDetailRepositoryV1 articleDetailRepositoryV1;
    private final ArticleMapper articleMapper;
    private final ArticleHomeV1Mapper articleHomeV1Mapper;
    private final ArticleDetailV1Mapper articleDetailV1Mapper;

    @PersistenceContext
    private EntityManager entityManager;

    public ArticleServiceImplV1(
        ArticleRepositoryV1 articleRepository,
        ArticleHomeRepositoryV1 articleHomeRepositoryV1,
        ArticleDetailRepositoryV1 articleDetailRepositoryV1,
        ArticleMapper articleMapper,
        ArticleHomeV1Mapper articleHomeV1Mapper,
        ArticleDetailV1Mapper articleDetailV1Mapper
    ) {
        this.articleRepository = articleRepository;
        this.articleHomeRepositoryV1 = articleHomeRepositoryV1;
        this.articleDetailRepositoryV1 = articleDetailRepositoryV1;
        this.articleMapper = articleMapper;
        this.articleHomeV1Mapper = articleHomeV1Mapper;
        this.articleDetailV1Mapper = articleDetailV1Mapper;
    }

    @Override
    public ArticleDTO save(ArticleDTO articleDTO) {
        LOG.debug("Request to save Article : {}", articleDTO);
        Article article = articleMapper.toEntity(articleDTO);
        article = articleRepository.save(article);
        return articleMapper.toDto(article);
    }

    @Override
    public ArticleDTO update(ArticleDTO articleDTO) {
        LOG.debug("Request to update Article : {}", articleDTO);
        Article article = articleMapper.toEntity(articleDTO);
        article = articleRepository.save(article);
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
            .map(articleMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Article : {}", id);
        articleRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ArticleHomeV1DTO> findAllArticlesHome(Status status, Pageable pageable) {
        LOG.debug("Request to get all Articles by status {}", status);
        return articleHomeRepositoryV1
            .findByStatusWithEagerRelationships(
                status,
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "date"))
            )
            .map(articleHomeV1Mapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ArticleDetailV1DTO> findOneArticleDetails(Long id) {
        LOG.debug("Request to get Article : {}", id);
        return articleDetailRepositoryV1.findOneWithEagerRelationships(id).map(articleDetailV1Mapper::toDto);
    }
}
