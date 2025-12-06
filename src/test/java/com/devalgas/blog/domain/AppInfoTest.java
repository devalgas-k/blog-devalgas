package com.devalgas.blog.domain;

import static com.devalgas.blog.domain.AppInfoTestSamples.*;
import static com.devalgas.blog.domain.FootersTestSamples.*;
import static com.devalgas.blog.domain.HeadersTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.devalgas.blog.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AppInfoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AppInfo.class);
        AppInfo appInfo1 = getAppInfoSample1();
        AppInfo appInfo2 = new AppInfo();
        assertThat(appInfo1).isNotEqualTo(appInfo2);

        appInfo2.setId(appInfo1.getId());
        assertThat(appInfo1).isEqualTo(appInfo2);

        appInfo2 = getAppInfoSample2();
        assertThat(appInfo1).isNotEqualTo(appInfo2);
    }

    @Test
    void headersTest() {
        AppInfo appInfo = getAppInfoRandomSampleGenerator();
        Headers headersBack = getHeadersRandomSampleGenerator();

        appInfo.setHeaders(headersBack);
        assertThat(appInfo.getHeaders()).isEqualTo(headersBack);

        appInfo.headers(null);
        assertThat(appInfo.getHeaders()).isNull();
    }

    @Test
    void footersTest() {
        AppInfo appInfo = getAppInfoRandomSampleGenerator();
        Footers footersBack = getFootersRandomSampleGenerator();

        appInfo.setFooters(footersBack);
        assertThat(appInfo.getFooters()).isEqualTo(footersBack);

        appInfo.footers(null);
        assertThat(appInfo.getFooters()).isNull();
    }
}
