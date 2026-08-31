package com.uelbosque.catalogservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class ProductRequestDto {

    @NotBlank(message = "El código de producto es obligatorio")
    @Size(max = 50, message = "El código no debe superar 50 caracteres")
    private String code;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 150, message = "El nombre no debe superar 150 caracteres")
    private String name;

    @Size(max = 500, message = "La descripción no debe superar 500 caracteres")
    private String description;

    @NotNull(message = "El precio de compra es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio de compra debe ser mayor o igual a 0")
    private BigDecimal purchasePrice;

    @NotNull(message = "El precio de venta es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio de venta debe ser mayor o igual a 0")
    private BigDecimal salePrice;

    @NotNull(message = "La tasa de IVA es obligatoria")
    @DecimalMin(value = "0.0", inclusive = true, message = "La tasa de IVA debe ser mayor o igual a 0")
    private BigDecimal ivaRate;

    private String category;
    private String imageUrl;

    public ProductRequestDto() {}

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
}
