package com.devalgas.blog.service.dto.v1;

import com.devalgas.blog.service.dto.v1.CategoryLabelBasicDTOV1;
import java.time.ZonedDateTime;
import java.util.Set;

public class ArticleSummaryBasicDTOV1 {

    private Long id;
    private String labelFr;
    private String labelEn;
    private ZonedDateTime date;
    private Set<CategoryLabelBasicDTOV1> categoryArticles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabelFr() {
        return labelFr;
    }

    public void setLabelFr(String labelFr) {
        this.labelFr = labelFr;
    }

    public String getLabelEn() {
        return labelEn;
    }

    public void setLabelEn(String labelEn) {
        this.labelEn = labelEn;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public Set<CategoryLabelBasicDTOV1> getCategoryArticles() {
        return categoryArticles;
    }

    public void setCategoryArticles(Set<CategoryLabelBasicDTOV1> categoryArticles) {
        this.categoryArticles = categoryArticles;
    }
}
