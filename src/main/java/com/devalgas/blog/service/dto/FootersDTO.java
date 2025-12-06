package com.devalgas.blog.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.devalgas.blog.domain.Footers} entity.
 */
@Schema(description = "Footers entity.\n@author Devalgas.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FootersDTO implements Serializable {

    private Long id;

    @Lob
    private byte[] logoFooters;

    private String logoFootersContentType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getLogoFooters() {
        return logoFooters;
    }

    public void setLogoFooters(byte[] logoFooters) {
        this.logoFooters = logoFooters;
    }

    public String getLogoFootersContentType() {
        return logoFootersContentType;
    }

    public void setLogoFootersContentType(String logoFootersContentType) {
        this.logoFootersContentType = logoFootersContentType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FootersDTO)) {
            return false;
        }

        FootersDTO footersDTO = (FootersDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, footersDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FootersDTO{" +
            "id=" + getId() +
            ", logoFooters='" + getLogoFooters() + "'" +
            "}";
    }
}
