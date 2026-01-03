package com.devalgas.blog.repository.v1;

import com.devalgas.blog.domain.CategoryArticle;
import com.devalgas.blog.domain.v1.ArticleHomeV1;
import com.devalgas.blog.domain.v1.CategoryArticleHomeV1;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CategoryArticle entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CategoryArticleHomeRepositoryV1 extends JpaRepository<CategoryArticleHomeV1, Long> {
    Page<CategoryArticleHomeV1> findAll(Pageable pageable);
}
