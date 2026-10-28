package com.spendwise.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name cannot exceed 100 characters")
    private String name;

    private String description;
    private String icon;
    private String colorCode;

    @PositiveOrZero(message = "Budget limit must be zero or positive")
    private BigDecimal budgetLimit;

    private Long userId;

    public CategoryRequest() {}

    public CategoryRequest(String name, String description, String icon, String colorCode, BigDecimal budgetLimit, Long userId) {
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.colorCode = colorCode;
        this.budgetLimit = budgetLimit;
        this.userId = userId;
    }

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
}
