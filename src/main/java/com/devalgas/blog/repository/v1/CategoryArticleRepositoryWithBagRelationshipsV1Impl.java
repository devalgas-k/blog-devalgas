package com.devalgas.blog.repository.v1;

import com.devalgas.blog.domain.CategoryArticle;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

public class CategoryArticleRepositoryWithBagRelationshipsV1Impl implements CategoryArticleRepositoryWithBagRelationshipsV1 {

    private static final String ID_PARAMETER = "id";
    private static final String CATEGORY_ARTICLES_PARAMETER = "categoryArticles";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<CategoryArticle> fetchBagRelationships(Optional<CategoryArticle> categoryArticle) {
        return categoryArticle.map(this::fetchArticles);
    }

    @Override
    public Page<CategoryArticle> fetchBagRelationships(Page<CategoryArticle> categoryArticles) {
        return new PageImpl<>(
            fetchBagRelationships(categoryArticles.getContent()),
            categoryArticles.getPageable(),
            categoryArticles.getTotalElements()
        );
    }

    @Override
    public List<CategoryArticle> fetchBagRelationships(List<CategoryArticle> categoryArticles) {
        return Optional.of(categoryArticles).map(this::fetchArticles).orElse(Collections.emptyList());
    }

    CategoryArticle fetchArticles(CategoryArticle result) {
        return entityManager
            .createQuery(
                "select categoryArticle from CategoryArticle categoryArticle left join fetch categoryArticle.articles where categoryArticle.id = :id",
                CategoryArticle.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<CategoryArticle> fetchArticles(List<CategoryArticle> categoryArticles) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, categoryArticles.size()).forEach(index -> order.put(categoryArticles.get(index).getId(), index));
        List<CategoryArticle> result = entityManager
            .createQuery(
                "select categoryArticle from CategoryArticle categoryArticle left join fetch categoryArticle.articles where categoryArticle in :categoryArticles",
                CategoryArticle.class
            )
            .setParameter(CATEGORY_ARTICLES_PARAMETER, categoryArticles)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
