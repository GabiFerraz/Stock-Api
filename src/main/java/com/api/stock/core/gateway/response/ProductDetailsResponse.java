package com.api.stock.core.gateway.response;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
public class ProductDetailsResponse {

  private final String name;
  private final String sku;
}
