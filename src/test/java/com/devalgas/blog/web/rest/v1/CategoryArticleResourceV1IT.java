package com.devalgas.blog.web.rest.v1;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.devalgas.blog.IntegrationTest;
import com.devalgas.blog.domain.Article;
import com.devalgas.blog.domain.CategoryArticle;
import com.devalgas.blog.domain.enumeration.Status;
import com.devalgas.blog.repository.ArticleRepository;
import com.devalgas.blog.repository.CategoryArticleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Base64;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CategoryArticleResourceV1IT {

    private static final String DEFAULT_CATEGORY_LABEL = "AAAAAAAAAA";
    private static final String DEFAULT_CATEGORY_CODE = "AA";
    private static final byte[] DEFAULT_BADGE = com.devalgas.blog.web.rest.TestUtil.createByteArray(1, "0");
    private static final String DEFAULT_BADGE_CONTENT_TYPE = "image/jpg";
    private static final String DEFAULT_DESCRIPTION_FR = "AAAAAAAAAA";
    private static final String DEFAULT_DESCRIPTION_EN = "AAAAAAAAAA";

    private static final String DEFAULT_ARTICLE_LABEL_EN = "AAAAAAAAAA";
    private static final String DEFAULT_ARTICLE_LABEL_FR = "AAAAAAAAAA";
    private static final String DEFAULT_ARTICLE_DESCRIPTION_FR = "AAAAAAAAAA";
    private static final String DEFAULT_ARTICLE_DESCRIPTION_EN = "AAAAAAAAAA";
    private static final byte[] DEFAULT_MARKDOWN_FR = com.devalgas.blog.web.rest.TestUtil.createByteArray(1, "0");
    private static final String DEFAULT_MARKDOWN_FR_CONTENT_TYPE = "image/jpg";
    private static final byte[] DEFAULT_MARKDOWN_EN = com.devalgas.blog.web.rest.TestUtil.createByteArray(1, "0");
    private static final String DEFAULT_MARKDOWN_EN_CONTENT_TYPE = "image/jpg";
    private static final Status DEFAULT_STATUS = Status.COMPLETED;
    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final byte[] DEFAULT_ARTICLE_BADGE = com.devalgas.blog.web.rest.TestUtil.createByteArray(1, "0");
    private static final String DEFAULT_ARTICLE_BADGE_CONTENT_TYPE = "image/jpg";
    private static final byte[] DEFAULT_BANNER = com.devalgas.blog.web.rest.TestUtil.createByteArray(1, "0");
    private static final String DEFAULT_BANNER_CONTENT_TYPE = "image/jpg";
    private static final Integer DEFAULT_VIEWS = 1;
    private static final Integer DEFAULT_STARS = 1;

    private static final String ENTITY_API_URL = "/api/v1/category-articles";
    private static final String ENTITY_API_URL_SUMMARY = ENTITY_API_URL + "/summary";
    private static final String ENTITY_API_URL_SUMMARY_FULL = ENTITY_API_URL + "/summary-full";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CategoryArticleRepository categoryArticleRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCategoryArticleMockMvc;

    private CategoryArticle categoryArticle;
    private CategoryArticle insertedCategoryArticle;
    private Article insertedArticle1;
    private Article insertedArticle2;

    public static CategoryArticle createCategoryEntity() {
        return new CategoryArticle()
            .label(DEFAULT_CATEGORY_LABEL)
            .code(DEFAULT_CATEGORY_CODE)
            .badge(DEFAULT_BADGE)
            .badgeContentType(DEFAULT_BADGE_CONTENT_TYPE)
            .descriptionFr(DEFAULT_DESCRIPTION_FR)
            .descriptionEn(DEFAULT_DESCRIPTION_EN);
    }

    public static Article createArticleEntity() {
        return new Article()
            .labelEn(DEFAULT_ARTICLE_LABEL_EN)
            .labelFr(DEFAULT_ARTICLE_LABEL_FR)
            .descriptionFr(DEFAULT_ARTICLE_DESCRIPTION_FR)
            .descriptionEn(DEFAULT_ARTICLE_DESCRIPTION_EN)
            .markdownFr(DEFAULT_MARKDOWN_FR)
            .markdownFrContentType(DEFAULT_MARKDOWN_FR_CONTENT_TYPE)
            .markdownEn(DEFAULT_MARKDOWN_EN)
            .markdownEnContentType(DEFAULT_MARKDOWN_EN_CONTENT_TYPE)
            .status(DEFAULT_STATUS)
            .date(DEFAULT_DATE)
            .badge(DEFAULT_ARTICLE_BADGE)
            .badgeContentType(DEFAULT_ARTICLE_BADGE_CONTENT_TYPE)
            .banner(DEFAULT_BANNER)
            .bannerContentType(DEFAULT_BANNER_CONTENT_TYPE)
            .views(DEFAULT_VIEWS)
            .stars(DEFAULT_STARS);
    }

    @BeforeEach
    void initTest() {
        categoryArticle = createCategoryEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedArticle1 != null) {
            articleRepository.delete(insertedArticle1);
            insertedArticle1 = null;
        }
        if (insertedArticle2 != null) {
            articleRepository.delete(insertedArticle2);
            insertedArticle2 = null;
        }
        if (insertedCategoryArticle != null) {
            categoryArticleRepository.delete(insertedCategoryArticle);
            insertedCategoryArticle = null;
        }
    }

    @Test
    @Transactional
    void getCategoryArticlesSummaryV1() throws Exception {
        insertedCategoryArticle = categoryArticleRepository.saveAndFlush(categoryArticle);

        Article article1 = createArticleEntity();
        Article article2 = createArticleEntity().labelEn("BBBBBBBBBB").labelFr("BBBBBBBBBB");

        article1.addCategoryArticle(insertedCategoryArticle);
        article2.addCategoryArticle(insertedCategoryArticle);

        insertedArticle1 = articleRepository.saveAndFlush(article1);
        insertedArticle2 = articleRepository.saveAndFlush(article2);

        restCategoryArticleMockMvc
            .perform(get(ENTITY_API_URL_SUMMARY))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(insertedCategoryArticle.getId().intValue())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_CATEGORY_LABEL)))
            .andExpect(jsonPath("$.[*].articles[*].labelFr").value(hasItem(DEFAULT_ARTICLE_LABEL_FR)))
            .andExpect(jsonPath("$.[*].articles[*].labelEn").value(hasItem(DEFAULT_ARTICLE_LABEL_EN)))
            .andExpect(jsonPath("$.[0].descriptionFr").value(DEFAULT_DESCRIPTION_FR))
            .andExpect(jsonPath("$.[0].descriptionEn").value(DEFAULT_DESCRIPTION_EN));
    }
    // Removed summary-full endpoint test: endpoint does not exist in V1 controller
}
