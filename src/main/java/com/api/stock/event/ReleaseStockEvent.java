package com.api.stock.event;

public record ReleaseStockEvent(String productSku, int quantity) {}
