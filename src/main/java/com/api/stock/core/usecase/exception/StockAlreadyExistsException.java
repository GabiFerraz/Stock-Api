package com.api.stock.core.usecase.exception;

import static java.lang.String.format;

public class StockAlreadyExistsException extends BusinessException {

  private static final String ERROR_CODE = "ALREADY_EXISTS";
  private static final String MESSAGE = "Stock for sku=[%s] already exists.";

  public StockAlreadyExistsException(final String productSku) {
    super(format(MESSAGE, productSku), ERROR_CODE);
  }
}
