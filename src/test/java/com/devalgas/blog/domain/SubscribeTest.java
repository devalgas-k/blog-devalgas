package com.devalgas.blog.domain;

import static com.devalgas.blog.domain.SubscribeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.devalgas.blog.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SubscribeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Subscribe.class);
        Subscribe subscribe1 = getSubscribeSample1();
        Subscribe subscribe2 = new Subscribe();
        assertThat(subscribe1).isNotEqualTo(subscribe2);

        subscribe2.setId(subscribe1.getId());
        assertThat(subscribe1).isEqualTo(subscribe2);

        subscribe2 = getSubscribeSample2();
        assertThat(subscribe1).isNotEqualTo(subscribe2);
    }
}
