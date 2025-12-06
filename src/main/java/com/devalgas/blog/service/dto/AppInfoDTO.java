package com.devalgas.blog.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.devalgas.blog.domain.AppInfo} entity.
 */
@Schema(description = "AppInfo entity.\n@author Devalgas.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AppInfoDTO implements Serializable {

    private Long id;

    private String keyInfo;

    private String valueInfo;

    private HeadersDTO headers;

    private FootersDTO footers;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKeyInfo() {
        return keyInfo;
    }

    public void setKeyInfo(String keyInfo) {
        this.keyInfo = keyInfo;
    }

    public String getValueInfo() {
        return valueInfo;
    }

    public void setValueInfo(String valueInfo) {
        this.valueInfo = valueInfo;
    }

    public HeadersDTO getHeaders() {
        return headers;
    }

    public void setHeaders(HeadersDTO headers) {
        this.headers = headers;
    }

    public FootersDTO getFooters() {
        return footers;
    }

    public void setFooters(FootersDTO footers) {
        this.footers = footers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AppInfoDTO)) {
            return false;
        }

        AppInfoDTO appInfoDTO = (AppInfoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, appInfoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AppInfoDTO{" +
            "id=" + getId() +
            ", keyInfo='" + getKeyInfo() + "'" +
            ", valueInfo='" + getValueInfo() + "'" +
            ", headers=" + getHeaders() +
            ", footers=" + getFooters() +
            "}";
    }
}
