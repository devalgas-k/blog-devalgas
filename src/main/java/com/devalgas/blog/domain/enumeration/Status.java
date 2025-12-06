package com.devalgas.blog.domain.enumeration;

/**
 * ArticleStatus a enum
 * @author Devalgas
 */
public enum Status {
    COMPLETED("Completed"),
    PENDING("Pending"),
    CANCELLED("Cancelled"),
    INPROGRESS("InProgress");

    private final String value;

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
