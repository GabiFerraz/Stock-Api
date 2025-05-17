package com.api.stock.event;

public record ReserveStockEvent(int orderId, String productSku, int quantity) {}
