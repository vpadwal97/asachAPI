package com.example.chat.model;

import java.util.UUID;

public class CmsComponentDesign {
    private String id;
    private String componentName;
    private String componentCode;
    private String componentType;
    private Boolean isActive;
    private String timestamp;

    // ✅ Required no-arg constructor for Jackson
    public CmsComponentDesign() {
        this.id = UUID.randomUUID().toString(); // Generate ID for Jackson use
    }

    // Optional: Parameterized constructor for manual creation
    public CmsComponentDesign(
            String componentName,
            String componentCode,
            String componentType,
            Boolean isActive,
            String timestamp) {
        this.id = UUID.randomUUID().toString();
        this.componentName = componentName;
        this.componentCode = componentCode;
        this.componentType = componentType;
        this.isActive = isActive;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public String getComponentCode() {
        return componentCode;
    }

    public void setComponentCode(String componentCode) {
        this.componentCode = componentCode;
    }

    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
