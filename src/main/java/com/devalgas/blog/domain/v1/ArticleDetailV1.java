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
public class ArticleDetailV1 implements Serializable {

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

    @Lob
    @Column(name = "markdown_fr")
    private byte[] markdownFr;

    @Column(name = "markdown_fr_content_type")
    private String markdownFrContentType;

    @Lob
    @Column(name = "markdown_en")
    private byte[] markdownEn;

    @Column(name = "markdown_en_content_type")
    private String markdownEnContentType;

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

    @Lob
    @Column(name = "badge")
    private byte[] badge;

    @Column(name = "badge_content_type")
    private String badgeContentType;

    @Lob
    @Column(name = "banner")
    private byte[] banner;

    @Column(name = "banner_content_type")
    private String bannerContentType;

    @ManyToMany(fetch = FetchType.LAZY)
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

    public ArticleDetailV1 id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabelEn() {
        return this.labelEn;
    }

    public ArticleDetailV1 labelEn(String labelEn) {
        this.setLabelEn(labelEn);
        return this;
    }

    public void setLabelEn(String labelEn) {
        this.labelEn = labelEn;
    }

    public String getLabelFr() {
        return this.labelFr;
    }

    public ArticleDetailV1 labelFr(String labelFr) {
        this.setLabelFr(labelFr);
        return this;
    }

    public void setLabelFr(String labelFr) {
        this.labelFr = labelFr;
    }

    public String getDescriptionFr() {
        return this.descriptionFr;
    }

    public ArticleDetailV1 descriptionFr(String descriptionFr) {
        this.setDescriptionFr(descriptionFr);
        return this;
    }

    public void setDescriptionFr(String descriptionFr) {
        this.descriptionFr = descriptionFr;
    }

    public String getDescriptionEn() {
        return this.descriptionEn;
    }

    public ArticleDetailV1 descriptionEn(String descriptionEn) {
        this.setDescriptionEn(descriptionEn);
        return this;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public Integer getViews() {
        return this.views;
    }

    public ArticleDetailV1 views(Integer views) {
        this.setViews(views);
        return this;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public Integer getStars() {
        return this.stars;
    }

    public ArticleDetailV1 stars(Integer stars) {
        this.setStars(stars);
        return this;
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

    public void setStars(Integer stars) {
        this.stars = stars;
    }

    public byte[] getBadge() {
        return this.badge;
    }

    public void setBadge(byte[] badge) {
        this.badge = badge;
    }

    public String getBadgeContentType() {
        return this.badgeContentType;
    }

    public void setBadgeContentType(String badgeContentType) {
        this.badgeContentType = badgeContentType;
    }

    public byte[] getBanner() {
        return this.banner;
    }

    public void setBanner(byte[] banner) {
        this.banner = banner;
    }

    public String getBannerContentType() {
        return this.bannerContentType;
    }

    public void setBannerContentType(String bannerContentType) {
        this.bannerContentType = bannerContentType;
    }

    public Set<CategoryArticleHomeV1> getCategoryArticles() {
        return this.categoryArticles;
    }

    public void setCategoryArticles(Set<CategoryArticleHomeV1> categoryArticles) {
        this.categoryArticles = categoryArticles;
    }

    public ArticleDetailV1 categoryArticles(Set<CategoryArticleHomeV1> categoryArticles) {
        this.setCategoryArticles(categoryArticles);
        return this;
    }

    public ArticleDetailV1 addCategoryArticle(CategoryArticleHomeV1 categoryArticle) {
        this.categoryArticles.add(categoryArticle);
        return this;
    }

    public ArticleDetailV1 removeCategoryArticle(CategoryArticleHomeV1 categoryArticle) {
        this.categoryArticles.remove(categoryArticle);
        return this;
    }

    public byte[] getMarkdownFr() {
        return this.markdownFr;
    }

    public ArticleDetailV1 markdownFr(byte[] markdownFr) {
        this.setMarkdownFr(markdownFr);
        return this;
    }

    public void setMarkdownFr(byte[] markdownFr) {
        this.markdownFr = markdownFr;
    }

    public String getMarkdownFrContentType() {
        return this.markdownFrContentType;
    }

    public ArticleDetailV1 markdownFrContentType(String markdownFrContentType) {
        this.markdownFrContentType = markdownFrContentType;
        return this;
    }

    public void setMarkdownFrContentType(String markdownFrContentType) {
        this.markdownFrContentType = markdownFrContentType;
    }

    public byte[] getMarkdownEn() {
        return this.markdownEn;
    }

    public ArticleDetailV1 markdownEn(byte[] markdownEn) {
        this.setMarkdownEn(markdownEn);
        return this;
    }

    public void setMarkdownEn(byte[] markdownEn) {
        this.markdownEn = markdownEn;
    }

    public String getMarkdownEnContentType() {
        return this.markdownEnContentType;
    }

    public ArticleDetailV1 markdownEnContentType(String markdownEnContentType) {
        this.markdownEnContentType = markdownEnContentType;
        return this;
    }

    public void setMarkdownEnContentType(String markdownEnContentType) {
        this.markdownEnContentType = markdownEnContentType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ArticleDetailV1)) {
            return false;
        }
        return getId() != null && getId().equals(((ArticleDetailV1) o).getId());
    }

    @Override
    public int hashCode() {
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
            ", markdownFr='" + getMarkdownFr() + "'" +
            ", markdownFrContentType='" + getMarkdownFrContentType() + "'" +
            ", markdownEn='" + getMarkdownEn() + "'" +
            ", markdownEnContentType='" + getMarkdownEnContentType() + "'" +
            ", views=" + getViews() +
            ", stars=" + getStars() +
            "}";
    }
}
