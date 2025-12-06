package com.devalgas.blog.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.devalgas.blog.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CategoryArticleDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CategoryArticleDTO.class);
        CategoryArticleDTO categoryArticleDTO1 = new CategoryArticleDTO();
        categoryArticleDTO1.setId(1L);
        CategoryArticleDTO categoryArticleDTO2 = new CategoryArticleDTO();
        assertThat(categoryArticleDTO1).isNotEqualTo(categoryArticleDTO2);
        categoryArticleDTO2.setId(categoryArticleDTO1.getId());
        assertThat(categoryArticleDTO1).isEqualTo(categoryArticleDTO2);
        categoryArticleDTO2.setId(2L);
        assertThat(categoryArticleDTO1).isNotEqualTo(categoryArticleDTO2);
        categoryArticleDTO1.setId(null);
        assertThat(categoryArticleDTO1).isNotEqualTo(categoryArticleDTO2);
    }
}
