package com.devalgas.blog.service.mapper;

import static com.devalgas.blog.domain.ArticleAsserts.*;
import static com.devalgas.blog.domain.ArticleTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ArticleMapperTest {

    private ArticleMapper articleMapper;

    @BeforeEach
    void setUp() {
        articleMapper = new ArticleMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getArticleSample1();
        var actual = articleMapper.toEntity(articleMapper.toDto(expected));
        assertArticleAllPropertiesEquals(expected, actual);
    }
}
