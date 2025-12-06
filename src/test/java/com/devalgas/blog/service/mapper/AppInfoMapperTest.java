package com.devalgas.blog.service.mapper;

import static com.devalgas.blog.domain.AppInfoAsserts.*;
import static com.devalgas.blog.domain.AppInfoTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AppInfoMapperTest {

    private AppInfoMapper appInfoMapper;

    @BeforeEach
    void setUp() {
        appInfoMapper = new AppInfoMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAppInfoSample1();
        var actual = appInfoMapper.toEntity(appInfoMapper.toDto(expected));
        assertAppInfoAllPropertiesEquals(expected, actual);
    }
}
