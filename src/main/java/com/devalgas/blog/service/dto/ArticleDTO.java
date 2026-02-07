package com.devalgas.blog.service.dto;

import com.devalgas.blog.domain.enumeration.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.devalgas.blog.domain.Article} entity.
 */
@Schema(description = "Article entity\n@author Devalgas")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ArticleDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 256)
    @Pattern(regexp = "^[A-Z].*$")
    private String labelEn;

    @NotNull
    @Size(max = 256)
    @Pattern(regexp = "^[A-Z].*$")
    private String labelFr;

    private String descriptionFr;

    private String descriptionEn;

    @Lob
    private byte[] markdownFr;

    private String markdownFrContentType;

    @Lob
    private byte[] markdownEn;

    private String markdownEnContentType;

    @NotNull
    private Status status;

    @NotNull
    private ZonedDateTime date;

    @Lob
    private byte[] badge;

    private String badgeContentType;

    @Lob
    private byte[] banner;

    private String bannerContentType;

    private Integer views;

    private Integer stars;

    private Boolean display;

    private Set<CategoryArticleDTO> categoryArticles = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabelEn() {
        return labelEn;
    }

    public void setLabelEn(String labelEn) {
        this.labelEn = labelEn;
    }

    public String getLabelFr() {
        return labelFr;
    }

    public void setLabelFr(String labelFr) {
        this.labelFr = labelFr;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public byte[] getBadge() {
        return badge;
    }

    public void setBadge(byte[] badge) {
        this.badge = badge;
    }

    public String getBadgeContentType() {
        return badgeContentType;
    }

    public void setBadgeContentType(String badgeContentType) {
        this.badgeContentType = badgeContentType;
    }

    public byte[] getBanner() {
        return banner;
    }

    public void setBanner(byte[] banner) {
        this.banner = banner;
    }

    public String getBannerContentType() {
        return bannerContentType;
    }

    public void setBannerContentType(String bannerContentType) {
        this.bannerContentType = bannerContentType;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public Integer getStars() {
        return stars;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }

    public Boolean getDisplay() {
        return display;
    }

    public void setDisplay(Boolean display) {
        this.display = display;
    }

    public Set<CategoryArticleDTO> getCategoryArticles() {
        return categoryArticles;
    }

    public void setCategoryArticles(Set<CategoryArticleDTO> categoryArticles) {
        this.categoryArticles = categoryArticles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ArticleDTO)) {
            return false;
        }

        ArticleDTO articleDTO = (ArticleDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, articleDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ArticleDTO{" +
            "id=" + getId() +
            ", labelEn='" + getLabelEn() + "'" +
            ", labelFr='" + getLabelFr() + "'" +
            ", descriptionFr='" + getDescriptionFr() + "'" +
            ", descriptionEn='" + getDescriptionEn() + "'" +
            ", markdownFr='" + getMarkdownFr() + "'" +
            ", markdownEn='" + getMarkdownEn() + "'" +
            ", status='" + getStatus() + "'" +
            ", date='" + getDate() + "'" +
            ", badge='" + getBadge() + "'" +
            ", banner='" + getBanner() + "'" +
            ", views=" + getViews() +
            ", stars=" + getStars() +
            ", display='" + getDisplay() + "'" +
            ", categoryArticles=" + getCategoryArticles() +
            "}";
    }
}
