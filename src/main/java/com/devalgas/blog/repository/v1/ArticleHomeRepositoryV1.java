package com.devalgas.blog.repository.v1;

import com.devalgas.blog.domain.v1.ArticleHomeV1;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Article entity.
 *
 * When extending this class, extend ArticleRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface ArticleHomeRepositoryV1 extends ArticleHomeRepositoryWithBagRelationshipsV1, JpaRepository<ArticleHomeV1, Long> {
    Page<ArticleHomeV1> findAll(Pageable pageable);

    default Page<ArticleHomeV1> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
