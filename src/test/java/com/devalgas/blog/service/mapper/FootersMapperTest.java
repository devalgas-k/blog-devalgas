package com.devalgas.blog.service.mapper;

import static com.devalgas.blog.domain.FootersAsserts.*;
import static com.devalgas.blog.domain.FootersTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FootersMapperTest {

    private FootersMapper footersMapper;

    @BeforeEach
    void setUp() {
        footersMapper = new FootersMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getFootersSample1();
        var actual = footersMapper.toEntity(footersMapper.toDto(expected));
        assertFootersAllPropertiesEquals(expected, actual);
    }
}
