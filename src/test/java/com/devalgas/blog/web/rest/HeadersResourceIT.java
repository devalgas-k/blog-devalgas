package com.devalgas.blog.web.rest;

import static com.devalgas.blog.domain.HeadersAsserts.*;
import static com.devalgas.blog.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.devalgas.blog.IntegrationTest;
import com.devalgas.blog.domain.Headers;
import com.devalgas.blog.repository.HeadersRepository;
import com.devalgas.blog.service.dto.HeadersDTO;
import com.devalgas.blog.service.mapper.HeadersMapper;
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
 * Integration tests for the {@link HeadersResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class HeadersResourceIT {

    private static final byte[] DEFAULT_LOGO_HEADERS = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_LOGO_HEADERS = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_LOGO_HEADERS_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_LOGO_HEADERS_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/headers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private HeadersRepository headersRepository;

    @Autowired
    private HeadersMapper headersMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restHeadersMockMvc;

    private Headers headers;

    private Headers insertedHeaders;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Headers createEntity() {
        return new Headers().logoHeaders(DEFAULT_LOGO_HEADERS).logoHeadersContentType(DEFAULT_LOGO_HEADERS_CONTENT_TYPE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Headers createUpdatedEntity() {
        return new Headers().logoHeaders(UPDATED_LOGO_HEADERS).logoHeadersContentType(UPDATED_LOGO_HEADERS_CONTENT_TYPE);
    }

    @BeforeEach
    void initTest() {
        headers = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedHeaders != null) {
            headersRepository.delete(insertedHeaders);
            insertedHeaders = null;
        }
    }

    @Test
    @Transactional
    void createHeaders() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Headers
        HeadersDTO headersDTO = headersMapper.toDto(headers);
        var returnedHeadersDTO = om.readValue(
            restHeadersMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(headersDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            HeadersDTO.class
        );

        // Validate the Headers in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedHeaders = headersMapper.toEntity(returnedHeadersDTO);
        assertHeadersUpdatableFieldsEquals(returnedHeaders, getPersistedHeaders(returnedHeaders));

        insertedHeaders = returnedHeaders;
    }

    @Test
    @Transactional
    void createHeadersWithExistingId() throws Exception {
        // Create the Headers with an existing ID
        headers.setId(1L);
        HeadersDTO headersDTO = headersMapper.toDto(headers);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restHeadersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(headersDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Headers in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllHeaders() throws Exception {
        // Initialize the database
        insertedHeaders = headersRepository.saveAndFlush(headers);

        // Get all the headersList
        restHeadersMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(headers.getId().intValue())))
            .andExpect(jsonPath("$.[*].logoHeadersContentType").value(hasItem(DEFAULT_LOGO_HEADERS_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].logoHeaders").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_LOGO_HEADERS))));
    }

    @Test
    @Transactional
    void getHeaders() throws Exception {
        // Initialize the database
        insertedHeaders = headersRepository.saveAndFlush(headers);

        // Get the headers
        restHeadersMockMvc
            .perform(get(ENTITY_API_URL_ID, headers.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(headers.getId().intValue()))
            .andExpect(jsonPath("$.logoHeadersContentType").value(DEFAULT_LOGO_HEADERS_CONTENT_TYPE))
            .andExpect(jsonPath("$.logoHeaders").value(Base64.getEncoder().encodeToString(DEFAULT_LOGO_HEADERS)));
    }

    @Test
    @Transactional
    void getNonExistingHeaders() throws Exception {
        // Get the headers
        restHeadersMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingHeaders() throws Exception {
        // Initialize the database
        insertedHeaders = headersRepository.saveAndFlush(headers);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the headers
        Headers updatedHeaders = headersRepository.findById(headers.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedHeaders are not directly saved in db
        em.detach(updatedHeaders);
        updatedHeaders.logoHeaders(UPDATED_LOGO_HEADERS).logoHeadersContentType(UPDATED_LOGO_HEADERS_CONTENT_TYPE);
        HeadersDTO headersDTO = headersMapper.toDto(updatedHeaders);

        restHeadersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, headersDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(headersDTO))
            )
            .andExpect(status().isOk());

        // Validate the Headers in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedHeadersToMatchAllProperties(updatedHeaders);
    }

    @Test
    @Transactional
    void putNonExistingHeaders() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        headers.setId(longCount.incrementAndGet());

        // Create the Headers
        HeadersDTO headersDTO = headersMapper.toDto(headers);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHeadersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, headersDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(headersDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Headers in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchHeaders() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        headers.setId(longCount.incrementAndGet());

        // Create the Headers
        HeadersDTO headersDTO = headersMapper.toDto(headers);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHeadersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(headersDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Headers in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamHeaders() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        headers.setId(longCount.incrementAndGet());

        // Create the Headers
        HeadersDTO headersDTO = headersMapper.toDto(headers);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHeadersMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(headersDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Headers in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateHeadersWithPatch() throws Exception {
        // Initialize the database
        insertedHeaders = headersRepository.saveAndFlush(headers);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the headers using partial update
        Headers partialUpdatedHeaders = new Headers();
        partialUpdatedHeaders.setId(headers.getId());

        restHeadersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHeaders.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedHeaders))
            )
            .andExpect(status().isOk());

        // Validate the Headers in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertHeadersUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedHeaders, headers), getPersistedHeaders(headers));
    }

    @Test
    @Transactional
    void fullUpdateHeadersWithPatch() throws Exception {
        // Initialize the database
        insertedHeaders = headersRepository.saveAndFlush(headers);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the headers using partial update
        Headers partialUpdatedHeaders = new Headers();
        partialUpdatedHeaders.setId(headers.getId());

        partialUpdatedHeaders.logoHeaders(UPDATED_LOGO_HEADERS).logoHeadersContentType(UPDATED_LOGO_HEADERS_CONTENT_TYPE);

        restHeadersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHeaders.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedHeaders))
            )
            .andExpect(status().isOk());

        // Validate the Headers in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertHeadersUpdatableFieldsEquals(partialUpdatedHeaders, getPersistedHeaders(partialUpdatedHeaders));
    }

    @Test
    @Transactional
    void patchNonExistingHeaders() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        headers.setId(longCount.incrementAndGet());

        // Create the Headers
        HeadersDTO headersDTO = headersMapper.toDto(headers);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHeadersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, headersDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(headersDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Headers in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchHeaders() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        headers.setId(longCount.incrementAndGet());

        // Create the Headers
        HeadersDTO headersDTO = headersMapper.toDto(headers);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHeadersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(headersDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Headers in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamHeaders() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        headers.setId(longCount.incrementAndGet());

        // Create the Headers
        HeadersDTO headersDTO = headersMapper.toDto(headers);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHeadersMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(headersDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Headers in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteHeaders() throws Exception {
        // Initialize the database
        insertedHeaders = headersRepository.saveAndFlush(headers);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the headers
        restHeadersMockMvc
            .perform(delete(ENTITY_API_URL_ID, headers.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return headersRepository.count();
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

    protected Headers getPersistedHeaders(Headers headers) {
        return headersRepository.findById(headers.getId()).orElseThrow();
    }

    protected void assertPersistedHeadersToMatchAllProperties(Headers expectedHeaders) {
        assertHeadersAllPropertiesEquals(expectedHeaders, getPersistedHeaders(expectedHeaders));
    }

    protected void assertPersistedHeadersToMatchUpdatableProperties(Headers expectedHeaders) {
        assertHeadersAllUpdatablePropertiesEquals(expectedHeaders, getPersistedHeaders(expectedHeaders));
    }
}
