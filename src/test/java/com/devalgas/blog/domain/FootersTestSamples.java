package com.devalgas.blog.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class FootersTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Footers getFootersSample1() {
        return new Footers().id(1L);
    }

    public static Footers getFootersSample2() {
        return new Footers().id(2L);
    }

    public static Footers getFootersRandomSampleGenerator() {
        return new Footers().id(longCount.incrementAndGet());
    }
}
