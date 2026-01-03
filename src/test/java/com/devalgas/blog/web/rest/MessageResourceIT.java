package com.devalgas.blog.web.rest;

import static com.devalgas.blog.domain.MessageAsserts.*;
import static com.devalgas.blog.web.rest.TestUtil.createUpdateProxyForBean;
import static com.devalgas.blog.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.devalgas.blog.IntegrationTest;
import com.devalgas.blog.domain.Message;
import com.devalgas.blog.domain.Subject;
import com.devalgas.blog.repository.MessageRepository;
import com.devalgas.blog.service.MessageService;
import com.devalgas.blog.service.dto.MessageDTO;
import com.devalgas.blog.service.mapper.MessageMapper;
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
 * Integration tests for the {@link MessageResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class MessageResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "235%@q_.2E/{|y";
    private static final String UPDATED_EMAIL = "v!@4./y#_&";

    private static final String DEFAULT_PHONE = "946 971741 8406";
    private static final String UPDATED_PHONE = "+67()713.684-5166";

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    private static final byte[] DEFAULT_FILE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_FILE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_FILE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_FILE_CONTENT_TYPE = "image/png";

    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_LANG_KEY = "AA";
    private static final String UPDATED_LANG_KEY = "BB";

    private static final String DEFAULT_COUNTRY_KEY = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY_KEY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/messages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MessageRepository messageRepository;

    @Mock
    private MessageRepository messageRepositoryMock;

    @Autowired
    private MessageMapper messageMapper;

    @Mock
    private MessageService messageServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMessageMockMvc;

    private Message message;

    private Message insertedMessage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Message createEntity(EntityManager em) {
        Message message = new Message()
            .name(DEFAULT_NAME)
            .email(DEFAULT_EMAIL)
            .phone(DEFAULT_PHONE)
            .message(DEFAULT_MESSAGE)
            .file(DEFAULT_FILE)
            .fileContentType(DEFAULT_FILE_CONTENT_TYPE)
            .date(DEFAULT_DATE)
            .langKey(DEFAULT_LANG_KEY)
            .countryKey(DEFAULT_COUNTRY_KEY);
        // Add required entity
        Subject subject;
        if (TestUtil.findAll(em, Subject.class).isEmpty()) {
            subject = SubjectResourceIT.createEntity();
            em.persist(subject);
            em.flush();
        } else {
            subject = TestUtil.findAll(em, Subject.class).get(0);
        }
        message.setSubject(subject);
        return message;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Message createUpdatedEntity(EntityManager em) {
        Message updatedMessage = new Message()
            .name(UPDATED_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .message(UPDATED_MESSAGE)
            .file(UPDATED_FILE)
            .fileContentType(UPDATED_FILE_CONTENT_TYPE)
            .date(UPDATED_DATE)
            .langKey(UPDATED_LANG_KEY)
            .countryKey(UPDATED_COUNTRY_KEY);
        // Add required entity
        Subject subject;
        if (TestUtil.findAll(em, Subject.class).isEmpty()) {
            subject = SubjectResourceIT.createUpdatedEntity();
            em.persist(subject);
            em.flush();
        } else {
            subject = TestUtil.findAll(em, Subject.class).get(0);
        }
        updatedMessage.setSubject(subject);
        return updatedMessage;
    }

    @BeforeEach
    void initTest() {
        message = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedMessage != null) {
            messageRepository.delete(insertedMessage);
            insertedMessage = null;
        }
    }

    @Test
    @Transactional
    void createMessage() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Message
        MessageDTO messageDTO = messageMapper.toDto(message);
        var returnedMessageDTO = om.readValue(
            restMessageMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(messageDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MessageDTO.class
        );

        // Validate the Message in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMessage = messageMapper.toEntity(returnedMessageDTO);
        assertMessageUpdatableFieldsEquals(returnedMessage, getPersistedMessage(returnedMessage));

        insertedMessage = returnedMessage;
    }

    @Test
    @Transactional
    void createMessageWithExistingId() throws Exception {
        // Create the Message with an existing ID
        message.setId(1L);
        MessageDTO messageDTO = messageMapper.toDto(message);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(messageDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Message in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        message.setName(null);

        // Create the Message, which fails.
        MessageDTO messageDTO = messageMapper.toDto(message);

        restMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(messageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        message.setEmail(null);

        // Create the Message, which fails.
        MessageDTO messageDTO = messageMapper.toDto(message);

        restMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(messageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMessageIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        message.setMessage(null);

        // Create the Message, which fails.
        MessageDTO messageDTO = messageMapper.toDto(message);

        restMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(messageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMessages() throws Exception {
        // Initialize the database
        insertedMessage = messageRepository.saveAndFlush(message);

        // Get all the messageList
        restMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(message.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].fileContentType").value(hasItem(DEFAULT_FILE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].file").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_FILE))))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))))
            .andExpect(jsonPath("$.[*].langKey").value(hasItem(DEFAULT_LANG_KEY)))
            .andExpect(jsonPath("$.[*].countryKey").value(hasItem(DEFAULT_COUNTRY_KEY)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMessagesWithEagerRelationshipsIsEnabled() throws Exception {
        when(messageServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMessageMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(messageServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMessagesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(messageServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMessageMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(messageRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getMessage() throws Exception {
        // Initialize the database
        insertedMessage = messageRepository.saveAndFlush(message);

        // Get the message
        restMessageMockMvc
            .perform(get(ENTITY_API_URL_ID, message.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(message.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE))
            .andExpect(jsonPath("$.fileContentType").value(DEFAULT_FILE_CONTENT_TYPE))
            .andExpect(jsonPath("$.file").value(Base64.getEncoder().encodeToString(DEFAULT_FILE)))
            .andExpect(jsonPath("$.date").value(sameInstant(DEFAULT_DATE)))
            .andExpect(jsonPath("$.langKey").value(DEFAULT_LANG_KEY))
            .andExpect(jsonPath("$.countryKey").value(DEFAULT_COUNTRY_KEY));
    }

    @Test
    @Transactional
    void getNonExistingMessage() throws Exception {
        // Get the message
        restMessageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMessage() throws Exception {
        // Initialize the database
        insertedMessage = messageRepository.saveAndFlush(message);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the message
        Message updatedMessage = messageRepository.findById(message.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMessage are not directly saved in db
        em.detach(updatedMessage);
        updatedMessage
            .name(UPDATED_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .message(UPDATED_MESSAGE)
            .file(UPDATED_FILE)
            .fileContentType(UPDATED_FILE_CONTENT_TYPE)
            .date(UPDATED_DATE)
            .langKey(UPDATED_LANG_KEY)
            .countryKey(UPDATED_COUNTRY_KEY);
        MessageDTO messageDTO = messageMapper.toDto(updatedMessage);

        restMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, messageDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(messageDTO))
            )
            .andExpect(status().isOk());

        // Validate the Message in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMessageToMatchAllProperties(updatedMessage);
    }

    @Test
    @Transactional
    void putNonExistingMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        message.setId(longCount.incrementAndGet());

        // Create the Message
        MessageDTO messageDTO = messageMapper.toDto(message);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, messageDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(messageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Message in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        message.setId(longCount.incrementAndGet());

        // Create the Message
        MessageDTO messageDTO = messageMapper.toDto(message);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(messageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Message in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        message.setId(longCount.incrementAndGet());

        // Create the Message
        MessageDTO messageDTO = messageMapper.toDto(message);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMessageMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(messageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Message in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMessageWithPatch() throws Exception {
        // Initialize the database
        insertedMessage = messageRepository.saveAndFlush(message);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the message using partial update
        Message partialUpdatedMessage = new Message();
        partialUpdatedMessage.setId(message.getId());

        partialUpdatedMessage.phone(UPDATED_PHONE).date(UPDATED_DATE);

        restMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMessage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMessage))
            )
            .andExpect(status().isOk());

        // Validate the Message in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMessageUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedMessage, message), getPersistedMessage(message));
    }

    @Test
    @Transactional
    void fullUpdateMessageWithPatch() throws Exception {
        // Initialize the database
        insertedMessage = messageRepository.saveAndFlush(message);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the message using partial update
        Message partialUpdatedMessage = new Message();
        partialUpdatedMessage.setId(message.getId());

        partialUpdatedMessage
            .name(UPDATED_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .message(UPDATED_MESSAGE)
            .file(UPDATED_FILE)
            .fileContentType(UPDATED_FILE_CONTENT_TYPE)
            .date(UPDATED_DATE)
            .langKey(UPDATED_LANG_KEY)
            .countryKey(UPDATED_COUNTRY_KEY);

        restMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMessage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMessage))
            )
            .andExpect(status().isOk());

        // Validate the Message in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMessageUpdatableFieldsEquals(partialUpdatedMessage, getPersistedMessage(partialUpdatedMessage));
    }

    @Test
    @Transactional
    void patchNonExistingMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        message.setId(longCount.incrementAndGet());

        // Create the Message
        MessageDTO messageDTO = messageMapper.toDto(message);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, messageDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(messageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Message in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        message.setId(longCount.incrementAndGet());

        // Create the Message
        MessageDTO messageDTO = messageMapper.toDto(message);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(messageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Message in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        message.setId(longCount.incrementAndGet());

        // Create the Message
        MessageDTO messageDTO = messageMapper.toDto(message);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMessageMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(messageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Message in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMessage() throws Exception {
        // Initialize the database
        insertedMessage = messageRepository.saveAndFlush(message);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the message
        restMessageMockMvc
            .perform(delete(ENTITY_API_URL_ID, message.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return messageRepository.count();
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

    protected Message getPersistedMessage(Message message) {
        return messageRepository.findById(message.getId()).orElseThrow();
    }

    protected void assertPersistedMessageToMatchAllProperties(Message expectedMessage) {
        assertMessageAllPropertiesEquals(expectedMessage, getPersistedMessage(expectedMessage));
    }

    protected void assertPersistedMessageToMatchUpdatableProperties(Message expectedMessage) {
        assertMessageAllUpdatablePropertiesEquals(expectedMessage, getPersistedMessage(expectedMessage));
    }
}
