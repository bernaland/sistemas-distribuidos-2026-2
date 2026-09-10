package com.uelbosque.inventoryservice;

public record StockResponse(String productCode,int available,int reserved,int onHand) {}
