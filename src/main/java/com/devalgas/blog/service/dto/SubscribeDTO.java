package com.devalgas.blog.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link com.devalgas.blog.domain.Subscribe} entity.
 */
@Schema(description = "This is a subscribe\nsubscribe a class\n@author Devalgas")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SubscribeDTO implements Serializable {

    private Long id;

    @NotNull
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    private String email;

    @Size(min = 2, max = 2)
    private String langKey;

    @Size(min = 2)
    private String countryKey;

    private ZonedDateTime date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public String getCountryKey() {
        return countryKey;
    }

    public void setCountryKey(String countryKey) {
        this.countryKey = countryKey;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SubscribeDTO)) {
            return false;
        }

        SubscribeDTO subscribeDTO = (SubscribeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, subscribeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SubscribeDTO{" +
            "id=" + getId() +
            ", email='" + getEmail() + "'" +
            ", langKey='" + getLangKey() + "'" +
            ", countryKey='" + getCountryKey() + "'" +
            ", date='" + getDate() + "'" +
            "}";
    }
}
