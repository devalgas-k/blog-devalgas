package com.devalgas.blog.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * This is a subject
 * subject a class
 * @author Devalgas
 */
@Entity
@Table(name = "subject")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Subject implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 256)
    @Column(name = "title_fr", length = 256, nullable = false)
    private String titleFr;

    @NotNull
    @Size(max = 256)
    @Column(name = "title_en", length = 256, nullable = false)
    private String titleEn;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Subject id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitleFr() {
        return this.titleFr;
    }

    public Subject titleFr(String titleFr) {
        this.setTitleFr(titleFr);
        return this;
    }

    public void setTitleFr(String titleFr) {
        this.titleFr = titleFr;
    }

    public String getTitleEn() {
        return this.titleEn;
    }

    public Subject titleEn(String titleEn) {
        this.setTitleEn(titleEn);
        return this;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Subject)) {
            return false;
        }
        return getId() != null && getId().equals(((Subject) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Subject{" +
            "id=" + getId() +
            ", titleFr='" + getTitleFr() + "'" +
            ", titleEn='" + getTitleEn() + "'" +
            "}";
    }
}
