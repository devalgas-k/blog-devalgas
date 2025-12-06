package com.devalgas.blog.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class HeadersTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Headers getHeadersSample1() {
        return new Headers().id(1L);
    }

    public static Headers getHeadersSample2() {
        return new Headers().id(2L);
    }

    public static Headers getHeadersRandomSampleGenerator() {
        return new Headers().id(longCount.incrementAndGet());
    }
}
