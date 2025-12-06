package com.devalgas.blog.repository;

import com.devalgas.blog.domain.Article;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class ArticleRepositoryWithBagRelationshipsImpl implements ArticleRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String ARTICLES_PARAMETER = "articles";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Article> fetchBagRelationships(Optional<Article> article) {
        return article.map(this::fetchCategoryArticles);
    }

    @Override
    public Page<Article> fetchBagRelationships(Page<Article> articles) {
        return new PageImpl<>(fetchBagRelationships(articles.getContent()), articles.getPageable(), articles.getTotalElements());
    }

    @Override
    public List<Article> fetchBagRelationships(List<Article> articles) {
        return Optional.of(articles).map(this::fetchCategoryArticles).orElse(Collections.emptyList());
    }

    Article fetchCategoryArticles(Article result) {
        return entityManager
            .createQuery(
                "select article from Article article left join fetch article.categoryArticles where article.id = :id",
                Article.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<Article> fetchCategoryArticles(List<Article> articles) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, articles.size()).forEach(index -> order.put(articles.get(index).getId(), index));
        List<Article> result = entityManager
            .createQuery(
                "select article from Article article left join fetch article.categoryArticles where article in :articles",
                Article.class
            )
            .setParameter(ARTICLES_PARAMETER, articles)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
