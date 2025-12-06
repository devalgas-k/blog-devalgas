package com.devalgas.blog.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * CategoryArticle entity
 * @author Devalgas
 */
@Entity
@Table(name = "category_article")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CategoryArticle implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 256)
    @Pattern(regexp = "^[A-Z].*$")
    @Column(name = "label", length = 256, nullable = false, unique = true)
    private String label;

    @NotNull
    @Size(min = 2, max = 2)
    @Pattern(regexp = "^[A-Z]+$")
    @Column(name = "code", length = 2, nullable = false, unique = true)
    private String code;

    @Lob
    @Column(name = "badge")
    private byte[] badge;

    @Column(name = "badge_content_type")
    private String badgeContentType;

    @Column(name = "description_fr")
    private String descriptionFr;

    @Column(name = "description_en")
    private String descriptionEn;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "categoryArticles")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "categoryArticles" }, allowSetters = true)
    private Set<Article> articles = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CategoryArticle id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return this.label;
    }

    public CategoryArticle label(String label) {
        this.setLabel(label);
        return this;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCode() {
        return this.code;
    }

    public CategoryArticle code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public byte[] getBadge() {
        return this.badge;
    }

    public CategoryArticle badge(byte[] badge) {
        this.setBadge(badge);
        return this;
    }

    public void setBadge(byte[] badge) {
        this.badge = badge;
    }

    public String getBadgeContentType() {
        return this.badgeContentType;
    }

    public CategoryArticle badgeContentType(String badgeContentType) {
        this.badgeContentType = badgeContentType;
        return this;
    }

    public void setBadgeContentType(String badgeContentType) {
        this.badgeContentType = badgeContentType;
    }

    public String getDescriptionFr() {
        return this.descriptionFr;
    }

    public CategoryArticle descriptionFr(String descriptionFr) {
        this.setDescriptionFr(descriptionFr);
        return this;
    }

    public void setDescriptionFr(String descriptionFr) {
        this.descriptionFr = descriptionFr;
    }

    public String getDescriptionEn() {
        return this.descriptionEn;
    }

    public CategoryArticle descriptionEn(String descriptionEn) {
        this.setDescriptionEn(descriptionEn);
        return this;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public Set<Article> getArticles() {
        return this.articles;
    }

    public void setArticles(Set<Article> articles) {
        if (this.articles != null) {
            this.articles.forEach(i -> i.removeCategoryArticle(this));
        }
        if (articles != null) {
            articles.forEach(i -> i.addCategoryArticle(this));
        }
        this.articles = articles;
    }

    public CategoryArticle articles(Set<Article> articles) {
        this.setArticles(articles);
        return this;
    }

    public CategoryArticle addArticle(Article article) {
        this.articles.add(article);
        article.getCategoryArticles().add(this);
        return this;
    }

    public CategoryArticle removeArticle(Article article) {
        this.articles.remove(article);
        article.getCategoryArticles().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CategoryArticle)) {
            return false;
        }
        return getId() != null && getId().equals(((CategoryArticle) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CategoryArticle{" +
            "id=" + getId() +
            ", label='" + getLabel() + "'" +
            ", code='" + getCode() + "'" +
            ", badge='" + getBadge() + "'" +
            ", badgeContentType='" + getBadgeContentType() + "'" +
            ", descriptionFr='" + getDescriptionFr() + "'" +
            ", descriptionEn='" + getDescriptionEn() + "'" +
            "}";
    }
}
