package com.api.stock.entrypoint.config;

import com.api.stock.core.domain.exception.ErrorDetail;
import java.util.List;
import lombok.Generated;

@Generated
public record ErrorResponse(String message, String error, List<ErrorDetail> errors) {}
