package com.devalgas.blog.domain;

import static com.devalgas.blog.domain.ArticleTestSamples.*;
import static com.devalgas.blog.domain.CategoryArticleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.devalgas.blog.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ArticleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Article.class);
        Article article1 = getArticleSample1();
        Article article2 = new Article();
        assertThat(article1).isNotEqualTo(article2);

        article2.setId(article1.getId());
        assertThat(article1).isEqualTo(article2);

        article2 = getArticleSample2();
        assertThat(article1).isNotEqualTo(article2);
    }

    @Test
    void categoryArticleTest() {
        Article article = getArticleRandomSampleGenerator();
        CategoryArticle categoryArticleBack = getCategoryArticleRandomSampleGenerator();

        article.addCategoryArticle(categoryArticleBack);
        assertThat(article.getCategoryArticles()).containsOnly(categoryArticleBack);

        article.removeCategoryArticle(categoryArticleBack);
        assertThat(article.getCategoryArticles()).doesNotContain(categoryArticleBack);

        article.categoryArticles(new HashSet<>(Set.of(categoryArticleBack)));
        assertThat(article.getCategoryArticles()).containsOnly(categoryArticleBack);

        article.setCategoryArticles(new HashSet<>());
        assertThat(article.getCategoryArticles()).doesNotContain(categoryArticleBack);
    }
}
