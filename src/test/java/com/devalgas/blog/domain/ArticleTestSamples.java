package com.devalgas.blog.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ArticleTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Article getArticleSample1() {
        return new Article()
            .id(1L)
            .labelEn("labelEn1")
            .labelFr("labelFr1")
            .descriptionFr("descriptionFr1")
            .descriptionEn("descriptionEn1")
            .views(1)
            .stars(1);
    }

    public static Article getArticleSample2() {
        return new Article()
            .id(2L)
            .labelEn("labelEn2")
            .labelFr("labelFr2")
            .descriptionFr("descriptionFr2")
            .descriptionEn("descriptionEn2")
            .views(2)
            .stars(2);
    }

    public static Article getArticleRandomSampleGenerator() {
        return new Article()
            .id(longCount.incrementAndGet())
            .labelEn(UUID.randomUUID().toString())
            .labelFr(UUID.randomUUID().toString())
            .descriptionFr(UUID.randomUUID().toString())
            .descriptionEn(UUID.randomUUID().toString())
            .views(intCount.incrementAndGet())
            .stars(intCount.incrementAndGet());
    }
}
