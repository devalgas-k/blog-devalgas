package com.devalgas.blog.repository.v1;

import com.devalgas.blog.domain.v1.CategoryArticleHomeV1;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface CategoryArticleHomeRepositoryWithBagRelationshipsV1 {
    Optional<CategoryArticleHomeV1> fetchBagRelationships(Optional<CategoryArticleHomeV1> category);

    List<CategoryArticleHomeV1> fetchBagRelationships(List<CategoryArticleHomeV1> categories);

    Page<CategoryArticleHomeV1> fetchBagRelationships(Page<CategoryArticleHomeV1> categories);
}
