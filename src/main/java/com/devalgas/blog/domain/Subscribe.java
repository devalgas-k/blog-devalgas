package com.devalgas.blog.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * This is a subscribe
 * subscribe a class
 * @author Devalgas
 */
@Entity
@Table(name = "subscribe")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Subscribe implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Size(min = 2, max = 2)
    @Column(name = "lang_key", length = 2)
    private String langKey;

    @Size(min = 2)
    @Column(name = "country_key")
    private String countryKey;

    @Column(name = "date")
    private ZonedDateTime date;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Subscribe id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return this.email;
    }

    public Subscribe email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLangKey() {
        return this.langKey;
    }

    public Subscribe langKey(String langKey) {
        this.setLangKey(langKey);
        return this;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public String getCountryKey() {
        return this.countryKey;
    }

    public Subscribe countryKey(String countryKey) {
        this.setCountryKey(countryKey);
        return this;
    }

    public void setCountryKey(String countryKey) {
        this.countryKey = countryKey;
    }

    public ZonedDateTime getDate() {
        return this.date;
    }

    public Subscribe date(ZonedDateTime date) {
        this.setDate(date);
        return this;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Subscribe)) {
            return false;
        }
        return getId() != null && getId().equals(((Subscribe) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Subscribe{" +
            "id=" + getId() +
            ", email='" + getEmail() + "'" +
            ", langKey='" + getLangKey() + "'" +
            ", countryKey='" + getCountryKey() + "'" +
            ", date='" + getDate() + "'" +
            "}";
    }
}
