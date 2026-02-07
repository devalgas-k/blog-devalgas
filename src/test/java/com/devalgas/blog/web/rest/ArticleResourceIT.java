package com.devalgas.blog.web.rest;

import static com.devalgas.blog.domain.ArticleAsserts.*;
import static com.devalgas.blog.web.rest.TestUtil.createUpdateProxyForBean;
import static com.devalgas.blog.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.devalgas.blog.IntegrationTest;
import com.devalgas.blog.domain.Article;
import com.devalgas.blog.domain.enumeration.Status;
import com.devalgas.blog.repository.ArticleRepository;
import com.devalgas.blog.service.ArticleService;
import com.devalgas.blog.service.dto.ArticleDTO;
import com.devalgas.blog.service.mapper.ArticleMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ArticleResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ArticleResourceIT {

    private static final String DEFAULT_LABEL_EN = "AAAAAAAAAA";
    private static final String UPDATED_LABEL_EN = "BBBBBBBBBB";

    private static final String DEFAULT_LABEL_FR = "AAAAAAAAAA";
    private static final String UPDATED_LABEL_FR = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION_FR = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION_FR = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION_EN = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION_EN = "BBBBBBBBBB";

    private static final byte[] DEFAULT_MARKDOWN_FR = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_MARKDOWN_FR = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_MARKDOWN_FR_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_MARKDOWN_FR_CONTENT_TYPE = "image/png";

    private static final byte[] DEFAULT_MARKDOWN_EN = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_MARKDOWN_EN = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_MARKDOWN_EN_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_MARKDOWN_EN_CONTENT_TYPE = "image/png";

    private static final Status DEFAULT_STATUS = Status.COMPLETED;
    private static final Status UPDATED_STATUS = Status.PENDING;

    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final byte[] DEFAULT_BADGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_BADGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_BADGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_BADGE_CONTENT_TYPE = "image/png";

    private static final byte[] DEFAULT_BANNER = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_BANNER = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_BANNER_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_BANNER_CONTENT_TYPE = "image/png";

    private static final Integer DEFAULT_VIEWS = 1;
    private static final Integer UPDATED_VIEWS = 2;

    private static final Integer DEFAULT_STARS = 1;
    private static final Integer UPDATED_STARS = 2;

    private static final Boolean DEFAULT_DISPLAY = false;
    private static final Boolean UPDATED_DISPLAY = true;

    private static final String ENTITY_API_URL = "/api/articles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ArticleRepository articleRepository;

    @Mock
    private ArticleRepository articleRepositoryMock;

    @Autowired
    private ArticleMapper articleMapper;

    @Mock
    private ArticleService articleServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restArticleMockMvc;

    private Article article;

    private Article insertedArticle;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
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
            .stars(DEFAULT_STARS)
            .display(DEFAULT_DISPLAY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Article createUpdatedEntity() {
        return new Article()
            .labelEn(UPDATED_LABEL_EN)
            .labelFr(UPDATED_LABEL_FR)
            .descriptionFr(UPDATED_DESCRIPTION_FR)
            .descriptionEn(UPDATED_DESCRIPTION_EN)
            .markdownFr(UPDATED_MARKDOWN_FR)
            .markdownFrContentType(UPDATED_MARKDOWN_FR_CONTENT_TYPE)
            .markdownEn(UPDATED_MARKDOWN_EN)
            .markdownEnContentType(UPDATED_MARKDOWN_EN_CONTENT_TYPE)
            .status(UPDATED_STATUS)
            .date(UPDATED_DATE)
            .badge(UPDATED_BADGE)
            .badgeContentType(UPDATED_BADGE_CONTENT_TYPE)
            .banner(UPDATED_BANNER)
            .bannerContentType(UPDATED_BANNER_CONTENT_TYPE)
            .views(UPDATED_VIEWS)
            .stars(UPDATED_STARS)
            .display(UPDATED_DISPLAY);
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
    void createArticle() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Article
        ArticleDTO articleDTO = articleMapper.toDto(article);
        var returnedArticleDTO = om.readValue(
            restArticleMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(articleDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ArticleDTO.class
        );

        // Validate the Article in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedArticle = articleMapper.toEntity(returnedArticleDTO);
        assertArticleUpdatableFieldsEquals(returnedArticle, getPersistedArticle(returnedArticle));

        insertedArticle = returnedArticle;
    }

    @Test
    @Transactional
    void createArticleWithExistingId() throws Exception {
        // Create the Article with an existing ID
        article.setId(1L);
        ArticleDTO articleDTO = articleMapper.toDto(article);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restArticleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(articleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Article in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLabelEnIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        article.setLabelEn(null);

        // Create the Article, which fails.
        ArticleDTO articleDTO = articleMapper.toDto(article);

        restArticleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(articleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLabelFrIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        article.setLabelFr(null);

        // Create the Article, which fails.
        ArticleDTO articleDTO = articleMapper.toDto(article);

        restArticleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(articleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        article.setStatus(null);

        // Create the Article, which fails.
        ArticleDTO articleDTO = articleMapper.toDto(article);

        restArticleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(articleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        article.setDate(null);

        // Create the Article, which fails.
        ArticleDTO articleDTO = articleMapper.toDto(article);

        restArticleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(articleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllArticles() throws Exception {
        // Initialize the database
        insertedArticle = articleRepository.saveAndFlush(article);

        // Get all the articleList
        restArticleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(article.getId().intValue())))
            .andExpect(jsonPath("$.[*].labelEn").value(hasItem(DEFAULT_LABEL_EN)))
            .andExpect(jsonPath("$.[*].labelFr").value(hasItem(DEFAULT_LABEL_FR)))
            .andExpect(jsonPath("$.[*].descriptionFr").value(hasItem(DEFAULT_DESCRIPTION_FR)))
            .andExpect(jsonPath("$.[*].descriptionEn").value(hasItem(DEFAULT_DESCRIPTION_EN)))
            .andExpect(jsonPath("$.[*].markdownFrContentType").value(hasItem(DEFAULT_MARKDOWN_FR_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].markdownFr").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_MARKDOWN_FR))))
            .andExpect(jsonPath("$.[*].markdownEnContentType").value(hasItem(DEFAULT_MARKDOWN_EN_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].markdownEn").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_MARKDOWN_EN))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))))
            .andExpect(jsonPath("$.[*].badgeContentType").value(hasItem(DEFAULT_BADGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].badge").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_BADGE))))
            .andExpect(jsonPath("$.[*].bannerContentType").value(hasItem(DEFAULT_BANNER_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].banner").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_BANNER))))
            .andExpect(jsonPath("$.[*].views").value(hasItem(DEFAULT_VIEWS)))
            .andExpect(jsonPath("$.[*].stars").value(hasItem(DEFAULT_STARS)))
            .andExpect(jsonPath("$.[*].display").value(hasItem(DEFAULT_DISPLAY)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllArticlesWithEagerRelationshipsIsEnabled() throws Exception {
        when(articleServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restArticleMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(articleServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllArticlesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(articleServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restArticleMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(articleRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getArticle() throws Exception {
        // Initialize the database
        insertedArticle = articleRepository.saveAndFlush(article);

        // Get the article
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
            .andExpect(jsonPath("$.stars").value(DEFAULT_STARS))
            .andExpect(jsonPath("$.display").value(DEFAULT_DISPLAY));
    }

    @Test
    @Transactional
    void getNonExistingArticle() throws Exception {
        // Get the article
        restArticleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingArticle() throws Exception {
        // Initialize the database
        insertedArticle = articleRepository.saveAndFlush(article);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the article
        Article updatedArticle = articleRepository.findById(article.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedArticle are not directly saved in db
        em.detach(updatedArticle);
        updatedArticle
            .labelEn(UPDATED_LABEL_EN)
            .labelFr(UPDATED_LABEL_FR)
            .descriptionFr(UPDATED_DESCRIPTION_FR)
            .descriptionEn(UPDATED_DESCRIPTION_EN)
            .markdownFr(UPDATED_MARKDOWN_FR)
            .markdownFrContentType(UPDATED_MARKDOWN_FR_CONTENT_TYPE)
            .markdownEn(UPDATED_MARKDOWN_EN)
            .markdownEnContentType(UPDATED_MARKDOWN_EN_CONTENT_TYPE)
            .status(UPDATED_STATUS)
            .date(UPDATED_DATE)
            .badge(UPDATED_BADGE)
            .badgeContentType(UPDATED_BADGE_CONTENT_TYPE)
            .banner(UPDATED_BANNER)
            .bannerContentType(UPDATED_BANNER_CONTENT_TYPE)
            .views(UPDATED_VIEWS)
            .stars(UPDATED_STARS)
            .display(UPDATED_DISPLAY);
        ArticleDTO articleDTO = articleMapper.toDto(updatedArticle);

        restArticleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, articleDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(articleDTO))
            )
            .andExpect(status().isOk());

        // Validate the Article in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedArticleToMatchAllProperties(updatedArticle);
    }

    @Test
    @Transactional
    void putNonExistingArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        article.setId(longCount.incrementAndGet());

        // Create the Article
        ArticleDTO articleDTO = articleMapper.toDto(article);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restArticleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, articleDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(articleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Article in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        article.setId(longCount.incrementAndGet());

        // Create the Article
        ArticleDTO articleDTO = articleMapper.toDto(article);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restArticleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(articleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Article in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        article.setId(longCount.incrementAndGet());

        // Create the Article
        ArticleDTO articleDTO = articleMapper.toDto(article);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restArticleMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(articleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Article in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateArticleWithPatch() throws Exception {
        // Initialize the database
        insertedArticle = articleRepository.saveAndFlush(article);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the article using partial update
        Article partialUpdatedArticle = new Article();
        partialUpdatedArticle.setId(article.getId());

        partialUpdatedArticle
            .labelEn(UPDATED_LABEL_EN)
            .labelFr(UPDATED_LABEL_FR)
            .markdownEn(UPDATED_MARKDOWN_EN)
            .markdownEnContentType(UPDATED_MARKDOWN_EN_CONTENT_TYPE)
            .status(UPDATED_STATUS);

        restArticleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedArticle.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedArticle))
            )
            .andExpect(status().isOk());

        // Validate the Article in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertArticleUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedArticle, article), getPersistedArticle(article));
    }

    @Test
    @Transactional
    void fullUpdateArticleWithPatch() throws Exception {
        // Initialize the database
        insertedArticle = articleRepository.saveAndFlush(article);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the article using partial update
        Article partialUpdatedArticle = new Article();
        partialUpdatedArticle.setId(article.getId());

        partialUpdatedArticle
            .labelEn(UPDATED_LABEL_EN)
            .labelFr(UPDATED_LABEL_FR)
            .descriptionFr(UPDATED_DESCRIPTION_FR)
            .descriptionEn(UPDATED_DESCRIPTION_EN)
            .markdownFr(UPDATED_MARKDOWN_FR)
            .markdownFrContentType(UPDATED_MARKDOWN_FR_CONTENT_TYPE)
            .markdownEn(UPDATED_MARKDOWN_EN)
            .markdownEnContentType(UPDATED_MARKDOWN_EN_CONTENT_TYPE)
            .status(UPDATED_STATUS)
            .date(UPDATED_DATE)
            .badge(UPDATED_BADGE)
            .badgeContentType(UPDATED_BADGE_CONTENT_TYPE)
            .banner(UPDATED_BANNER)
            .bannerContentType(UPDATED_BANNER_CONTENT_TYPE)
            .views(UPDATED_VIEWS)
            .stars(UPDATED_STARS)
            .display(UPDATED_DISPLAY);

        restArticleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedArticle.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedArticle))
            )
            .andExpect(status().isOk());

        // Validate the Article in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertArticleUpdatableFieldsEquals(partialUpdatedArticle, getPersistedArticle(partialUpdatedArticle));
    }

    @Test
    @Transactional
    void patchNonExistingArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        article.setId(longCount.incrementAndGet());

        // Create the Article
        ArticleDTO articleDTO = articleMapper.toDto(article);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restArticleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, articleDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(articleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Article in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        article.setId(longCount.incrementAndGet());

        // Create the Article
        ArticleDTO articleDTO = articleMapper.toDto(article);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restArticleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(articleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Article in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        article.setId(longCount.incrementAndGet());

        // Create the Article
        ArticleDTO articleDTO = articleMapper.toDto(article);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restArticleMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(articleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Article in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteArticle() throws Exception {
        // Initialize the database
        insertedArticle = articleRepository.saveAndFlush(article);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the article
        restArticleMockMvc
            .perform(delete(ENTITY_API_URL_ID, article.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return articleRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Article getPersistedArticle(Article article) {
        return articleRepository.findById(article.getId()).orElseThrow();
    }

    protected void assertPersistedArticleToMatchAllProperties(Article expectedArticle) {
        assertArticleAllPropertiesEquals(expectedArticle, getPersistedArticle(expectedArticle));
    }

    protected void assertPersistedArticleToMatchUpdatableProperties(Article expectedArticle) {
        assertArticleAllUpdatablePropertiesEquals(expectedArticle, getPersistedArticle(expectedArticle));
    }
}
