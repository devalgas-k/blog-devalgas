package com.devalgas.blog.domain.v1;

import com.devalgas.blog.domain.enumeration.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Article entity
 * @author Devalgas
 */
@Entity
@Table(name = "article")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ArticleHomeV1 implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 256)
    @Pattern(regexp = "^[A-Z].*$")
    @Column(name = "label_en", length = 256, nullable = false, unique = true)
    private String labelEn;

    @NotNull
    @Size(max = 256)
    @Pattern(regexp = "^[A-Z].*$")
    @Column(name = "label_fr", length = 256, nullable = false, unique = true)
    private String labelFr;

    @Column(name = "description_fr")
    private String descriptionFr;

    @Column(name = "description_en")
    private String descriptionEn;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @NotNull
    @Column(name = "date", nullable = false)
    private ZonedDateTime date;

    @Column(name = "views")
    private Integer views;

    @Column(name = "stars")
    private Integer stars;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "rel_article__category_article",
        joinColumns = @JoinColumn(name = "article_id"),
        inverseJoinColumns = @JoinColumn(name = "category_article_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "articles" }, allowSetters = true)
    private Set<CategoryArticleHomeV1> categoryArticles = new HashSet<>();

    public Long getId() {
        return this.id;
    }

    public ArticleHomeV1 id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabelEn() {
        return this.labelEn;
    }

    public ArticleHomeV1 labelEn(String labelEn) {
        this.setLabelEn(labelEn);
        return this;
    }

    public void setLabelEn(String labelEn) {
        this.labelEn = labelEn;
    }

    public String getLabelFr() {
        return this.labelFr;
    }

    public ArticleHomeV1 labelFr(String labelFr) {
        this.setLabelFr(labelFr);
        return this;
    }

    public void setLabelFr(String labelFr) {
        this.labelFr = labelFr;
    }

    public String getDescriptionFr() {
        return this.descriptionFr;
    }

    public ArticleHomeV1 descriptionFr(String descriptionFr) {
        this.setDescriptionFr(descriptionFr);
        return this;
    }

    public void setDescriptionFr(String descriptionFr) {
        this.descriptionFr = descriptionFr;
    }

    public String getDescriptionEn() {
        return this.descriptionEn;
    }

    public ArticleHomeV1 descriptionEn(String descriptionEn) {
        this.setDescriptionEn(descriptionEn);
        return this;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public ZonedDateTime getDate() {
        return this.date;
    }

    public ArticleHomeV1 date(ZonedDateTime date) {
        this.setDate(date);
        return this;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getViews() {
        return this.views;
    }

    public ArticleHomeV1 views(Integer views) {
        this.setViews(views);
        return this;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public Integer getStars() {
        return this.stars;
    }

    public ArticleHomeV1 stars(Integer stars) {
        this.setStars(stars);
        return this;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }

    public Set<CategoryArticleHomeV1> getCategoryArticles() {
        return this.categoryArticles;
    }

    public void setCategoryArticles(Set<CategoryArticleHomeV1> categoryArticles) {
        this.categoryArticles = categoryArticles;
    }

    public ArticleHomeV1 categoryArticles(Set<CategoryArticleHomeV1> categoryArticles) {
        this.setCategoryArticles(categoryArticles);
        return this;
    }

    public ArticleHomeV1 addCategoryArticle(CategoryArticleHomeV1 categoryArticle) {
        this.categoryArticles.add(categoryArticle);
        return this;
    }

    public ArticleHomeV1 removeCategoryArticle(CategoryArticleHomeV1 categoryArticle) {
        this.categoryArticles.remove(categoryArticle);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ArticleHomeV1)) {
            return false;
        }
        return getId() != null && getId().equals(((ArticleHomeV1) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Article{" +
            "id=" + getId() +
            ", labelEn='" + getLabelEn() + "'" +
            ", labelFr='" + getLabelFr() + "'" +
            ", descriptionFr='" + getDescriptionFr() + "'" +
            ", descriptionEn='" + getDescriptionEn() + "'" +
            ", views=" + getViews() +
            ", stars=" + getStars() +
            "}";
    }
}
