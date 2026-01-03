package com.devalgas.blog.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MessageTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Message getMessageSample1() {
        return new Message()
            .id(1L)
            .name("name1")
            .email("email1")
            .phone("phone1")
            .message("message1")
            .langKey("langKey1")
            .countryKey("countryKey1");
    }

    public static Message getMessageSample2() {
        return new Message()
            .id(2L)
            .name("name2")
            .email("email2")
            .phone("phone2")
            .message("message2")
            .langKey("langKey2")
            .countryKey("countryKey2");
    }

    public static Message getMessageRandomSampleGenerator() {
        return new Message()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .message(UUID.randomUUID().toString())
            .langKey(UUID.randomUUID().toString())
            .countryKey(UUID.randomUUID().toString());
    }
}
