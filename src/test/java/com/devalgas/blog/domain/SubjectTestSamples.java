package com.devalgas.blog.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SubjectTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Subject getSubjectSample1() {
        return new Subject().id(1L).titleFr("titleFr1").titleEn("titleEn1");
    }

    public static Subject getSubjectSample2() {
        return new Subject().id(2L).titleFr("titleFr2").titleEn("titleEn2");
    }

    public static Subject getSubjectRandomSampleGenerator() {
        return new Subject().id(longCount.incrementAndGet()).titleFr(UUID.randomUUID().toString()).titleEn(UUID.randomUUID().toString());
    }
}
