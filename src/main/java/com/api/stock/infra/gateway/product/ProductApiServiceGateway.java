package com.api.stock.infra.gateway.product;

import static java.lang.String.format;

import com.api.stock.core.gateway.ProductApiGateway;
import com.api.stock.core.gateway.response.ProductDetailsResponse;
import com.api.stock.infra.gateway.exception.GatewayException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class ProductApiServiceGateway implements ProductApiGateway {

  @Value("${app.product-api.base-url}")
  private String productApiBaseUrl;

  private final WebClient.Builder webClientBuilder;

  @Override
  public ProductDetailsResponse getProductDetails(final String productSku) {
    try {
      final String url = productApiBaseUrl + "/" + productSku;

      return callService(url);
    } catch (Exception e) {
      throw new GatewayException(format("Failed to access Product API=[%s]", e.getMessage()));
    }
  }

  private ProductDetailsResponse callService(final String url) {
    WebClient webClient = webClientBuilder.baseUrl(url).build();

    return webClient.get().retrieve().bodyToMono(ProductDetailsResponse.class).block();
  }
}
