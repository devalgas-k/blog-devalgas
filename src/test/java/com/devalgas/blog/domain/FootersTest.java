package com.devalgas.blog.domain;

import static com.devalgas.blog.domain.AppInfoTestSamples.*;
import static com.devalgas.blog.domain.FootersTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.devalgas.blog.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class FootersTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Footers.class);
        Footers footers1 = getFootersSample1();
        Footers footers2 = new Footers();
        assertThat(footers1).isNotEqualTo(footers2);

        footers2.setId(footers1.getId());
        assertThat(footers1).isEqualTo(footers2);

        footers2 = getFootersSample2();
        assertThat(footers1).isNotEqualTo(footers2);
    }

    @Test
    void appInfoFootersTest() {
        Footers footers = getFootersRandomSampleGenerator();
        AppInfo appInfoBack = getAppInfoRandomSampleGenerator();

        footers.addAppInfoFooters(appInfoBack);
        assertThat(footers.getAppInfoFooters()).containsOnly(appInfoBack);
        assertThat(appInfoBack.getFooters()).isEqualTo(footers);

        footers.removeAppInfoFooters(appInfoBack);
        assertThat(footers.getAppInfoFooters()).doesNotContain(appInfoBack);
        assertThat(appInfoBack.getFooters()).isNull();

        footers.appInfoFooters(new HashSet<>(Set.of(appInfoBack)));
        assertThat(footers.getAppInfoFooters()).containsOnly(appInfoBack);
        assertThat(appInfoBack.getFooters()).isEqualTo(footers);

        footers.setAppInfoFooters(new HashSet<>());
        assertThat(footers.getAppInfoFooters()).doesNotContain(appInfoBack);
        assertThat(appInfoBack.getFooters()).isNull();
    }
}
