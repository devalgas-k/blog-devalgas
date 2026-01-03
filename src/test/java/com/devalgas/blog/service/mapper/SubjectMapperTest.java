package com.devalgas.blog.service.mapper;

import static com.devalgas.blog.domain.SubjectAsserts.*;
import static com.devalgas.blog.domain.SubjectTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SubjectMapperTest {

    private SubjectMapper subjectMapper;

    @BeforeEach
    void setUp() {
        subjectMapper = new SubjectMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSubjectSample1();
        var actual = subjectMapper.toEntity(subjectMapper.toDto(expected));
        assertSubjectAllPropertiesEquals(expected, actual);
    }
}
