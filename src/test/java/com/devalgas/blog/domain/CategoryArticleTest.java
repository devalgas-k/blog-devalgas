package com.devalgas.blog.domain;

import static com.devalgas.blog.domain.ArticleTestSamples.*;
import static com.devalgas.blog.domain.CategoryArticleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.devalgas.blog.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CategoryArticleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CategoryArticle.class);
        CategoryArticle categoryArticle1 = getCategoryArticleSample1();
        CategoryArticle categoryArticle2 = new CategoryArticle();
        assertThat(categoryArticle1).isNotEqualTo(categoryArticle2);

        categoryArticle2.setId(categoryArticle1.getId());
        assertThat(categoryArticle1).isEqualTo(categoryArticle2);

        categoryArticle2 = getCategoryArticleSample2();
        assertThat(categoryArticle1).isNotEqualTo(categoryArticle2);
    }

    @Test
    void articleTest() {
        CategoryArticle categoryArticle = getCategoryArticleRandomSampleGenerator();
        Article articleBack = getArticleRandomSampleGenerator();

        categoryArticle.addArticle(articleBack);
        assertThat(categoryArticle.getArticles()).containsOnly(articleBack);
        assertThat(articleBack.getCategoryArticles()).containsOnly(categoryArticle);

        categoryArticle.removeArticle(articleBack);
        assertThat(categoryArticle.getArticles()).doesNotContain(articleBack);
        assertThat(articleBack.getCategoryArticles()).doesNotContain(categoryArticle);

        categoryArticle.articles(new HashSet<>(Set.of(articleBack)));
        assertThat(categoryArticle.getArticles()).containsOnly(articleBack);
        assertThat(articleBack.getCategoryArticles()).containsOnly(categoryArticle);

        categoryArticle.setArticles(new HashSet<>());
        assertThat(categoryArticle.getArticles()).doesNotContain(articleBack);
        assertThat(articleBack.getCategoryArticles()).doesNotContain(categoryArticle);
    }
}
