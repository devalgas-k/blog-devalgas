package com.devalgas.blog.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * AppInfo entity.
 * @author Devalgas.
 */
@Entity
@Table(name = "app_info")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AppInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "key_info")
    private String keyInfo;

    @Column(name = "value_info")
    private String valueInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "appInfoHeaders" }, allowSetters = true)
    private Headers headers;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "appInfoFooters" }, allowSetters = true)
    private Footers footers;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AppInfo id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKeyInfo() {
        return this.keyInfo;
    }

    public AppInfo keyInfo(String keyInfo) {
        this.setKeyInfo(keyInfo);
        return this;
    }

    public void setKeyInfo(String keyInfo) {
        this.keyInfo = keyInfo;
    }

    public String getValueInfo() {
        return this.valueInfo;
    }

    public AppInfo valueInfo(String valueInfo) {
        this.setValueInfo(valueInfo);
        return this;
    }

    public void setValueInfo(String valueInfo) {
        this.valueInfo = valueInfo;
    }

    public Headers getHeaders() {
        return this.headers;
    }

    public void setHeaders(Headers headers) {
        this.headers = headers;
    }

    public AppInfo headers(Headers headers) {
        this.setHeaders(headers);
        return this;
    }

    public Footers getFooters() {
        return this.footers;
    }

    public void setFooters(Footers footers) {
        this.footers = footers;
    }

    public AppInfo footers(Footers footers) {
        this.setFooters(footers);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AppInfo)) {
            return false;
        }
        return getId() != null && getId().equals(((AppInfo) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AppInfo{" +
            "id=" + getId() +
            ", keyInfo='" + getKeyInfo() + "'" +
            ", valueInfo='" + getValueInfo() + "'" +
            "}";
    }
}
