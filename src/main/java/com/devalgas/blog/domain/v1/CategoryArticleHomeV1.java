package com.devalgas.blog.domain.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
public class CategoryArticleHomeV1 implements Serializable {

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

    @Column(name = "description_fr")
    private String descriptionFr;

    @Column(name = "description_en")
    private String descriptionEn;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "categoryArticles")
    @JsonIgnoreProperties(value = { "categoryArticles" }, allowSetters = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<ArticleHomeV1> articles = new HashSet<>();

    public Long getId() {
        return this.id;
    }

    public CategoryArticleHomeV1 id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return this.label;
    }

    public CategoryArticleHomeV1 label(String label) {
        this.setLabel(label);
        return this;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCode() {
        return this.code;
    }

    public CategoryArticleHomeV1 code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescriptionFr() {
        return this.descriptionFr;
    }

    public CategoryArticleHomeV1 descriptionFr(String descriptionFr) {
        this.setDescriptionFr(descriptionFr);
        return this;
    }

    public void setDescriptionFr(String descriptionFr) {
        this.descriptionFr = descriptionFr;
    }

    public String getDescriptionEn() {
        return this.descriptionEn;
    }

    public CategoryArticleHomeV1 descriptionEn(String descriptionEn) {
        this.setDescriptionEn(descriptionEn);
        return this;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public Set<ArticleHomeV1> getArticles() {
        return this.articles;
    }

    public void setArticles(Set<ArticleHomeV1> articles) {
        if (this.articles != null) {
            this.articles.forEach(i -> i.removeCategoryArticle(this));
        }
        if (articles != null) {
            articles.forEach(i -> i.addCategoryArticle(this));
        }
        this.articles = articles;
    }

    public CategoryArticleHomeV1 articles(Set<ArticleHomeV1> articles) {
        this.setArticles(articles);
        return this;
    }

    public CategoryArticleHomeV1 addArticle(ArticleHomeV1 article) {
        this.articles.add(article);
        article.getCategoryArticles().add(this);
        return this;
    }

    public CategoryArticleHomeV1 removeArticle(ArticleHomeV1 article) {
        this.articles.remove(article);
        article.getCategoryArticles().remove(this);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CategoryArticleHomeV1)) {
            return false;
        }
        return getId() != null && getId().equals(((CategoryArticleHomeV1) o).getId());
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
            ", descriptionFr='" + getDescriptionFr() + "'" +
            ", descriptionEn='" + getDescriptionEn() + "'" +
            "}";
    }
}
