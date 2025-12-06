package com.devalgas.blog.domain;

import static com.devalgas.blog.domain.AppInfoTestSamples.*;
import static com.devalgas.blog.domain.HeadersTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.devalgas.blog.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class HeadersTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Headers.class);
        Headers headers1 = getHeadersSample1();
        Headers headers2 = new Headers();
        assertThat(headers1).isNotEqualTo(headers2);

        headers2.setId(headers1.getId());
        assertThat(headers1).isEqualTo(headers2);

        headers2 = getHeadersSample2();
        assertThat(headers1).isNotEqualTo(headers2);
    }

    @Test
    void appInfoHeadersTest() {
        Headers headers = getHeadersRandomSampleGenerator();
        AppInfo appInfoBack = getAppInfoRandomSampleGenerator();

        headers.addAppInfoHeaders(appInfoBack);
        assertThat(headers.getAppInfoHeaders()).containsOnly(appInfoBack);
        assertThat(appInfoBack.getHeaders()).isEqualTo(headers);

        headers.removeAppInfoHeaders(appInfoBack);
        assertThat(headers.getAppInfoHeaders()).doesNotContain(appInfoBack);
        assertThat(appInfoBack.getHeaders()).isNull();

        headers.appInfoHeaders(new HashSet<>(Set.of(appInfoBack)));
        assertThat(headers.getAppInfoHeaders()).containsOnly(appInfoBack);
        assertThat(appInfoBack.getHeaders()).isEqualTo(headers);

        headers.setAppInfoHeaders(new HashSet<>());
        assertThat(headers.getAppInfoHeaders()).doesNotContain(appInfoBack);
        assertThat(appInfoBack.getHeaders()).isNull();
    }
}
