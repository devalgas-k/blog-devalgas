package com.devalgas.blog.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SubscribeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Subscribe getSubscribeSample1() {
        return new Subscribe().id(1L).email("email1").langKey("langKey1").countryKey("countryKey1");
    }

    public static Subscribe getSubscribeSample2() {
        return new Subscribe().id(2L).email("email2").langKey("langKey2").countryKey("countryKey2");
    }

    public static Subscribe getSubscribeRandomSampleGenerator() {
        return new Subscribe()
            .id(longCount.incrementAndGet())
            .email(UUID.randomUUID().toString())
            .langKey(UUID.randomUUID().toString())
            .countryKey(UUID.randomUUID().toString());
    }
}
