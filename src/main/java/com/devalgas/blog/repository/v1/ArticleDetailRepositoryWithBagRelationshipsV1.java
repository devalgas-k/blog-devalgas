package com.devalgas.blog.repository.v1;

import com.devalgas.blog.domain.v1.ArticleDetailV1;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface ArticleDetailRepositoryWithBagRelationshipsV1 {
    Optional<ArticleDetailV1> fetchBagRelationships(Optional<ArticleDetailV1> article);

    List<ArticleDetailV1> fetchBagRelationships(List<ArticleDetailV1> articles);

    Page<ArticleDetailV1> fetchBagRelationships(Page<ArticleDetailV1> articles);
}
