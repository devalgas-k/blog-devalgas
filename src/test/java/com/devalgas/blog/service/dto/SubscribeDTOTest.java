package com.devalgas.blog.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.devalgas.blog.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SubscribeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SubscribeDTO.class);
        SubscribeDTO subscribeDTO1 = new SubscribeDTO();
        subscribeDTO1.setId(1L);
        SubscribeDTO subscribeDTO2 = new SubscribeDTO();
        assertThat(subscribeDTO1).isNotEqualTo(subscribeDTO2);
        subscribeDTO2.setId(subscribeDTO1.getId());
        assertThat(subscribeDTO1).isEqualTo(subscribeDTO2);
        subscribeDTO2.setId(2L);
        assertThat(subscribeDTO1).isNotEqualTo(subscribeDTO2);
        subscribeDTO1.setId(null);
        assertThat(subscribeDTO1).isNotEqualTo(subscribeDTO2);
    }
}
