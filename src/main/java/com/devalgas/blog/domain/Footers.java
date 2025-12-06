package com.devalgas.blog.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Footers entity.
 * @author Devalgas.
 */
@Entity
@Table(name = "footers")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Footers implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Lob
    @Column(name = "logo_footers")
    private byte[] logoFooters;

    @Column(name = "logo_footers_content_type")
    private String logoFootersContentType;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "footers")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "headers", "footers" }, allowSetters = true)
    private Set<AppInfo> appInfoFooters = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Footers id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getLogoFooters() {
        return this.logoFooters;
    }

    public Footers logoFooters(byte[] logoFooters) {
        this.setLogoFooters(logoFooters);
        return this;
    }

    public void setLogoFooters(byte[] logoFooters) {
        this.logoFooters = logoFooters;
    }

    public String getLogoFootersContentType() {
        return this.logoFootersContentType;
    }

    public Footers logoFootersContentType(String logoFootersContentType) {
        this.logoFootersContentType = logoFootersContentType;
        return this;
    }

    public void setLogoFootersContentType(String logoFootersContentType) {
        this.logoFootersContentType = logoFootersContentType;
    }

    public Set<AppInfo> getAppInfoFooters() {
        return this.appInfoFooters;
    }

    public void setAppInfoFooters(Set<AppInfo> appInfos) {
        if (this.appInfoFooters != null) {
            this.appInfoFooters.forEach(i -> i.setFooters(null));
        }
        if (appInfos != null) {
            appInfos.forEach(i -> i.setFooters(this));
        }
        this.appInfoFooters = appInfos;
    }

    public Footers appInfoFooters(Set<AppInfo> appInfos) {
        this.setAppInfoFooters(appInfos);
        return this;
    }

    public Footers addAppInfoFooters(AppInfo appInfo) {
        this.appInfoFooters.add(appInfo);
        appInfo.setFooters(this);
        return this;
    }

    public Footers removeAppInfoFooters(AppInfo appInfo) {
        this.appInfoFooters.remove(appInfo);
        appInfo.setFooters(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Footers)) {
            return false;
        }
        return getId() != null && getId().equals(((Footers) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Footers{" +
            "id=" + getId() +
            ", logoFooters='" + getLogoFooters() + "'" +
            ", logoFootersContentType='" + getLogoFootersContentType() + "'" +
            "}";
    }
}
