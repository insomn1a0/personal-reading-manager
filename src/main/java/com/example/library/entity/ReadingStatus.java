package com.example.library.entity;

public enum ReadingStatus {
    WANT_TO_READ("Want to Read", "bg-info-subtle text-info-emphasis"),
    READING("Reading", "bg-primary-subtle text-primary-emphasis"),
    FINISHED("Finished", "bg-success-subtle text-success-emphasis"),
    DNF("DNF", "bg-danger-subtle text-danger-emphasis");

    private final String displayName;
    private final String badgeClass;

    ReadingStatus(String displayName, String badgeClass) {
        this.displayName = displayName;
        this.badgeClass = badgeClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBadgeClass() {
        return badgeClass;
    }
}
