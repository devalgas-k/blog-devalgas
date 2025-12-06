package com.devalgas.blog.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.devalgas.blog.domain.Headers} entity.
 */
@Schema(description = "Headers entity.\n@author Devalgas.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class HeadersDTO implements Serializable {

    private Long id;

    @Lob
    private byte[] logoHeaders;

    private String logoHeadersContentType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getLogoHeaders() {
        return logoHeaders;
    }

    public void setLogoHeaders(byte[] logoHeaders) {
        this.logoHeaders = logoHeaders;
    }

    public String getLogoHeadersContentType() {
        return logoHeadersContentType;
    }

    public void setLogoHeadersContentType(String logoHeadersContentType) {
        this.logoHeadersContentType = logoHeadersContentType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HeadersDTO)) {
            return false;
        }

        HeadersDTO headersDTO = (HeadersDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, headersDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HeadersDTO{" +
            "id=" + getId() +
            ", logoHeaders='" + getLogoHeaders() + "'" +
            "}";
    }
}
