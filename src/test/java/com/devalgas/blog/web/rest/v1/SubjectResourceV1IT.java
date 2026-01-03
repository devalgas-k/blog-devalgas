package com.devalgas.blog.web.rest.v1;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.devalgas.blog.IntegrationTest;
import com.devalgas.blog.domain.Subject;
import com.devalgas.blog.repository.SubjectRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
class SubjectResourceV1IT {

    private static final String DEFAULT_TITLE_FR = "AAAAAAAAAA";
    private static final String DEFAULT_TITLE_EN = "AAAAAAAAAA";

    private static final String ENTITY_API_URL = "/api/v1/subjects";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSubjectV1MockMvc;

    private Subject subject;
    private Subject insertedSubject;

    public static Subject createEntity() {
        return new Subject().titleFr(DEFAULT_TITLE_FR).titleEn(DEFAULT_TITLE_EN);
    }

    @BeforeEach
    void initTest() {
        subject = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSubject != null) {
            subjectRepository.delete(insertedSubject);
            insertedSubject = null;
        }
    }

    @Test
    @Transactional
    void getAllSubjectsV1() throws Exception {
        insertedSubject = subjectRepository.saveAndFlush(subject);

        restSubjectV1MockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(subject.getId().intValue())))
            .andExpect(jsonPath("$.[*].titleFr").value(hasItem(DEFAULT_TITLE_FR)))
            .andExpect(jsonPath("$.[*].titleEn").value(hasItem(DEFAULT_TITLE_EN)));
    }
}
