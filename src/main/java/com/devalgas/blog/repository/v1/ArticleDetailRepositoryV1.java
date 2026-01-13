package com.devalgas.blog.repository.v1;

import com.devalgas.blog.domain.v1.ArticleDetailV1;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Article entity.
 *
 * When extending this class, extend ArticleRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface ArticleDetailRepositoryV1 extends JpaRepository<ArticleDetailV1, Long> {
    Optional<ArticleDetailV1> findById(Long id);
}
