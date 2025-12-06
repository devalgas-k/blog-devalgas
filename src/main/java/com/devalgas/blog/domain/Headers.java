package com.devalgas.blog.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Headers entity.
 * @author Devalgas.
 */
@Entity
@Table(name = "headers")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Headers implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Lob
    @Column(name = "logo_headers")
    private byte[] logoHeaders;

    @Column(name = "logo_headers_content_type")
    private String logoHeadersContentType;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "headers")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "headers", "footers" }, allowSetters = true)
    private Set<AppInfo> appInfoHeaders = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Headers id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getLogoHeaders() {
        return this.logoHeaders;
    }

    public Headers logoHeaders(byte[] logoHeaders) {
        this.setLogoHeaders(logoHeaders);
        return this;
    }

    public void setLogoHeaders(byte[] logoHeaders) {
        this.logoHeaders = logoHeaders;
    }

    public String getLogoHeadersContentType() {
        return this.logoHeadersContentType;
    }

    public Headers logoHeadersContentType(String logoHeadersContentType) {
        this.logoHeadersContentType = logoHeadersContentType;
        return this;
    }

    public void setLogoHeadersContentType(String logoHeadersContentType) {
        this.logoHeadersContentType = logoHeadersContentType;
    }

    public Set<AppInfo> getAppInfoHeaders() {
        return this.appInfoHeaders;
    }

    public void setAppInfoHeaders(Set<AppInfo> appInfos) {
        if (this.appInfoHeaders != null) {
            this.appInfoHeaders.forEach(i -> i.setHeaders(null));
        }
        if (appInfos != null) {
            appInfos.forEach(i -> i.setHeaders(this));
        }
        this.appInfoHeaders = appInfos;
    }

    public Headers appInfoHeaders(Set<AppInfo> appInfos) {
        this.setAppInfoHeaders(appInfos);
        return this;
    }

    public Headers addAppInfoHeaders(AppInfo appInfo) {
        this.appInfoHeaders.add(appInfo);
        appInfo.setHeaders(this);
        return this;
    }

    public Headers removeAppInfoHeaders(AppInfo appInfo) {
        this.appInfoHeaders.remove(appInfo);
        appInfo.setHeaders(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Headers)) {
            return false;
        }
        return getId() != null && getId().equals(((Headers) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Headers{" +
            "id=" + getId() +
            ", logoHeaders='" + getLogoHeaders() + "'" +
            ", logoHeadersContentType='" + getLogoHeadersContentType() + "'" +
            "}";
    }
}
