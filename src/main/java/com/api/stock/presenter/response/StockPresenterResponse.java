package com.api.stock.presenter.response;

import lombok.Builder;

@Builder
public record StockPresenterResponse(int id, String productSku, int quantity) {}
