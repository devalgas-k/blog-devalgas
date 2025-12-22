package com.devalgas.blog.service.mapper;

import static com.devalgas.blog.domain.SubscribeAsserts.*;
import static com.devalgas.blog.domain.SubscribeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SubscribeMapperTest {

    private SubscribeMapper subscribeMapper;

    @BeforeEach
    void setUp() {
        subscribeMapper = new SubscribeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSubscribeSample1();
        var actual = subscribeMapper.toEntity(subscribeMapper.toDto(expected));
        assertSubscribeAllPropertiesEquals(expected, actual);
    }
}
