package com.devalgas.blog.service.mapper;

import static com.devalgas.blog.domain.CategoryArticleAsserts.*;
import static com.devalgas.blog.domain.CategoryArticleTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CategoryArticleMapperTest {

    private CategoryArticleMapper categoryArticleMapper;

    @BeforeEach
    void setUp() {
        categoryArticleMapper = new CategoryArticleMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCategoryArticleSample1();
        var actual = categoryArticleMapper.toEntity(categoryArticleMapper.toDto(expected));
        assertCategoryArticleAllPropertiesEquals(expected, actual);
    }
}
