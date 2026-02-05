package com.devalgas.blog.web.rest.v1;

import static com.devalgas.blog.web.rest.TestUtil.sameInstant;
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

    // Legacy V1 detail route retired: use summary/{id}

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CategoryArticleRepository categoryArticleRepository;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restArticleMockMvc;

    private Article article;
    private Article insertedArticle;
    private CategoryArticle insertedCategory;

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
        if (insertedCategory != null) {
            categoryArticleRepository.delete(insertedCategory);
            insertedCategory = null;
        }
    }

    // Removed: tests for legacy id route on /api/v1/articles/{id}

    @Test
    @Transactional
    void getAllArticlesSummaryV1() throws Exception {
        insertedArticle = articleRepository.saveAndFlush(article);
        restArticleMockMvc
            .perform(get(ENTITY_API_URL + "/summary?sort=id,asc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(article.getId().intValue())))
            .andExpect(jsonPath("$.[*].labelEn").value(hasItem(DEFAULT_LABEL_EN)))
            .andExpect(jsonPath("$.[*].labelFr").value(hasItem(DEFAULT_LABEL_FR)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))));
    }

    @Test
    @Transactional
    void getAllArticlesSummaryV1Headers() throws Exception {
        Article a1 = createEntity();
        a1.setLabelEn(DEFAULT_LABEL_EN + "S1");
        a1.setLabelFr(DEFAULT_LABEL_FR + "S1");
        articleRepository.saveAndFlush(a1);
        Article a2 = createEntity();
        a2.setLabelEn(DEFAULT_LABEL_EN + "S2");
        a2.setLabelFr(DEFAULT_LABEL_FR + "S2");
        articleRepository.saveAndFlush(a2);
        Article a3 = createEntity();
        a3.setLabelEn(DEFAULT_LABEL_EN + "S3");
        a3.setLabelFr(DEFAULT_LABEL_FR + "S3");
        articleRepository.saveAndFlush(a3);
        restArticleMockMvc
            .perform(get(ENTITY_API_URL + "/summary"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header().string("X-Total-Count", "3"))
            .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    @Transactional
    void getAllArticlesSummaryV1IncludesCategories() throws Exception {
        CategoryArticle category = new CategoryArticle().label("IR1N5d").code("IR").descriptionFr("cat fr").descriptionEn("cat en");
        insertedCategory = categoryArticleRepository.saveAndFlush(category);

        Article a1 = createEntity();
        a1.addCategoryArticle(insertedCategory);
        insertedArticle = articleRepository.saveAndFlush(a1);

        restArticleMockMvc
            .perform(get(ENTITY_API_URL + "/summary?sort=id,asc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(insertedArticle.getId().intValue())))
            .andExpect(jsonPath("$.[*].categoryArticles[*].label").value(hasItem("IR1N5d")));
    }

    @Test
    @Transactional
    void getArticleSummaryDetailsV1() throws Exception {
        insertedArticle = articleRepository.saveAndFlush(article);
        restArticleMockMvc
            .perform(get(ENTITY_API_URL + "/summary/{id}", insertedArticle.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(insertedArticle.getId().intValue()))
            .andExpect(jsonPath("$.labelEn").value(DEFAULT_LABEL_EN))
            .andExpect(jsonPath("$.labelFr").value(DEFAULT_LABEL_FR))
            .andExpect(jsonPath("$.markdownFrContentType").value(DEFAULT_MARKDOWN_FR_CONTENT_TYPE))
            .andExpect(jsonPath("$.markdownFr").value(Base64.getEncoder().encodeToString(DEFAULT_MARKDOWN_FR)))
            .andExpect(jsonPath("$.markdownEnContentType").value(DEFAULT_MARKDOWN_EN_CONTENT_TYPE))
            .andExpect(jsonPath("$.markdownEn").value(Base64.getEncoder().encodeToString(DEFAULT_MARKDOWN_EN)))
            .andExpect(jsonPath("$.date").value(sameInstant(DEFAULT_DATE)));
    }

    @Test
    @Transactional
    void getArticleSummaryDetailsV1NotFound() throws Exception {
        restArticleMockMvc.perform(get(ENTITY_API_URL + "/summary/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void getArticleSummaryDetailsV1IncludesCategories() throws Exception {
        CategoryArticle category = new CategoryArticle().label("IR1N5d").code("IR").descriptionFr("cat fr").descriptionEn("cat en");
        insertedCategory = categoryArticleRepository.saveAndFlush(category);

        Article a1 = createEntity();
        a1.addCategoryArticle(insertedCategory);
        insertedArticle = articleRepository.saveAndFlush(a1);

        restArticleMockMvc
            .perform(get(ENTITY_API_URL + "/summary/{id}", insertedArticle.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(insertedArticle.getId().intValue()))
            .andExpect(jsonPath("$.categoryArticles[*].label").value(hasItem("IR1N5d")));
    }

    @Test
    @Transactional
    void getAllArticlesSummaryV1PaginationHeaders() throws Exception {
        Article a1 = createEntity();
        a1.setLabelEn(DEFAULT_LABEL_EN + "1");
        a1.setLabelFr(DEFAULT_LABEL_FR + "1");
        articleRepository.saveAndFlush(a1);
        Article a2 = createEntity();
        a2.setLabelEn(DEFAULT_LABEL_EN + "2");
        a2.setLabelFr(DEFAULT_LABEL_FR + "2");
        articleRepository.saveAndFlush(a2);
        Article a3 = createEntity();
        a3.setLabelEn(DEFAULT_LABEL_EN + "3");
        a3.setLabelFr(DEFAULT_LABEL_FR + "3");
        articleRepository.saveAndFlush(a3);
        restArticleMockMvc
            .perform(get(ENTITY_API_URL + "/summary?page=0&size=2&sort=id,asc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header().string("X-Total-Count", "3"))
            .andExpect(
                org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
                    .string("Link", org.hamcrest.Matchers.containsString("page=1"))
            )
            .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @Transactional
    void getAllArticlesSummaryV1SecondPage() throws Exception {
        Article a1 = createEntity();
        a1.setLabelEn(DEFAULT_LABEL_EN + "1");
        a1.setLabelFr(DEFAULT_LABEL_FR + "1");
        articleRepository.saveAndFlush(a1);
        Article a2 = createEntity();
        a2.setLabelEn(DEFAULT_LABEL_EN + "2");
        a2.setLabelFr(DEFAULT_LABEL_FR + "2");
        articleRepository.saveAndFlush(a2);
        Article a3 = createEntity();
        a3.setLabelEn(DEFAULT_LABEL_EN + "3");
        a3.setLabelFr(DEFAULT_LABEL_FR + "3");
        articleRepository.saveAndFlush(a3);
        restArticleMockMvc
            .perform(get(ENTITY_API_URL + "/summary?page=1&size=2&sort=id,asc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @Transactional
    void getAllArticlesSummaryV1DefaultStatusFilter() throws Exception {
        Article completed1 = createEntity();
        completed1.setLabelEn(DEFAULT_LABEL_EN + "C1");
        completed1.setLabelFr(DEFAULT_LABEL_FR + "C1");
        articleRepository.saveAndFlush(completed1);

        Article completed2 = createEntity();
        completed2.setLabelEn(DEFAULT_LABEL_EN + "C2");
        completed2.setLabelFr(DEFAULT_LABEL_FR + "C2");
        articleRepository.saveAndFlush(completed2);

        Article pending = createEntity();
        pending.setLabelEn(DEFAULT_LABEL_EN + "P1");
        pending.setLabelFr(DEFAULT_LABEL_FR + "P1");
        pending.setStatus(Status.PENDING);
        articleRepository.saveAndFlush(pending);

        restArticleMockMvc
            .perform(get(ENTITY_API_URL + "/summary"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header().string("X-Total-Count", "2"))
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$.[*].labelEn").value(hasItem(DEFAULT_LABEL_EN + "C1")))
            .andExpect(jsonPath("$.[*].labelEn").value(hasItem(DEFAULT_LABEL_EN + "C2")));
    }

    @Test
    @Transactional
    void getAllArticlesSummaryV1WithStatusParam() throws Exception {
        Article completed = createEntity();
        completed.setLabelEn(DEFAULT_LABEL_EN + "C3");
        completed.setLabelFr(DEFAULT_LABEL_FR + "C3");
        articleRepository.saveAndFlush(completed);

        Article pending1 = createEntity();
        pending1.setLabelEn(DEFAULT_LABEL_EN + "P2");
        pending1.setLabelFr(DEFAULT_LABEL_FR + "P2");
        pending1.setStatus(Status.PENDING);
        articleRepository.saveAndFlush(pending1);

        restArticleMockMvc
            .perform(get(ENTITY_API_URL + "/summary?status=PENDING"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header().string("X-Total-Count", "1"))
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$.[0].labelEn").value(DEFAULT_LABEL_EN + "P2"));
    }

    @Test
    @Transactional
    void getAllArticlesSummaryV1InvalidStatusParam() throws Exception {
        restArticleMockMvc.perform(get(ENTITY_API_URL + "/summary?status=UNKNOWN")).andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void getAllArticlesSummaryV1SortedByDateDesc() throws Exception {
        Article aOld = createEntity();
        aOld.setLabelEn(DEFAULT_LABEL_EN + "OLD");
        aOld.setLabelFr(DEFAULT_LABEL_FR + "OLD");
        aOld.setDate(ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC));
        articleRepository.saveAndFlush(aOld);

        Article aMid = createEntity();
        aMid.setLabelEn(DEFAULT_LABEL_EN + "MID");
        aMid.setLabelFr(DEFAULT_LABEL_FR + "MID");
        aMid.setDate(ZonedDateTime.ofInstant(Instant.ofEpochMilli(1000L), ZoneOffset.UTC));
        articleRepository.saveAndFlush(aMid);

        Article aNew = createEntity();
        aNew.setLabelEn(DEFAULT_LABEL_EN + "NEW");
        aNew.setLabelFr(DEFAULT_LABEL_FR + "NEW");
        aNew.setDate(ZonedDateTime.ofInstant(Instant.ofEpochMilli(2000L), ZoneOffset.UTC));
        articleRepository.saveAndFlush(aNew);

        restArticleMockMvc
            .perform(get(ENTITY_API_URL + "/summary?sort=id,asc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[0].labelEn").value(DEFAULT_LABEL_EN + "NEW"))
            .andExpect(jsonPath("$.[1].labelEn").value(DEFAULT_LABEL_EN + "MID"))
            .andExpect(jsonPath("$.[2].labelEn").value(DEFAULT_LABEL_EN + "OLD"));
    }
}
