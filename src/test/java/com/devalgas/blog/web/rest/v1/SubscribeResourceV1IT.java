package com.devalgas.blog.web.rest.v1;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.devalgas.blog.IntegrationTest;
import com.devalgas.blog.repository.SubscribeRepository;
import com.devalgas.blog.service.dto.SubscribeDTO;
import com.devalgas.blog.service.v1.MailServiceV1;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SubscribeResourceV1IT {

    private static final String ENTITY_API_URL = "/api/v1/subscribes";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SubscribeRepository subscribeRepository;

    @Autowired
    private MockMvc restSubscribeV1MockMvc;

    @MockitoBean
    private MailServiceV1 mailServiceV1;

    private SubscribeDTO subscribeDTO;

    @BeforeEach
    void initTest() {
        subscribeDTO = new SubscribeDTO();
        subscribeDTO.setEmail("john@example.com");
        subscribeDTO.setLangKey("fr");
        subscribeDTO.setCountryKey("FR");
        org.mockito.Mockito.doNothing().when(mailServiceV1).sendEmailNewSubscribe(org.mockito.ArgumentMatchers.any(SubscribeDTO.class));
    }

    @AfterEach
    void cleanup() {}

    @Test
    @Transactional
    void registerSubscribeV1() throws Exception {
        long databaseSizeBeforeCreate = subscribeRepository.count();
        var returnedSubscribeDTO = om.readValue(
            restSubscribeV1MockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(subscribeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SubscribeDTO.class
        );
        assert (databaseSizeBeforeCreate + 1 == subscribeRepository.count());
        assert (returnedSubscribeDTO.getId() != null);
        assert (returnedSubscribeDTO.getEmail().equals(subscribeDTO.getEmail()));
    }
}
