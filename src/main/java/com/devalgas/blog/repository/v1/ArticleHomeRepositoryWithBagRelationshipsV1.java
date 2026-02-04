package com.devalgas.blog.repository.v1;

import com.devalgas.blog.domain.v1.ArticleHomeV1;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface ArticleHomeRepositoryWithBagRelationshipsV1 {
    Optional<ArticleHomeV1> fetchBagRelationships(Optional<ArticleHomeV1> article);

    List<ArticleHomeV1> fetchBagRelationships(List<ArticleHomeV1> articles);

    Page<ArticleHomeV1> fetchBagRelationships(Page<ArticleHomeV1> articles);
}
