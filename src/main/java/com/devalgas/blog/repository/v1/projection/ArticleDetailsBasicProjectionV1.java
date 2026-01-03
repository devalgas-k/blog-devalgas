package com.devalgas.blog.repository.v1.projection;

import java.time.ZonedDateTime;

public interface ArticleDetailsBasicProjectionV1 {
    Long getId();
    String getLabelFr();
    String getLabelEn();
    byte[] getMarkdownFr();
    String getMarkdownFrContentType();
    byte[] getMarkdownEn();
    String getMarkdownEnContentType();
    ZonedDateTime getDate();
}
