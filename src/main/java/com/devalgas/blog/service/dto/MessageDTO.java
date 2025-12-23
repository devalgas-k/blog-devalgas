package com.devalgas.blog.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link com.devalgas.blog.domain.Message} entity.
 */
@Schema(description = "Message entity\n@author Devalgas")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MessageDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 256)
    private String name;

    @NotNull
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{1,4}?[-.\\s]?(\\(?\\d{1,3}?\\)?[-.\\s]?)?\\d{3}[-.\\s]?\\d{3}[-.\\s]?\\d{4}$")
    private String phone;

    @NotNull
    @Size(max = 256)
    private String message;

    @Lob
    private byte[] file;

    private String fileContentType;

    private ZonedDateTime date;

    @Size(min = 2, max = 2)
    private String langKey;

    @Size(min = 2)
    private String countryKey;

    @NotNull
    private SubjectDTO subject;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getFileContentType() {
        return fileContentType;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
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

    public SubjectDTO getSubject() {
        return subject;
    }

    public void setSubject(SubjectDTO subject) {
        this.subject = subject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MessageDTO)) {
            return false;
        }

        MessageDTO messageDTO = (MessageDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, messageDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MessageDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", email='" + getEmail() + "'" +
            ", phone='" + getPhone() + "'" +
            ", message='" + getMessage() + "'" +
            ", file='" + getFile() + "'" +
            ", date='" + getDate() + "'" +
            ", langKey='" + getLangKey() + "'" +
            ", countryKey='" + getCountryKey() + "'" +
            ", subject=" + getSubject() +
            "}";
    }
}
