package com.devalgas.blog.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.devalgas.blog.domain.Subject} entity.
 */
@Schema(description = "This is a subject\nsubject a class\n@author Devalgas")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SubjectDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 256)
    private String titleFr;

    @NotNull
    @Size(max = 256)
    private String titleEn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitleFr() {
        return titleFr;
    }

    public void setTitleFr(String titleFr) {
        this.titleFr = titleFr;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SubjectDTO)) {
            return false;
        }

        SubjectDTO subjectDTO = (SubjectDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, subjectDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SubjectDTO{" +
            "id=" + getId() +
            ", titleFr='" + getTitleFr() + "'" +
            ", titleEn='" + getTitleEn() + "'" +
            "}";
    }
}
