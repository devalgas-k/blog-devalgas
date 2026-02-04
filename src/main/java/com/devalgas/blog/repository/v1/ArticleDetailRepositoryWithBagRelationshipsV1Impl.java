package com.devalgas.blog.repository.v1;

import com.devalgas.blog.domain.v1.ArticleDetailV1;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.*;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;

@Repository
public class ArticleDetailRepositoryWithBagRelationshipsV1Impl implements ArticleDetailRepositoryWithBagRelationshipsV1 {

    private static final String ARTICLES_PARAMETER = "articles";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<ArticleDetailV1> fetchBagRelationships(Optional<ArticleDetailV1> article) {
        return article.map(this::fetchCategoryArticles);
    }

    @Override
    public List<ArticleDetailV1> fetchBagRelationships(List<ArticleDetailV1> articles) {
        return fetchCategoryArticles(articles);
    }

    @Override
    public Page<ArticleDetailV1> fetchBagRelationships(Page<ArticleDetailV1> articles) {
        return new PageImpl<>(fetchBagRelationships(articles.getContent()), articles.getPageable(), articles.getTotalElements());
    }

    ArticleDetailV1 fetchCategoryArticles(ArticleDetailV1 result) {
        return entityManager
            .createQuery("select a from ArticleDetailV1 a left join fetch a.categoryArticles where a.id = :id", ArticleDetailV1.class)
            .setParameter("id", result.getId())
            .getSingleResult();
    }

    List<ArticleDetailV1> fetchCategoryArticles(List<ArticleDetailV1> articles) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, articles.size()).forEach(index -> order.put(articles.get(index).getId(), index));
        List<ArticleDetailV1> result = entityManager
            .createQuery("select a from ArticleDetailV1 a left join fetch a.categoryArticles where a in :articles", ArticleDetailV1.class)
            .setParameter(ARTICLES_PARAMETER, articles)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
