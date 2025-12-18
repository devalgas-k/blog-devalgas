package com.devalgas.blog.web.rest.v1;

import static com.devalgas.blog.web.rest.TestUtil.sameInstant;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.devalgas.blog.IntegrationTest;
import com.devalgas.blog.domain.Article;
import com.devalgas.blog.domain.enumeration.Status;
import com.devalgas.blog.repository.ArticleRepository;
import com.devalgas.blog.service.mapper.ArticleMapper;
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
class ArticleResourceV1IT {

    private static final String DEFAULT_LABEL_EN = "AAAAAAAAAA";
    private static final String DEFAULT_LABEL_FR = "AAAAAAAAAA";
    private static final String DEFAULT_DESCRIPTION_FR = "AAAAAAAAAA";
    private static final String DEFAULT_DESCRIPTION_EN = "AAAAAAAAAA";

    private static final byte[] DEFAULT_MARKDOWN_FR = com.devalgas.blog.web.rest.TestUtil.createByteArray(1, "0");
    private static final String DEFAULT_MARKDOWN_FR_CONTENT_TYPE = "image/jpg";

    private static final byte[] DEFAULT_MARKDOWN_EN = com.devalgas.blog.web.rest.TestUtil.createByteArray(1, "0");
    private static final String DEFAULT_MARKDOWN_EN_CONTENT_TYPE = "image/jpg";

    private static final Status DEFAULT_STATUS = Status.COMPLETED;
    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);

    private static final byte[] DEFAULT_BADGE = com.devalgas.blog.web.rest.TestUtil.createByteArray(1, "0");
    private static final String DEFAULT_BADGE_CONTENT_TYPE = "image/jpg";

    private static final byte[] DEFAULT_BANNER = com.devalgas.blog.web.rest.TestUtil.createByteArray(1, "0");
    private static final String DEFAULT_BANNER_CONTENT_TYPE = "image/jpg";

    private static final Integer DEFAULT_VIEWS = 1;
    private static final Integer DEFAULT_STARS = 1;

    private static final String ENTITY_API_URL = "/api/v1/articles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restArticleMockMvc;

    private Article article;
    private Article insertedArticle;

    public static Article createEntity() {
        return new Article()
            .labelEn(DEFAULT_LABEL_EN)
            .labelFr(DEFAULT_LABEL_FR)
            .descriptionFr(DEFAULT_DESCRIPTION_FR)
            .descriptionEn(DEFAULT_DESCRIPTION_EN)
            .markdownFr(DEFAULT_MARKDOWN_FR)
            .markdownFrContentType(DEFAULT_MARKDOWN_FR_CONTENT_TYPE)
            .markdownEn(DEFAULT_MARKDOWN_EN)
            .markdownEnContentType(DEFAULT_MARKDOWN_EN_CONTENT_TYPE)
            .status(DEFAULT_STATUS)
            .date(DEFAULT_DATE)
            .badge(DEFAULT_BADGE)
            .badgeContentType(DEFAULT_BADGE_CONTENT_TYPE)
            .banner(DEFAULT_BANNER)
            .bannerContentType(DEFAULT_BANNER_CONTENT_TYPE)
            .views(DEFAULT_VIEWS)
            .stars(DEFAULT_STARS);
    }

    @BeforeEach
    void initTest() {
        article = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedArticle != null) {
            articleRepository.delete(insertedArticle);
            insertedArticle = null;
        }
    }

    @Test
    @Transactional
    void getArticleV1() throws Exception {
        insertedArticle = articleRepository.saveAndFlush(article);
        restArticleMockMvc
            .perform(get(ENTITY_API_URL_ID, article.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(article.getId().intValue()))
            .andExpect(jsonPath("$.labelEn").value(DEFAULT_LABEL_EN))
            .andExpect(jsonPath("$.labelFr").value(DEFAULT_LABEL_FR))
            .andExpect(jsonPath("$.descriptionFr").value(DEFAULT_DESCRIPTION_FR))
            .andExpect(jsonPath("$.descriptionEn").value(DEFAULT_DESCRIPTION_EN))
            .andExpect(jsonPath("$.markdownFrContentType").value(DEFAULT_MARKDOWN_FR_CONTENT_TYPE))
            .andExpect(jsonPath("$.markdownFr").value(Base64.getEncoder().encodeToString(DEFAULT_MARKDOWN_FR)))
            .andExpect(jsonPath("$.markdownEnContentType").value(DEFAULT_MARKDOWN_EN_CONTENT_TYPE))
            .andExpect(jsonPath("$.markdownEn").value(Base64.getEncoder().encodeToString(DEFAULT_MARKDOWN_EN)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.date").value(sameInstant(DEFAULT_DATE)))
            .andExpect(jsonPath("$.badgeContentType").value(DEFAULT_BADGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.badge").value(Base64.getEncoder().encodeToString(DEFAULT_BADGE)))
            .andExpect(jsonPath("$.bannerContentType").value(DEFAULT_BANNER_CONTENT_TYPE))
            .andExpect(jsonPath("$.banner").value(Base64.getEncoder().encodeToString(DEFAULT_BANNER)))
            .andExpect(jsonPath("$.views").value(DEFAULT_VIEWS))
            .andExpect(jsonPath("$.stars").value(DEFAULT_STARS));
    }

    @Test
    @Transactional
    void getNonExistingArticleV1() throws Exception {
        restArticleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }
}
