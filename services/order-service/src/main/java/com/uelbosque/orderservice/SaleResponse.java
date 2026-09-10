package com.uelbosque.orderservice;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
public record SaleResponse(Long id,String customerCedula,String customerName,String userCedula,String username,
    Instant createdAt,List<Line> items,BigDecimal subtotal,BigDecimal taxTotal,BigDecimal grandTotal) {
    public record Line(String productCode,String productName,int quantity,BigDecimal unitPrice,
        BigDecimal ivaRate,BigDecimal subtotal,BigDecimal tax) {
        public static Line from(SaleLine line) {
            return new Line(line.getProductCode(),line.getProductName(),line.getQuantity(),line.getUnitPrice(),
                line.getIvaRate(),line.getSubtotal(),line.getTax());
        }
    }
}
