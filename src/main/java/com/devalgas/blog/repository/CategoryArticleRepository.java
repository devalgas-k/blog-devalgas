package com.devalgas.blog.repository;

import com.devalgas.blog.domain.CategoryArticle;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CategoryArticle entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CategoryArticleRepository extends JpaRepository<CategoryArticle, Long> {}
