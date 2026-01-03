package com.devalgas.blog.repository.v1.projection;

import java.time.ZonedDateTime;

public interface CategoryArticleSummaryWithArticlesProjectionV1 {
    Long getId();
    String getLabel();
    Long getArticleId();
    String getArticleLabelFr();
    String getArticleLabelEn();
    ZonedDateTime getArticleDate();
}
