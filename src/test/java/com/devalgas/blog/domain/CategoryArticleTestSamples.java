package com.devalgas.blog.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CategoryArticleTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static CategoryArticle getCategoryArticleSample1() {
        return new CategoryArticle().id(1L).label("label1").code("code1").descriptionFr("descriptionFr1").descriptionEn("descriptionEn1");
    }

    public static CategoryArticle getCategoryArticleSample2() {
        return new CategoryArticle().id(2L).label("label2").code("code2").descriptionFr("descriptionFr2").descriptionEn("descriptionEn2");
    }

    public static CategoryArticle getCategoryArticleRandomSampleGenerator() {
        return new CategoryArticle()
            .id(longCount.incrementAndGet())
            .label(UUID.randomUUID().toString())
            .code(UUID.randomUUID().toString())
            .descriptionFr(UUID.randomUUID().toString())
            .descriptionEn(UUID.randomUUID().toString());
    }
}
