package com.devalgas.blog.web.rest;

import static com.devalgas.blog.domain.SubscribeAsserts.*;
import static com.devalgas.blog.web.rest.TestUtil.createUpdateProxyForBean;
import static com.devalgas.blog.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.devalgas.blog.IntegrationTest;
import com.devalgas.blog.domain.Subscribe;
import com.devalgas.blog.repository.SubscribeRepository;
import com.devalgas.blog.service.dto.SubscribeDTO;
import com.devalgas.blog.service.mapper.SubscribeMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
 * Integration tests for the {@link SubscribeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SubscribeResourceIT {

    private static final String DEFAULT_EMAIL = "+}@f6a.Lp5}/p";
    private static final String UPDATED_EMAIL = "t{T!t@L'D.mu1N";

    private static final String DEFAULT_LANG_KEY = "AA";
    private static final String UPDATED_LANG_KEY = "BB";

    private static final String DEFAULT_COUNTRY_KEY = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY_KEY = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/subscribes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SubscribeRepository subscribeRepository;

    @Autowired
    private SubscribeMapper subscribeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSubscribeMockMvc;

    private Subscribe subscribe;

    private Subscribe insertedSubscribe;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Subscribe createEntity() {
        return new Subscribe().email(DEFAULT_EMAIL).langKey(DEFAULT_LANG_KEY).countryKey(DEFAULT_COUNTRY_KEY).date(DEFAULT_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Subscribe createUpdatedEntity() {
        return new Subscribe().email(UPDATED_EMAIL).langKey(UPDATED_LANG_KEY).countryKey(UPDATED_COUNTRY_KEY).date(UPDATED_DATE);
    }

    @BeforeEach
    void initTest() {
        subscribe = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSubscribe != null) {
            subscribeRepository.delete(insertedSubscribe);
            insertedSubscribe = null;
        }
    }

    @Test
    @Transactional
    void createSubscribe() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Subscribe
        SubscribeDTO subscribeDTO = subscribeMapper.toDto(subscribe);
        var returnedSubscribeDTO = om.readValue(
            restSubscribeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subscribeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SubscribeDTO.class
        );

        // Validate the Subscribe in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSubscribe = subscribeMapper.toEntity(returnedSubscribeDTO);
        assertSubscribeUpdatableFieldsEquals(returnedSubscribe, getPersistedSubscribe(returnedSubscribe));

        insertedSubscribe = returnedSubscribe;
    }

    @Test
    @Transactional
    void createSubscribeWithExistingId() throws Exception {
        // Create the Subscribe with an existing ID
        subscribe.setId(1L);
        SubscribeDTO subscribeDTO = subscribeMapper.toDto(subscribe);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSubscribeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subscribeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Subscribe in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        subscribe.setEmail(null);

        // Create the Subscribe, which fails.
        SubscribeDTO subscribeDTO = subscribeMapper.toDto(subscribe);

        restSubscribeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subscribeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSubscribes() throws Exception {
        // Initialize the database
        insertedSubscribe = subscribeRepository.saveAndFlush(subscribe);

        // Get all the subscribeList
        restSubscribeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(subscribe.getId().intValue())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].langKey").value(hasItem(DEFAULT_LANG_KEY)))
            .andExpect(jsonPath("$.[*].countryKey").value(hasItem(DEFAULT_COUNTRY_KEY)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))));
    }

    @Test
    @Transactional
    void getSubscribe() throws Exception {
        // Initialize the database
        insertedSubscribe = subscribeRepository.saveAndFlush(subscribe);

        // Get the subscribe
        restSubscribeMockMvc
            .perform(get(ENTITY_API_URL_ID, subscribe.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(subscribe.getId().intValue()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.langKey").value(DEFAULT_LANG_KEY))
            .andExpect(jsonPath("$.countryKey").value(DEFAULT_COUNTRY_KEY))
            .andExpect(jsonPath("$.date").value(sameInstant(DEFAULT_DATE)));
    }

    @Test
    @Transactional
    void getNonExistingSubscribe() throws Exception {
        // Get the subscribe
        restSubscribeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSubscribe() throws Exception {
        // Initialize the database
        insertedSubscribe = subscribeRepository.saveAndFlush(subscribe);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the subscribe
        Subscribe updatedSubscribe = subscribeRepository.findById(subscribe.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSubscribe are not directly saved in db
        em.detach(updatedSubscribe);
        updatedSubscribe.email(UPDATED_EMAIL).langKey(UPDATED_LANG_KEY).countryKey(UPDATED_COUNTRY_KEY).date(UPDATED_DATE);
        SubscribeDTO subscribeDTO = subscribeMapper.toDto(updatedSubscribe);

        restSubscribeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, subscribeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(subscribeDTO))
            )
            .andExpect(status().isOk());

        // Validate the Subscribe in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSubscribeToMatchAllProperties(updatedSubscribe);
    }

    @Test
    @Transactional
    void putNonExistingSubscribe() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subscribe.setId(longCount.incrementAndGet());

        // Create the Subscribe
        SubscribeDTO subscribeDTO = subscribeMapper.toDto(subscribe);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSubscribeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, subscribeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(subscribeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subscribe in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSubscribe() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subscribe.setId(longCount.incrementAndGet());

        // Create the Subscribe
        SubscribeDTO subscribeDTO = subscribeMapper.toDto(subscribe);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubscribeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(subscribeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subscribe in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSubscribe() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subscribe.setId(longCount.incrementAndGet());

        // Create the Subscribe
        SubscribeDTO subscribeDTO = subscribeMapper.toDto(subscribe);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubscribeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subscribeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Subscribe in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSubscribeWithPatch() throws Exception {
        // Initialize the database
        insertedSubscribe = subscribeRepository.saveAndFlush(subscribe);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the subscribe using partial update
        Subscribe partialUpdatedSubscribe = new Subscribe();
        partialUpdatedSubscribe.setId(subscribe.getId());

        partialUpdatedSubscribe.countryKey(UPDATED_COUNTRY_KEY).date(UPDATED_DATE);

        restSubscribeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSubscribe.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSubscribe))
            )
            .andExpect(status().isOk());

        // Validate the Subscribe in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSubscribeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSubscribe, subscribe),
            getPersistedSubscribe(subscribe)
        );
    }

    @Test
    @Transactional
    void fullUpdateSubscribeWithPatch() throws Exception {
        // Initialize the database
        insertedSubscribe = subscribeRepository.saveAndFlush(subscribe);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the subscribe using partial update
        Subscribe partialUpdatedSubscribe = new Subscribe();
        partialUpdatedSubscribe.setId(subscribe.getId());

        partialUpdatedSubscribe.email(UPDATED_EMAIL).langKey(UPDATED_LANG_KEY).countryKey(UPDATED_COUNTRY_KEY).date(UPDATED_DATE);

        restSubscribeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSubscribe.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSubscribe))
            )
            .andExpect(status().isOk());

        // Validate the Subscribe in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSubscribeUpdatableFieldsEquals(partialUpdatedSubscribe, getPersistedSubscribe(partialUpdatedSubscribe));
    }

    @Test
    @Transactional
    void patchNonExistingSubscribe() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subscribe.setId(longCount.incrementAndGet());

        // Create the Subscribe
        SubscribeDTO subscribeDTO = subscribeMapper.toDto(subscribe);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSubscribeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, subscribeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(subscribeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subscribe in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSubscribe() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subscribe.setId(longCount.incrementAndGet());

        // Create the Subscribe
        SubscribeDTO subscribeDTO = subscribeMapper.toDto(subscribe);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubscribeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(subscribeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subscribe in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSubscribe() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        subscribe.setId(longCount.incrementAndGet());

        // Create the Subscribe
        SubscribeDTO subscribeDTO = subscribeMapper.toDto(subscribe);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubscribeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(subscribeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Subscribe in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSubscribe() throws Exception {
        // Initialize the database
        insertedSubscribe = subscribeRepository.saveAndFlush(subscribe);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the subscribe
        restSubscribeMockMvc
            .perform(delete(ENTITY_API_URL_ID, subscribe.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return subscribeRepository.count();
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

    protected Subscribe getPersistedSubscribe(Subscribe subscribe) {
        return subscribeRepository.findById(subscribe.getId()).orElseThrow();
    }

    protected void assertPersistedSubscribeToMatchAllProperties(Subscribe expectedSubscribe) {
        assertSubscribeAllPropertiesEquals(expectedSubscribe, getPersistedSubscribe(expectedSubscribe));
    }

    protected void assertPersistedSubscribeToMatchUpdatableProperties(Subscribe expectedSubscribe) {
        assertSubscribeAllUpdatablePropertiesEquals(expectedSubscribe, getPersistedSubscribe(expectedSubscribe));
    }
}
