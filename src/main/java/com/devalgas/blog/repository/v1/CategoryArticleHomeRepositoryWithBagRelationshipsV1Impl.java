package com.devalgas.blog.repository.v1;

import com.devalgas.blog.domain.v1.CategoryArticleHomeV1;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.*;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryArticleHomeRepositoryWithBagRelationshipsV1Impl implements CategoryArticleHomeRepositoryWithBagRelationshipsV1 {

    private static final String CATEGORIES_PARAMETER = "categories";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<CategoryArticleHomeV1> fetchBagRelationships(Optional<CategoryArticleHomeV1> category) {
        return category.map(this::fetchArticles);
    }

    @Override
    public List<CategoryArticleHomeV1> fetchBagRelationships(List<CategoryArticleHomeV1> categories) {
        return fetchArticles(categories);
    }

    @Override
    public Page<CategoryArticleHomeV1> fetchBagRelationships(Page<CategoryArticleHomeV1> categories) {
        return new PageImpl<>(fetchBagRelationships(categories.getContent()), categories.getPageable(), categories.getTotalElements());
    }

    CategoryArticleHomeV1 fetchArticles(CategoryArticleHomeV1 result) {
        return entityManager
            .createQuery("select c from CategoryArticleHomeV1 c left join fetch c.articles where c.id = :id", CategoryArticleHomeV1.class)
            .setParameter("id", result.getId())
            .getSingleResult();
    }

    List<CategoryArticleHomeV1> fetchArticles(List<CategoryArticleHomeV1> categories) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, categories.size()).forEach(index -> order.put(categories.get(index).getId(), index));
        List<CategoryArticleHomeV1> result = entityManager
            .createQuery(
                "select c from CategoryArticleHomeV1 c left join fetch c.articles where c in :categories",
                CategoryArticleHomeV1.class
            )
            .setParameter(CATEGORIES_PARAMETER, categories)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
