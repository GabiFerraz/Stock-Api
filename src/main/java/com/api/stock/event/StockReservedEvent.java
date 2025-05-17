package com.api.stock.event;

public record StockReservedEvent(String orderId, boolean success) {}
