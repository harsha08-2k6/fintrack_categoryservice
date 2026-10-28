package com.spendwise.category.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private String icon;
    private String colorCode;
    private BigDecimal budgetLimit;
    private Long userId;
    private boolean custom;
    private LocalDateTime createdAt;

    public CategoryResponse() {}

    public CategoryResponse(Long id, String name, String description, String icon, String colorCode, BigDecimal budgetLimit, Long userId, boolean custom, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.colorCode = colorCode;
        this.budgetLimit = budgetLimit;
        this.userId = userId;
        this.custom = custom;
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private String description;
        private String icon;
        private String colorCode;
        private BigDecimal budgetLimit;
        private Long userId;
        private boolean custom;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder icon(String icon) { this.icon = icon; return this; }
        public Builder colorCode(String colorCode) { this.colorCode = colorCode; return this; }
        public Builder budgetLimit(BigDecimal budgetLimit) { this.budgetLimit = budgetLimit; return this; }
        public Builder userId(Long userId) { this.userId = userId; return this; }
        public Builder isCustom(boolean custom) { this.custom = custom; return this; }
        public Builder custom(boolean custom) { this.custom = custom; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public CategoryResponse build() {
            return new CategoryResponse(id, name, description, icon, colorCode, budgetLimit, userId, custom, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getColorCode() { return colorCode; }
    public void setColorCode(String colorCode) { this.colorCode = colorCode; }

    public BigDecimal getBudgetLimit() { return budgetLimit; }
    public void setBudgetLimit(BigDecimal budgetLimit) { this.budgetLimit = budgetLimit; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public boolean isCustom() { return custom; }
    public void setCustom(boolean custom) { this.custom = custom; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
