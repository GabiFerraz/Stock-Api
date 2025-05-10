package com.api.stock.core.domain.exception;

public record ErrorDetail(String field, String errorMessage, Object rejectedValue) {}
