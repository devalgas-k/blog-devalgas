package com.devalgas.blog.web.rest;

import static com.devalgas.blog.domain.FootersAsserts.*;
import static com.devalgas.blog.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.devalgas.blog.IntegrationTest;
import com.devalgas.blog.domain.Footers;
import com.devalgas.blog.repository.FootersRepository;
import com.devalgas.blog.service.dto.FootersDTO;
import com.devalgas.blog.service.mapper.FootersMapper;
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
 * Integration tests for the {@link FootersResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FootersResourceIT {

    private static final byte[] DEFAULT_LOGO_FOOTERS = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_LOGO_FOOTERS = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_LOGO_FOOTERS_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_LOGO_FOOTERS_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/footers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FootersRepository footersRepository;

    @Autowired
    private FootersMapper footersMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFootersMockMvc;

    private Footers footers;

    private Footers insertedFooters;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Footers createEntity() {
        return new Footers().logoFooters(DEFAULT_LOGO_FOOTERS).logoFootersContentType(DEFAULT_LOGO_FOOTERS_CONTENT_TYPE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Footers createUpdatedEntity() {
        return new Footers().logoFooters(UPDATED_LOGO_FOOTERS).logoFootersContentType(UPDATED_LOGO_FOOTERS_CONTENT_TYPE);
    }

    @BeforeEach
    void initTest() {
        footers = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedFooters != null) {
            footersRepository.delete(insertedFooters);
            insertedFooters = null;
        }
    }

    @Test
    @Transactional
    void createFooters() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Footers
        FootersDTO footersDTO = footersMapper.toDto(footers);
        var returnedFootersDTO = om.readValue(
            restFootersMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(footersDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            FootersDTO.class
        );

        // Validate the Footers in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedFooters = footersMapper.toEntity(returnedFootersDTO);
        assertFootersUpdatableFieldsEquals(returnedFooters, getPersistedFooters(returnedFooters));

        insertedFooters = returnedFooters;
    }

    @Test
    @Transactional
    void createFootersWithExistingId() throws Exception {
        // Create the Footers with an existing ID
        footers.setId(1L);
        FootersDTO footersDTO = footersMapper.toDto(footers);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFootersMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(footersDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Footers in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllFooters() throws Exception {
        // Initialize the database
        insertedFooters = footersRepository.saveAndFlush(footers);

        // Get all the footersList
        restFootersMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(footers.getId().intValue())))
            .andExpect(jsonPath("$.[*].logoFootersContentType").value(hasItem(DEFAULT_LOGO_FOOTERS_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].logoFooters").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_LOGO_FOOTERS))));
    }

    @Test
    @Transactional
    void getFooters() throws Exception {
        // Initialize the database
        insertedFooters = footersRepository.saveAndFlush(footers);

        // Get the footers
        restFootersMockMvc
            .perform(get(ENTITY_API_URL_ID, footers.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(footers.getId().intValue()))
            .andExpect(jsonPath("$.logoFootersContentType").value(DEFAULT_LOGO_FOOTERS_CONTENT_TYPE))
            .andExpect(jsonPath("$.logoFooters").value(Base64.getEncoder().encodeToString(DEFAULT_LOGO_FOOTERS)));
    }

    @Test
    @Transactional
    void getNonExistingFooters() throws Exception {
        // Get the footers
        restFootersMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFooters() throws Exception {
        // Initialize the database
        insertedFooters = footersRepository.saveAndFlush(footers);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the footers
        Footers updatedFooters = footersRepository.findById(footers.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFooters are not directly saved in db
        em.detach(updatedFooters);
        updatedFooters.logoFooters(UPDATED_LOGO_FOOTERS).logoFootersContentType(UPDATED_LOGO_FOOTERS_CONTENT_TYPE);
        FootersDTO footersDTO = footersMapper.toDto(updatedFooters);

        restFootersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, footersDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(footersDTO))
            )
            .andExpect(status().isOk());

        // Validate the Footers in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFootersToMatchAllProperties(updatedFooters);
    }

    @Test
    @Transactional
    void putNonExistingFooters() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        footers.setId(longCount.incrementAndGet());

        // Create the Footers
        FootersDTO footersDTO = footersMapper.toDto(footers);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFootersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, footersDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(footersDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Footers in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFooters() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        footers.setId(longCount.incrementAndGet());

        // Create the Footers
        FootersDTO footersDTO = footersMapper.toDto(footers);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFootersMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(footersDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Footers in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFooters() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        footers.setId(longCount.incrementAndGet());

        // Create the Footers
        FootersDTO footersDTO = footersMapper.toDto(footers);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFootersMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(footersDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Footers in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFootersWithPatch() throws Exception {
        // Initialize the database
        insertedFooters = footersRepository.saveAndFlush(footers);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the footers using partial update
        Footers partialUpdatedFooters = new Footers();
        partialUpdatedFooters.setId(footers.getId());

        restFootersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFooters.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFooters))
            )
            .andExpect(status().isOk());

        // Validate the Footers in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFootersUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedFooters, footers), getPersistedFooters(footers));
    }

    @Test
    @Transactional
    void fullUpdateFootersWithPatch() throws Exception {
        // Initialize the database
        insertedFooters = footersRepository.saveAndFlush(footers);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the footers using partial update
        Footers partialUpdatedFooters = new Footers();
        partialUpdatedFooters.setId(footers.getId());

        partialUpdatedFooters.logoFooters(UPDATED_LOGO_FOOTERS).logoFootersContentType(UPDATED_LOGO_FOOTERS_CONTENT_TYPE);

        restFootersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFooters.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFooters))
            )
            .andExpect(status().isOk());

        // Validate the Footers in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFootersUpdatableFieldsEquals(partialUpdatedFooters, getPersistedFooters(partialUpdatedFooters));
    }

    @Test
    @Transactional
    void patchNonExistingFooters() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        footers.setId(longCount.incrementAndGet());

        // Create the Footers
        FootersDTO footersDTO = footersMapper.toDto(footers);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFootersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, footersDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(footersDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Footers in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFooters() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        footers.setId(longCount.incrementAndGet());

        // Create the Footers
        FootersDTO footersDTO = footersMapper.toDto(footers);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFootersMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(footersDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Footers in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFooters() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        footers.setId(longCount.incrementAndGet());

        // Create the Footers
        FootersDTO footersDTO = footersMapper.toDto(footers);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFootersMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(footersDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Footers in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFooters() throws Exception {
        // Initialize the database
        insertedFooters = footersRepository.saveAndFlush(footers);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the footers
        restFootersMockMvc
            .perform(delete(ENTITY_API_URL_ID, footers.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return footersRepository.count();
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

    protected Footers getPersistedFooters(Footers footers) {
        return footersRepository.findById(footers.getId()).orElseThrow();
    }

    protected void assertPersistedFootersToMatchAllProperties(Footers expectedFooters) {
        assertFootersAllPropertiesEquals(expectedFooters, getPersistedFooters(expectedFooters));
    }

    protected void assertPersistedFootersToMatchUpdatableProperties(Footers expectedFooters) {
        assertFootersAllUpdatablePropertiesEquals(expectedFooters, getPersistedFooters(expectedFooters));
    }
}
