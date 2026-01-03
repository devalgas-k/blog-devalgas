package com.devalgas.blog.service.dto.v1;

import com.devalgas.blog.domain.enumeration.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
public class ArticleHomeV1DTO implements Serializable {

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

    @NotNull
    private Status status;

    @NotNull
    private ZonedDateTime date;

    private Integer views;

    private Integer stars;

    private Set<CategoryArticleHomeV1DTO> categoryArticles = new HashSet<>();

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

    public Set<CategoryArticleHomeV1DTO> getCategoryArticles() {
        return categoryArticles;
    }

    public void setCategoryArticles(Set<CategoryArticleHomeV1DTO> categoryArticles) {
        this.categoryArticles = categoryArticles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ArticleHomeV1DTO)) {
            return false;
        }

        ArticleHomeV1DTO articleDTO = (ArticleHomeV1DTO) o;
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
            ", status='" + getStatus() + "'" +
            ", date='" + getDate() + "'" +
            ", views=" + getViews() +
            ", stars=" + getStars() +
            ", categoryArticles=" + getCategoryArticles() +
            "}";
    }
}
