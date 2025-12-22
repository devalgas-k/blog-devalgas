package com.devalgas.blog.web.rest;

import static com.devalgas.blog.domain.CategoryArticleAsserts.*;
import static com.devalgas.blog.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.devalgas.blog.IntegrationTest;
import com.devalgas.blog.domain.CategoryArticle;
import com.devalgas.blog.repository.CategoryArticleRepository;
import com.devalgas.blog.service.dto.CategoryArticleDTO;
import com.devalgas.blog.service.mapper.CategoryArticleMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CategoryArticleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CategoryArticleResourceIT {

    private static final String DEFAULT_LABEL = "AAAAAAAAAA";
    private static final String UPDATED_LABEL = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AA";
    private static final String UPDATED_CODE = "BB";

    private static final byte[] DEFAULT_BADGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_BADGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_BADGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_BADGE_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_DESCRIPTION_FR = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION_FR = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION_EN = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION_EN = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/category-articles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CategoryArticleRepository categoryArticleRepository;

    @Autowired
    private CategoryArticleMapper categoryArticleMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCategoryArticleMockMvc;

    private CategoryArticle categoryArticle;

    private CategoryArticle insertedCategoryArticle;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CategoryArticle createEntity() {
        return new CategoryArticle()
            .label(DEFAULT_LABEL)
            .code(DEFAULT_CODE)
            .badge(DEFAULT_BADGE)
            .badgeContentType(DEFAULT_BADGE_CONTENT_TYPE)
            .descriptionFr(DEFAULT_DESCRIPTION_FR)
            .descriptionEn(DEFAULT_DESCRIPTION_EN);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CategoryArticle createUpdatedEntity() {
        return new CategoryArticle()
            .label(UPDATED_LABEL)
            .code(UPDATED_CODE)
            .badge(UPDATED_BADGE)
            .badgeContentType(UPDATED_BADGE_CONTENT_TYPE)
            .descriptionFr(UPDATED_DESCRIPTION_FR)
            .descriptionEn(UPDATED_DESCRIPTION_EN);
    }

    @BeforeEach
    void initTest() {
        categoryArticle = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCategoryArticle != null) {
            categoryArticleRepository.delete(insertedCategoryArticle);
            insertedCategoryArticle = null;
        }
    }

    @Test
    @Transactional
    void createCategoryArticle() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CategoryArticle
        CategoryArticleDTO categoryArticleDTO = categoryArticleMapper.toDto(categoryArticle);
        var returnedCategoryArticleDTO = om.readValue(
            restCategoryArticleMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(categoryArticleDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CategoryArticleDTO.class
        );

        // Validate the CategoryArticle in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCategoryArticle = categoryArticleMapper.toEntity(returnedCategoryArticleDTO);
        assertCategoryArticleUpdatableFieldsEquals(returnedCategoryArticle, getPersistedCategoryArticle(returnedCategoryArticle));

        insertedCategoryArticle = returnedCategoryArticle;
    }

    @Test
    @Transactional
    void createCategoryArticleWithExistingId() throws Exception {
        // Create the CategoryArticle with an existing ID
        categoryArticle.setId(1L);
        CategoryArticleDTO categoryArticleDTO = categoryArticleMapper.toDto(categoryArticle);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCategoryArticleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(categoryArticleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CategoryArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLabelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        categoryArticle.setLabel(null);

        // Create the CategoryArticle, which fails.
        CategoryArticleDTO categoryArticleDTO = categoryArticleMapper.toDto(categoryArticle);

        restCategoryArticleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(categoryArticleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        categoryArticle.setCode(null);

        // Create the CategoryArticle, which fails.
        CategoryArticleDTO categoryArticleDTO = categoryArticleMapper.toDto(categoryArticle);

        restCategoryArticleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(categoryArticleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCategoryArticles() throws Exception {
        // Initialize the database
        insertedCategoryArticle = categoryArticleRepository.saveAndFlush(categoryArticle);

        // Get all the categoryArticleList
        restCategoryArticleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(categoryArticle.getId().intValue())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].badgeContentType").value(hasItem(DEFAULT_BADGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].badge").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_BADGE))))
            .andExpect(jsonPath("$.[*].descriptionFr").value(hasItem(DEFAULT_DESCRIPTION_FR)))
            .andExpect(jsonPath("$.[*].descriptionEn").value(hasItem(DEFAULT_DESCRIPTION_EN)));
    }

    @Test
    @Transactional
    void getCategoryArticle() throws Exception {
        // Initialize the database
        insertedCategoryArticle = categoryArticleRepository.saveAndFlush(categoryArticle);

        // Get the categoryArticle
        restCategoryArticleMockMvc
            .perform(get(ENTITY_API_URL_ID, categoryArticle.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(categoryArticle.getId().intValue()))
            .andExpect(jsonPath("$.label").value(DEFAULT_LABEL))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.badgeContentType").value(DEFAULT_BADGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.badge").value(Base64.getEncoder().encodeToString(DEFAULT_BADGE)))
            .andExpect(jsonPath("$.descriptionFr").value(DEFAULT_DESCRIPTION_FR))
            .andExpect(jsonPath("$.descriptionEn").value(DEFAULT_DESCRIPTION_EN));
    }

    @Test
    @Transactional
    void getNonExistingCategoryArticle() throws Exception {
        // Get the categoryArticle
        restCategoryArticleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCategoryArticle() throws Exception {
        // Initialize the database
        insertedCategoryArticle = categoryArticleRepository.saveAndFlush(categoryArticle);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the categoryArticle
        CategoryArticle updatedCategoryArticle = categoryArticleRepository.findById(categoryArticle.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCategoryArticle are not directly saved in db
        em.detach(updatedCategoryArticle);
        updatedCategoryArticle
            .label(UPDATED_LABEL)
            .code(UPDATED_CODE)
            .badge(UPDATED_BADGE)
            .badgeContentType(UPDATED_BADGE_CONTENT_TYPE)
            .descriptionFr(UPDATED_DESCRIPTION_FR)
            .descriptionEn(UPDATED_DESCRIPTION_EN);
        CategoryArticleDTO categoryArticleDTO = categoryArticleMapper.toDto(updatedCategoryArticle);

        restCategoryArticleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, categoryArticleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(categoryArticleDTO))
            )
            .andExpect(status().isOk());

        // Validate the CategoryArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCategoryArticleToMatchAllProperties(updatedCategoryArticle);
    }

    @Test
    @Transactional
    void putNonExistingCategoryArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        categoryArticle.setId(longCount.incrementAndGet());

        // Create the CategoryArticle
        CategoryArticleDTO categoryArticleDTO = categoryArticleMapper.toDto(categoryArticle);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCategoryArticleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, categoryArticleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(categoryArticleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CategoryArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCategoryArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        categoryArticle.setId(longCount.incrementAndGet());

        // Create the CategoryArticle
        CategoryArticleDTO categoryArticleDTO = categoryArticleMapper.toDto(categoryArticle);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCategoryArticleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(categoryArticleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CategoryArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCategoryArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        categoryArticle.setId(longCount.incrementAndGet());

        // Create the CategoryArticle
        CategoryArticleDTO categoryArticleDTO = categoryArticleMapper.toDto(categoryArticle);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCategoryArticleMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(categoryArticleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CategoryArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCategoryArticleWithPatch() throws Exception {
        // Initialize the database
        insertedCategoryArticle = categoryArticleRepository.saveAndFlush(categoryArticle);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the categoryArticle using partial update
        CategoryArticle partialUpdatedCategoryArticle = new CategoryArticle();
        partialUpdatedCategoryArticle.setId(categoryArticle.getId());

        partialUpdatedCategoryArticle.label(UPDATED_LABEL);

        restCategoryArticleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCategoryArticle.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCategoryArticle))
            )
            .andExpect(status().isOk());

        // Validate the CategoryArticle in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCategoryArticleUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCategoryArticle, categoryArticle),
            getPersistedCategoryArticle(categoryArticle)
        );
    }

    @Test
    @Transactional
    void fullUpdateCategoryArticleWithPatch() throws Exception {
        // Initialize the database
        insertedCategoryArticle = categoryArticleRepository.saveAndFlush(categoryArticle);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the categoryArticle using partial update
        CategoryArticle partialUpdatedCategoryArticle = new CategoryArticle();
        partialUpdatedCategoryArticle.setId(categoryArticle.getId());

        partialUpdatedCategoryArticle
            .label(UPDATED_LABEL)
            .code(UPDATED_CODE)
            .badge(UPDATED_BADGE)
            .badgeContentType(UPDATED_BADGE_CONTENT_TYPE)
            .descriptionFr(UPDATED_DESCRIPTION_FR)
            .descriptionEn(UPDATED_DESCRIPTION_EN);

        restCategoryArticleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCategoryArticle.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCategoryArticle))
            )
            .andExpect(status().isOk());

        // Validate the CategoryArticle in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCategoryArticleUpdatableFieldsEquals(
            partialUpdatedCategoryArticle,
            getPersistedCategoryArticle(partialUpdatedCategoryArticle)
        );
    }

    @Test
    @Transactional
    void patchNonExistingCategoryArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        categoryArticle.setId(longCount.incrementAndGet());

        // Create the CategoryArticle
        CategoryArticleDTO categoryArticleDTO = categoryArticleMapper.toDto(categoryArticle);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCategoryArticleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, categoryArticleDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(categoryArticleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CategoryArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCategoryArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        categoryArticle.setId(longCount.incrementAndGet());

        // Create the CategoryArticle
        CategoryArticleDTO categoryArticleDTO = categoryArticleMapper.toDto(categoryArticle);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCategoryArticleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(categoryArticleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CategoryArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCategoryArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        categoryArticle.setId(longCount.incrementAndGet());

        // Create the CategoryArticle
        CategoryArticleDTO categoryArticleDTO = categoryArticleMapper.toDto(categoryArticle);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCategoryArticleMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(categoryArticleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CategoryArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCategoryArticle() throws Exception {
        // Initialize the database
        insertedCategoryArticle = categoryArticleRepository.saveAndFlush(categoryArticle);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the categoryArticle
        restCategoryArticleMockMvc
            .perform(delete(ENTITY_API_URL_ID, categoryArticle.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return categoryArticleRepository.count();
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

    protected CategoryArticle getPersistedCategoryArticle(CategoryArticle categoryArticle) {
        return categoryArticleRepository.findById(categoryArticle.getId()).orElseThrow();
    }

    protected void assertPersistedCategoryArticleToMatchAllProperties(CategoryArticle expectedCategoryArticle) {
        assertCategoryArticleAllPropertiesEquals(expectedCategoryArticle, getPersistedCategoryArticle(expectedCategoryArticle));
    }

    protected void assertPersistedCategoryArticleToMatchUpdatableProperties(CategoryArticle expectedCategoryArticle) {
        assertCategoryArticleAllUpdatablePropertiesEquals(expectedCategoryArticle, getPersistedCategoryArticle(expectedCategoryArticle));
    }
}
