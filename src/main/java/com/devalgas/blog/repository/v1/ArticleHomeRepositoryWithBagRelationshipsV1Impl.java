package com.devalgas.blog.repository.v1;

import com.devalgas.blog.domain.v1.ArticleHomeV1;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.*;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;

@Repository
public class ArticleHomeRepositoryWithBagRelationshipsV1Impl implements ArticleHomeRepositoryWithBagRelationshipsV1 {

    private static final String ARTICLES_PARAMETER = "articles";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<ArticleHomeV1> fetchBagRelationships(Optional<ArticleHomeV1> article) {
        return article.map(this::fetchCategoryArticles);
    }

    @Override
    public List<ArticleHomeV1> fetchBagRelationships(List<ArticleHomeV1> articles) {
        return fetchCategoryArticles(articles);
    }

    @Override
    public Page<ArticleHomeV1> fetchBagRelationships(Page<ArticleHomeV1> articles) {
        return new PageImpl<>(fetchBagRelationships(articles.getContent()), articles.getPageable(), articles.getTotalElements());
    }

    ArticleHomeV1 fetchCategoryArticles(ArticleHomeV1 result) {
        return entityManager
            .createQuery("select a from ArticleHomeV1 a left join fetch a.categoryArticles where a.id = :id", ArticleHomeV1.class)
            .setParameter("id", result.getId())
            .getSingleResult();
    }

    List<ArticleHomeV1> fetchCategoryArticles(List<ArticleHomeV1> articles) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, articles.size()).forEach(index -> order.put(articles.get(index).getId(), index));
        List<ArticleHomeV1> result = entityManager
            .createQuery("select a from ArticleHomeV1 a left join fetch a.categoryArticles where a in :articles", ArticleHomeV1.class)
            .setParameter(ARTICLES_PARAMETER, articles)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
