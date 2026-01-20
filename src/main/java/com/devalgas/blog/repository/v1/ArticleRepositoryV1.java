package com.devalgas.blog.repository.v1;

import com.devalgas.blog.domain.Article;
import com.devalgas.blog.repository.ArticleRepositoryWithBagRelationships;
import com.devalgas.blog.repository.v1.projection.ArticleDetailsBasicProjectionV1;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Article entity.
 *
 * When extending this class, extend ArticleRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface ArticleRepositoryV1 extends ArticleRepositoryWithBagRelationships, JpaRepository<Article, Long> {
    default Optional<Article> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<Article> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Article> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }

    @Query(
        "select a.id as id, a.labelFr as labelFr, a.labelEn as labelEn, a.descriptionFr as descriptionFr, a.descriptionEn as descriptionEn, a.markdownFr as markdownFr, a.markdownFrContentType as markdownFrContentType, a.markdownEn as markdownEn, a.markdownEnContentType as markdownEnContentType, a.date as date from Article a where a.id = :id"
    )
    Optional<ArticleDetailsBasicProjectionV1> findDetailsBasicById(Long id);
}
