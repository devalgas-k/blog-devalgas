package com.devalgas.blog.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.devalgas.blog.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HeadersDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(HeadersDTO.class);
        HeadersDTO headersDTO1 = new HeadersDTO();
        headersDTO1.setId(1L);
        HeadersDTO headersDTO2 = new HeadersDTO();
        assertThat(headersDTO1).isNotEqualTo(headersDTO2);
        headersDTO2.setId(headersDTO1.getId());
        assertThat(headersDTO1).isEqualTo(headersDTO2);
        headersDTO2.setId(2L);
        assertThat(headersDTO1).isNotEqualTo(headersDTO2);
        headersDTO1.setId(null);
        assertThat(headersDTO1).isNotEqualTo(headersDTO2);
    }
}
