package com.devalgas.blog.service.dto.v1;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

public class ArticleDetailsBasicDTOV1 {

    private Long id;
    private String labelFr;
    private String labelEn;
    private String descriptionFr;
    private String descriptionEn;
    private byte[] markdownFr;
    private String markdownFrContentType;
    private byte[] markdownEn;
    private String markdownEnContentType;
    private ZonedDateTime date;
    private Set<CategoryLabelBasicDTOV1> categoryArticles = new HashSet<>();

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

    public String getDescriptionFr() {
        return descriptionFr;
    }

    public void setDescriptionFr(String descriptionFr) {
        this.descriptionFr = descriptionFr;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public byte[] getMarkdownFr() {
        return markdownFr;
    }

    public void setMarkdownFr(byte[] markdownFr) {
        this.markdownFr = markdownFr;
    }

    public String getMarkdownFrContentType() {
        return markdownFrContentType;
    }

    public void setMarkdownFrContentType(String markdownFrContentType) {
        this.markdownFrContentType = markdownFrContentType;
    }

    public byte[] getMarkdownEn() {
        return markdownEn;
    }

    public void setMarkdownEn(byte[] markdownEn) {
        this.markdownEn = markdownEn;
    }

    public String getMarkdownEnContentType() {
        return markdownEnContentType;
    }

    public void setMarkdownEnContentType(String markdownEnContentType) {
        this.markdownEnContentType = markdownEnContentType;
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
