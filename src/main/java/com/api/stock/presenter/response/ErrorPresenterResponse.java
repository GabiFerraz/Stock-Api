package com.api.stock.presenter.response;

import lombok.Builder;

@Builder
public record ErrorPresenterResponse(String errorMessage) {}
