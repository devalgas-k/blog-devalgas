package com.devalgas.blog.service.mapper;

import static com.devalgas.blog.domain.HeadersAsserts.*;
import static com.devalgas.blog.domain.HeadersTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HeadersMapperTest {

    private HeadersMapper headersMapper;

    @BeforeEach
    void setUp() {
        headersMapper = new HeadersMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getHeadersSample1();
        var actual = headersMapper.toEntity(headersMapper.toDto(expected));
        assertHeadersAllPropertiesEquals(expected, actual);
    }
}
