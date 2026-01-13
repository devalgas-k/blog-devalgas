package com.devalgas.blog.repository.v1;

import com.devalgas.blog.domain.CategoryArticle;
import com.devalgas.blog.repository.v1.projection.CategoryArticleLabelProjectionV1;
import com.devalgas.blog.repository.v1.projection.CategoryArticleSummaryWithArticlesProjectionV1;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CategoryArticle entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CategoryArticleRepositoryV1 extends CategoryArticleRepositoryWithBagRelationshipsV1, JpaRepository<CategoryArticle, Long> {
    default Optional<CategoryArticle> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<CategoryArticle> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<CategoryArticle> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }

    @Query(
        value = """
        select ca.id as id, ca.label as label,
               a.id as articleId, a.labelFr as articleLabelFr, a.labelEn as articleLabelEn, a.date as articleDate
        from CategoryArticle ca
        left join ca.articles a
        """
    )
    List<CategoryArticleSummaryWithArticlesProjectionV1> findAllSummaryWithArticles();

    Set<CategoryArticleLabelProjectionV1> findAllByArticles_Id(Long articlesId);
}
