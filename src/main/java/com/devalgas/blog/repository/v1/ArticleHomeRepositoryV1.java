package com.devalgas.blog.repository.v1;

import com.devalgas.blog.domain.enumeration.Status;
import com.devalgas.blog.domain.v1.ArticleHomeV1;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    Page<ArticleHomeV1> findByStatus(Status status, Pageable pageable);

    default Page<ArticleHomeV1> findByStatusWithEagerRelationships(Status status, Pageable pageable) {
        return this.fetchBagRelationships(this.findByStatus(status, pageable));
    }

    Page<ArticleHomeV1> findByStatusAndDisplay(Status status, Boolean display, Pageable pageable);

    default Page<ArticleHomeV1> findByStatusAndDisplayWithEagerRelationships(Status status, Boolean display, Pageable pageable) {
        return this.fetchBagRelationships(this.findByStatusAndDisplay(status, display, pageable));
    }

    @Query(
        value = "SELECT * FROM article WHERE status = :status AND display = :display",
        countQuery = "SELECT count(*) FROM article WHERE status = :status AND display = :display",
        nativeQuery = true
    )
    Page<ArticleHomeV1> findByStatusAndDisplayNative(@Param("status") String status, @Param("display") Boolean display, Pageable pageable);

    default Page<ArticleHomeV1> findByStatusAndDisplayNativeWithEagerRelationships(String status, Boolean display, Pageable pageable) {
        return this.fetchBagRelationships(this.findByStatusAndDisplayNative(status, display, pageable));
    }
}
