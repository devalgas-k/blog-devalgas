package com.devalgas.blog.repository.v1.projection;

import java.time.ZonedDateTime;

public interface ArticleSummaryProjectionV1 {
    Long getId();
    String getLabelFr();
    String getLabelEn();
    ZonedDateTime getDate();
}
