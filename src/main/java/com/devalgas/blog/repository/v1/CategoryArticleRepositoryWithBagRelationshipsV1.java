package com.devalgas.blog.repository.v1;

import com.devalgas.blog.domain.CategoryArticle;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface CategoryArticleRepositoryWithBagRelationshipsV1 {
    Optional<CategoryArticle> fetchBagRelationships(Optional<CategoryArticle> categoryArticle);

    List<CategoryArticle> fetchBagRelationships(List<CategoryArticle> categoryArticles);

    Page<CategoryArticle> fetchBagRelationships(Page<CategoryArticle> categoryArticles);
}
