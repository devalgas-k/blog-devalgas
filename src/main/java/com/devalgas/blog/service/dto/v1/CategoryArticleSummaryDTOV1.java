package com.devalgas.blog.service.dto.v1;

import java.util.Set;

public class CategoryArticleSummaryDTOV1 {

    private Long id;
    private String label;
    private Set<ArticleLabelBasicDTOV1> articles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Set<ArticleLabelBasicDTOV1> getArticles() {
        return articles;
    }

    public void setArticles(Set<ArticleLabelBasicDTOV1> articles) {
        this.articles = articles;
    }
}
