package com.api.stock.core.usecase.exception;

import static java.lang.String.format;

public class StockNotFoundException extends BusinessException {

  private static final String ERROR_CODE = "NOT_FOUND";
  private static final String MESSAGE = "Stock for sku=[%s] not found.";

  public StockNotFoundException(final String productSku) {
    super(format(MESSAGE, productSku), ERROR_CODE);
  }
}
