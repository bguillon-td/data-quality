package org.talend.dataquality.semantic.model;

public enum CategoryState {
    SANDBOX("sandbox"),
    DRAFT("draft"),
    PUBLISH("published");

    private String technicalName;

    private CategoryState(String technicalName) {
        this.technicalName = technicalName;
    }

    public String getTechnicalName() {
        return technicalName;
    }
}
