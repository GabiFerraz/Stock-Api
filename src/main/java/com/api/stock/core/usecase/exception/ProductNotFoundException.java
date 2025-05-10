package com.api.stock.core.usecase.exception;

import static java.lang.String.format;

public class ProductNotFoundException extends BusinessException {

  private static final String ERROR_CODE = "NOT_FOUND";
  private static final String MESSAGE = "Product with sku=[%s] not found in Product-API.";

  public ProductNotFoundException(final String productSku) {
    super(format(MESSAGE, productSku), ERROR_CODE);
  }
}
