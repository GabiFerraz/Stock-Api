package com.api.stock.event;

public record StockReservedEvent(int orderId, boolean success) {}
