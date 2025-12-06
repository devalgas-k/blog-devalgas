package com.devalgas.blog.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.devalgas.blog.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FootersDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FootersDTO.class);
        FootersDTO footersDTO1 = new FootersDTO();
        footersDTO1.setId(1L);
        FootersDTO footersDTO2 = new FootersDTO();
        assertThat(footersDTO1).isNotEqualTo(footersDTO2);
        footersDTO2.setId(footersDTO1.getId());
        assertThat(footersDTO1).isEqualTo(footersDTO2);
        footersDTO2.setId(2L);
        assertThat(footersDTO1).isNotEqualTo(footersDTO2);
        footersDTO1.setId(null);
        assertThat(footersDTO1).isNotEqualTo(footersDTO2);
    }
}
