package com.api.stock.core.gateway;

import com.api.stock.core.gateway.response.ProductDetailsResponse;

public interface ProductApiGateway {

  ProductDetailsResponse getProductDetails(final String productSku);
}
