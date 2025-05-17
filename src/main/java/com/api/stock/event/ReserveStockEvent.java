package com.api.stock.event;

public record ReserveStockEvent(String orderId, String productSku, int quantity) {}
