package com.devalgas.blog.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.devalgas.blog.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AppInfoDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AppInfoDTO.class);
        AppInfoDTO appInfoDTO1 = new AppInfoDTO();
        appInfoDTO1.setId(1L);
        AppInfoDTO appInfoDTO2 = new AppInfoDTO();
        assertThat(appInfoDTO1).isNotEqualTo(appInfoDTO2);
        appInfoDTO2.setId(appInfoDTO1.getId());
        assertThat(appInfoDTO1).isEqualTo(appInfoDTO2);
        appInfoDTO2.setId(2L);
        assertThat(appInfoDTO1).isNotEqualTo(appInfoDTO2);
        appInfoDTO1.setId(null);
        assertThat(appInfoDTO1).isNotEqualTo(appInfoDTO2);
    }
}
