package com.devalgas.blog.repository.v1;

import com.devalgas.blog.domain.Article;
import com.devalgas.blog.domain.enumeration.Status;
import com.devalgas.blog.repository.ArticleRepositoryWithBagRelationships;
import java.util.List;
import java.util.Optional;
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
public interface ArticleRepositoryV1 extends ArticleRepositoryWithBagRelationships, JpaRepository<Article, Long> {
    default List<Article> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Article> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }

    Optional<Article> findFirstByStatusAndDisplayAndNewsletterOrderByDateDesc(Status status, Boolean display, Boolean newsletter);

    default Optional<Article> findFirstByStatusAndDisplayAndNewsletterWithEagerRelationships(
        Status status,
        Boolean display,
        Boolean newsletter
    ) {
        return this.fetchBagRelationships(this.findFirstByStatusAndDisplayAndNewsletterOrderByDateDesc(status, display, newsletter));
    }
}
