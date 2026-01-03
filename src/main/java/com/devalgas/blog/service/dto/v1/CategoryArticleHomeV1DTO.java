package com.devalgas.blog.service.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.devalgas.blog.domain.CategoryArticle} entity.
 */
@Schema(description = "CategoryArticle entity\n@author Devalgas")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CategoryArticleHomeV1DTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 256)
    @Pattern(regexp = "^[A-Z].*$")
    private String label;

    @NotNull
    @Size(min = 2, max = 2)
    @Pattern(regexp = "^[A-Z]+$")
    private String code;

    private String descriptionFr;

    private String descriptionEn;

    private Set<ArticleHomeV1DTO> articles = new HashSet<>();

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public Set<ArticleHomeV1DTO> getArticles() {
        return articles;
    }

    public void setArticles(Set<ArticleHomeV1DTO> articles) {
        this.articles = articles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CategoryArticleHomeV1DTO)) {
            return false;
        }

        CategoryArticleHomeV1DTO categoryArticleDTO = (CategoryArticleHomeV1DTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, categoryArticleDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CategoryArticleDTO{" +
            "id=" + getId() +
            ", label='" + getLabel() + "'" +
            ", code='" + getCode() + "'" +
            ", descriptionFr='" + getDescriptionFr() + "'" +
            ", descriptionEn='" + getDescriptionEn() + "'" +
            ", articles=" + getArticles() +
            "}";
    }
}
