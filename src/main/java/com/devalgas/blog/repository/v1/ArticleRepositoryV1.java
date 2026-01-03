package com.devalgas.blog.repository.v1;

import com.devalgas.blog.domain.Article;
import com.devalgas.blog.repository.ArticleRepositoryWithBagRelationships;
import com.devalgas.blog.repository.v1.projection.ArticleDetailsBasicProjectionV1;
import com.devalgas.blog.repository.v1.projection.ArticleSummaryWithCategoriesProjectionV1;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
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

    @EntityGraph(attributePaths = "categoryArticles")
    List<Article> findAllByCategoryArticles_Id(Long categoryId);

    @Query(
        value = """
        select a.id as id, a.labelFr as labelFr, a.labelEn as labelEn, a.date as date,
               ca.id as categoryId, ca.label as categoryLabel
        from Article a
        left join a.categoryArticles ca
        """,
        countQuery = "select count(a) from Article a"
    )
    Page<ArticleSummaryWithCategoriesProjectionV1> findAllSummaryWithCategories(Pageable pageable);

    @Query(
        "select a.id as id, a.labelFr as labelFr, a.labelEn as labelEn, a.markdownFr as markdownFr, a.markdownFrContentType as markdownFrContentType, a.markdownEn as markdownEn, a.markdownEnContentType as markdownEnContentType, a.date as date from Article a where a.id = :id"
    )
    Optional<ArticleDetailsBasicProjectionV1> findDetailsBasicById(Long id);

    @Query(
        "select a.id as id, a.labelFr as labelFr, null as labelEn, a.markdownFr as markdownFr, a.markdownFrContentType as markdownFrContentType, null as markdownEn, null as markdownEnContentType, a.date as date from Article a where a.id = :id"
    )
    Optional<ArticleDetailsBasicProjectionV1> findDetailsBasicFrById(Long id);

    @Query(
        "select a.id as id, null as labelFr, a.labelEn as labelEn, null as markdownFr, null as markdownFrContentType, a.markdownEn as markdownEn, a.markdownEnContentType as markdownEnContentType, a.date as date from Article a where a.id = :id"
    )
    Optional<ArticleDetailsBasicProjectionV1> findDetailsBasicEnById(Long id);
}
