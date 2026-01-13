package com.devalgas.blog.web.rest.v1;

import static com.devalgas.blog.web.rest.TestUtil.sameInstant;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.devalgas.blog.IntegrationTest;
import com.devalgas.blog.domain.Message;
import com.devalgas.blog.domain.Subject;
import com.devalgas.blog.repository.MessageRepository;
import com.devalgas.blog.service.dto.MessageDTO;
import com.devalgas.blog.service.dto.SubjectDTO;
import com.devalgas.blog.service.mapper.MessageMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
class MessageResourceV1IT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String DEFAULT_EMAIL = "john@example.com";
    private static final String DEFAULT_PHONE = "123-456-7890";
    private static final String DEFAULT_MESSAGE = "BBBBBBBBBB";
    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final String DEFAULT_LANG_KEY = "AA";
    private static final String DEFAULT_COUNTRY_KEY = "AAAAAAAAAA";

    private static final String ENTITY_API_URL = "/api/v1/messages";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMessageV1MockMvc;

    private Message message;
    private Message insertedMessage;

    public static Message createEntity(EntityManager em) {
        Message message = new Message()
            .name(DEFAULT_NAME)
            .email(DEFAULT_EMAIL)
            .phone(DEFAULT_PHONE)
            .message(DEFAULT_MESSAGE)
            .date(DEFAULT_DATE)
            .langKey(DEFAULT_LANG_KEY)
            .countryKey(DEFAULT_COUNTRY_KEY);
        Subject subject = new Subject().titleFr("TT").titleEn("TT");
        em.persist(subject);
        em.flush();
        message.setSubject(subject);
        return message;
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
    void createMessageV1() throws Exception {
        long databaseSizeBeforeCreate = messageRepository.count();
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setName(DEFAULT_NAME);
        messageDTO.setEmail(DEFAULT_EMAIL);
        messageDTO.setMessage(DEFAULT_MESSAGE);
        messageDTO.setDate(DEFAULT_DATE);
        var subject = message.getSubject();
        SubjectDTO subjectDTO = new SubjectDTO();
        subjectDTO.setId(subject.getId());
        messageDTO.setSubject(subjectDTO);
        var returnedMessageDTO = om.readValue(
            restMessageV1MockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(messageDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MessageDTO.class
        );
        insertedMessage = messageMapper.toEntity(returnedMessageDTO);
        assert (databaseSizeBeforeCreate + 1 == messageRepository.count());
        assert (sameInstant(DEFAULT_DATE).matches(returnedMessageDTO.getDate().toString()));
    }

    @Test
    @Transactional
    void createMessageV1WithInvalidEmail() throws Exception {
        long databaseSizeBeforeCreate = messageRepository.count();
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setName(DEFAULT_NAME);
        messageDTO.setEmail("invalid-email");
        messageDTO.setPhone(DEFAULT_PHONE);
        messageDTO.setMessage(DEFAULT_MESSAGE);
        messageDTO.setDate(DEFAULT_DATE);
        SubjectDTO subjectDTO = new SubjectDTO();
        subjectDTO.setId(message.getSubject().getId());
        messageDTO.setSubject(subjectDTO);
        restMessageV1MockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(messageDTO)))
            .andDo(print())
            .andExpect(status().isBadRequest());
        assert (databaseSizeBeforeCreate == messageRepository.count());
    }

    @Test
    @Transactional
    void createMessageV1WithNullName() throws Exception {
        long databaseSizeBeforeCreate = messageRepository.count();
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setName(null);
        messageDTO.setEmail(DEFAULT_EMAIL);
        messageDTO.setPhone(DEFAULT_PHONE);
        messageDTO.setMessage(DEFAULT_MESSAGE);
        messageDTO.setDate(DEFAULT_DATE);
        SubjectDTO subjectDTO = new SubjectDTO();
        subjectDTO.setId(message.getSubject().getId());
        messageDTO.setSubject(subjectDTO);
        restMessageV1MockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(messageDTO)))
            .andDo(print())
            .andExpect(status().isBadRequest());
        assert (databaseSizeBeforeCreate == messageRepository.count());
    }

    @Test
    @Transactional
    void createMessageV1WithNullSubject() throws Exception {
        long databaseSizeBeforeCreate = messageRepository.count();
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setName(DEFAULT_NAME);
        messageDTO.setEmail(DEFAULT_EMAIL);
        messageDTO.setPhone(DEFAULT_PHONE);
        messageDTO.setMessage(DEFAULT_MESSAGE);
        messageDTO.setDate(DEFAULT_DATE);
        messageDTO.setSubject(null);
        restMessageV1MockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(messageDTO)))
            .andDo(print())
            .andExpect(status().isBadRequest());
        assert (databaseSizeBeforeCreate == messageRepository.count());
    }

    @Test
    @Transactional
    void createMessageV1WithNullMessage() throws Exception {
        long databaseSizeBeforeCreate = messageRepository.count();
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setName(DEFAULT_NAME);
        messageDTO.setEmail(DEFAULT_EMAIL);
        messageDTO.setPhone(DEFAULT_PHONE);
        messageDTO.setMessage(null);
        messageDTO.setDate(DEFAULT_DATE);
        SubjectDTO subjectDTO = new SubjectDTO();
        subjectDTO.setId(message.getSubject().getId());
        messageDTO.setSubject(subjectDTO);
        restMessageV1MockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(messageDTO)))
            .andDo(print())
            .andExpect(status().isBadRequest());
        assert (databaseSizeBeforeCreate == messageRepository.count());
    }

    @Test
    @Transactional
    void createMessageV1WithInvalidPhone() throws Exception {
        long databaseSizeBeforeCreate = messageRepository.count();
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setName(DEFAULT_NAME);
        messageDTO.setEmail(DEFAULT_EMAIL);
        messageDTO.setPhone("abc");
        messageDTO.setMessage(DEFAULT_MESSAGE);
        messageDTO.setDate(DEFAULT_DATE);
        SubjectDTO subjectDTO = new SubjectDTO();
        subjectDTO.setId(message.getSubject().getId());
        messageDTO.setSubject(subjectDTO);
        restMessageV1MockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(messageDTO)))
            .andDo(print())
            .andExpect(status().isBadRequest());
        assert (databaseSizeBeforeCreate == messageRepository.count());
    }

    @Test
    @Transactional
    void createMessageV1WithValidPhone() throws Exception {
        long databaseSizeBeforeCreate = messageRepository.count();
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setName(DEFAULT_NAME);
        messageDTO.setEmail(DEFAULT_EMAIL);
        messageDTO.setPhone("+1 (555) 123-4567");
        messageDTO.setMessage(DEFAULT_MESSAGE);
        messageDTO.setDate(DEFAULT_DATE);
        SubjectDTO subjectDTO = new SubjectDTO();
        subjectDTO.setId(message.getSubject().getId());
        messageDTO.setSubject(subjectDTO);
        restMessageV1MockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(messageDTO)))
            .andDo(print())
            .andExpect(status().isCreated());
        assert (databaseSizeBeforeCreate + 1 == messageRepository.count());
    }

    @Test
    @Transactional
    void createMessageV1WithFile() throws Exception {
        long databaseSizeBeforeCreate = messageRepository.count();
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setName(DEFAULT_NAME);
        messageDTO.setEmail(DEFAULT_EMAIL);
        messageDTO.setMessage(DEFAULT_MESSAGE);
        messageDTO.setDate(DEFAULT_DATE);
        messageDTO.setFile("hello".getBytes(StandardCharsets.UTF_8));
        messageDTO.setFileContentType("text/plain");
        SubjectDTO subjectDTO = new SubjectDTO();
        subjectDTO.setId(message.getSubject().getId());
        messageDTO.setSubject(subjectDTO);
        var returnedMessageDTO = om.readValue(
            restMessageV1MockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(messageDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MessageDTO.class
        );
        insertedMessage = messageMapper.toEntity(returnedMessageDTO);
        assert (databaseSizeBeforeCreate + 1 == messageRepository.count());
        assert (returnedMessageDTO.getFile() != null &&
            returnedMessageDTO.getFile().length == "hello".getBytes(StandardCharsets.UTF_8).length);
    }
}
