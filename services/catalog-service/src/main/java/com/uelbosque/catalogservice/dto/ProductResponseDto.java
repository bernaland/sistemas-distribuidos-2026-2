package com.uelbosque.catalogservice.dto;

import java.math.BigDecimal;

public class ProductResponseDto {

    private Long id;
    private String code;
    private String name;
    private String description;
    private BigDecimal purchasePrice;
    private BigDecimal salePrice;
    private BigDecimal ivaRate;
    private String category;
    private String imageUrl;
    private boolean active;

    public ProductResponseDto() {}

    public ProductResponseDto(Long id, String code, String name, String description,
                              BigDecimal purchasePrice, BigDecimal salePrice, BigDecimal ivaRate,
                              String category, String imageUrl, boolean active) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.purchasePrice = purchasePrice;
        this.salePrice = salePrice;
        this.ivaRate = ivaRate;
        this.category = category;
        this.imageUrl = imageUrl;
        this.active = active;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }

    public BigDecimal getSalePrice() { return salePrice; }
    public void setSalePrice(BigDecimal salePrice) { this.salePrice = salePrice; }

    public BigDecimal getIvaRate() { return ivaRate; }
    public void setIvaRate(BigDecimal ivaRate) { this.ivaRate = ivaRate; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
