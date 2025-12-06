package com.devalgas.blog.web.rest;

import static com.devalgas.blog.domain.AppInfoAsserts.*;
import static com.devalgas.blog.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.devalgas.blog.IntegrationTest;
import com.devalgas.blog.domain.AppInfo;
import com.devalgas.blog.repository.AppInfoRepository;
import com.devalgas.blog.service.dto.AppInfoDTO;
import com.devalgas.blog.service.mapper.AppInfoMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link AppInfoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AppInfoResourceIT {

    private static final String DEFAULT_KEY_INFO = "AAAAAAAAAA";
    private static final String UPDATED_KEY_INFO = "BBBBBBBBBB";

    private static final String DEFAULT_VALUE_INFO = "AAAAAAAAAA";
    private static final String UPDATED_VALUE_INFO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/app-infos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AppInfoRepository appInfoRepository;

    @Autowired
    private AppInfoMapper appInfoMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAppInfoMockMvc;

    private AppInfo appInfo;

    private AppInfo insertedAppInfo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppInfo createEntity() {
        return new AppInfo().keyInfo(DEFAULT_KEY_INFO).valueInfo(DEFAULT_VALUE_INFO);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppInfo createUpdatedEntity() {
        return new AppInfo().keyInfo(UPDATED_KEY_INFO).valueInfo(UPDATED_VALUE_INFO);
    }

    @BeforeEach
    void initTest() {
        appInfo = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAppInfo != null) {
            appInfoRepository.delete(insertedAppInfo);
            insertedAppInfo = null;
        }
    }

    @Test
    @Transactional
    void createAppInfo() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AppInfo
        AppInfoDTO appInfoDTO = appInfoMapper.toDto(appInfo);
        var returnedAppInfoDTO = om.readValue(
            restAppInfoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appInfoDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AppInfoDTO.class
        );

        // Validate the AppInfo in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAppInfo = appInfoMapper.toEntity(returnedAppInfoDTO);
        assertAppInfoUpdatableFieldsEquals(returnedAppInfo, getPersistedAppInfo(returnedAppInfo));

        insertedAppInfo = returnedAppInfo;
    }

    @Test
    @Transactional
    void createAppInfoWithExistingId() throws Exception {
        // Create the AppInfo with an existing ID
        appInfo.setId(1L);
        AppInfoDTO appInfoDTO = appInfoMapper.toDto(appInfo);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAppInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appInfoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AppInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllAppInfos() throws Exception {
        // Initialize the database
        insertedAppInfo = appInfoRepository.saveAndFlush(appInfo);

        // Get all the appInfoList
        restAppInfoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].keyInfo").value(hasItem(DEFAULT_KEY_INFO)))
            .andExpect(jsonPath("$.[*].valueInfo").value(hasItem(DEFAULT_VALUE_INFO)));
    }

    @Test
    @Transactional
    void getAppInfo() throws Exception {
        // Initialize the database
        insertedAppInfo = appInfoRepository.saveAndFlush(appInfo);

        // Get the appInfo
        restAppInfoMockMvc
            .perform(get(ENTITY_API_URL_ID, appInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(appInfo.getId().intValue()))
            .andExpect(jsonPath("$.keyInfo").value(DEFAULT_KEY_INFO))
            .andExpect(jsonPath("$.valueInfo").value(DEFAULT_VALUE_INFO));
    }

    @Test
    @Transactional
    void getNonExistingAppInfo() throws Exception {
        // Get the appInfo
        restAppInfoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAppInfo() throws Exception {
        // Initialize the database
        insertedAppInfo = appInfoRepository.saveAndFlush(appInfo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appInfo
        AppInfo updatedAppInfo = appInfoRepository.findById(appInfo.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAppInfo are not directly saved in db
        em.detach(updatedAppInfo);
        updatedAppInfo.keyInfo(UPDATED_KEY_INFO).valueInfo(UPDATED_VALUE_INFO);
        AppInfoDTO appInfoDTO = appInfoMapper.toDto(updatedAppInfo);

        restAppInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, appInfoDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appInfoDTO))
            )
            .andExpect(status().isOk());

        // Validate the AppInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAppInfoToMatchAllProperties(updatedAppInfo);
    }

    @Test
    @Transactional
    void putNonExistingAppInfo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appInfo.setId(longCount.incrementAndGet());

        // Create the AppInfo
        AppInfoDTO appInfoDTO = appInfoMapper.toDto(appInfo);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, appInfoDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appInfoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAppInfo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appInfo.setId(longCount.incrementAndGet());

        // Create the AppInfo
        AppInfoDTO appInfoDTO = appInfoMapper.toDto(appInfo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(appInfoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAppInfo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appInfo.setId(longCount.incrementAndGet());

        // Create the AppInfo
        AppInfoDTO appInfoDTO = appInfoMapper.toDto(appInfo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppInfoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appInfoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AppInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAppInfoWithPatch() throws Exception {
        // Initialize the database
        insertedAppInfo = appInfoRepository.saveAndFlush(appInfo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appInfo using partial update
        AppInfo partialUpdatedAppInfo = new AppInfo();
        partialUpdatedAppInfo.setId(appInfo.getId());

        partialUpdatedAppInfo.keyInfo(UPDATED_KEY_INFO).valueInfo(UPDATED_VALUE_INFO);

        restAppInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAppInfo.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAppInfo))
            )
            .andExpect(status().isOk());

        // Validate the AppInfo in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAppInfoUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAppInfo, appInfo), getPersistedAppInfo(appInfo));
    }

    @Test
    @Transactional
    void fullUpdateAppInfoWithPatch() throws Exception {
        // Initialize the database
        insertedAppInfo = appInfoRepository.saveAndFlush(appInfo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appInfo using partial update
        AppInfo partialUpdatedAppInfo = new AppInfo();
        partialUpdatedAppInfo.setId(appInfo.getId());

        partialUpdatedAppInfo.keyInfo(UPDATED_KEY_INFO).valueInfo(UPDATED_VALUE_INFO);

        restAppInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAppInfo.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAppInfo))
            )
            .andExpect(status().isOk());

        // Validate the AppInfo in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAppInfoUpdatableFieldsEquals(partialUpdatedAppInfo, getPersistedAppInfo(partialUpdatedAppInfo));
    }

    @Test
    @Transactional
    void patchNonExistingAppInfo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appInfo.setId(longCount.incrementAndGet());

        // Create the AppInfo
        AppInfoDTO appInfoDTO = appInfoMapper.toDto(appInfo);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, appInfoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(appInfoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAppInfo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appInfo.setId(longCount.incrementAndGet());

        // Create the AppInfo
        AppInfoDTO appInfoDTO = appInfoMapper.toDto(appInfo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(appInfoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAppInfo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appInfo.setId(longCount.incrementAndGet());

        // Create the AppInfo
        AppInfoDTO appInfoDTO = appInfoMapper.toDto(appInfo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppInfoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(appInfoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AppInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAppInfo() throws Exception {
        // Initialize the database
        insertedAppInfo = appInfoRepository.saveAndFlush(appInfo);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the appInfo
        restAppInfoMockMvc
            .perform(delete(ENTITY_API_URL_ID, appInfo.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return appInfoRepository.count();
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

    protected AppInfo getPersistedAppInfo(AppInfo appInfo) {
        return appInfoRepository.findById(appInfo.getId()).orElseThrow();
    }

    protected void assertPersistedAppInfoToMatchAllProperties(AppInfo expectedAppInfo) {
        assertAppInfoAllPropertiesEquals(expectedAppInfo, getPersistedAppInfo(expectedAppInfo));
    }

    protected void assertPersistedAppInfoToMatchUpdatableProperties(AppInfo expectedAppInfo) {
        assertAppInfoAllUpdatablePropertiesEquals(expectedAppInfo, getPersistedAppInfo(expectedAppInfo));
    }
}
