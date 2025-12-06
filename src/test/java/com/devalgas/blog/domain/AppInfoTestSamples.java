package com.devalgas.blog.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AppInfoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static AppInfo getAppInfoSample1() {
        return new AppInfo().id(1L).keyInfo("keyInfo1").valueInfo("valueInfo1");
    }

    public static AppInfo getAppInfoSample2() {
        return new AppInfo().id(2L).keyInfo("keyInfo2").valueInfo("valueInfo2");
    }

    public static AppInfo getAppInfoRandomSampleGenerator() {
        return new AppInfo().id(longCount.incrementAndGet()).keyInfo(UUID.randomUUID().toString()).valueInfo(UUID.randomUUID().toString());
    }
}
