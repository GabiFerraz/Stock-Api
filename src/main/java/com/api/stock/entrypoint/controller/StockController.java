package com.api.stock.entrypoint.controller;

import static java.lang.String.format;

import com.api.stock.core.domain.Stock;
import com.api.stock.core.dto.StockDto;
import com.api.stock.core.usecase.CreateStock;
import com.api.stock.core.usecase.DeleteStock;
import com.api.stock.core.usecase.SearchStock;
import com.api.stock.core.usecase.UpdateStock;
import com.api.stock.presenter.ErrorPresenter;
import com.api.stock.presenter.StockPresenter;
import com.api.stock.presenter.response.StockPresenterResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stocks")
public class StockController {

  private static final String STOCK_NOT_FOUND_MESSAGE = "Stock not found for product sku=[%s].";

  private final CreateStock createStock;
  private final SearchStock searchStock;
  private final UpdateStock updateStock;
  private final DeleteStock deleteStock;
  private final StockPresenter presenter;
  private final ErrorPresenter errorPresenter;

  @PostMapping
  public ResponseEntity<StockPresenterResponse> create(@Valid @RequestBody final Stock request) {
    final var stock = this.createStock.execute(this.toStockDto(request));

    return new ResponseEntity<>(this.presenter.parseToResponse(stock), HttpStatus.CREATED);
  }

  @GetMapping("/{productSku}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Object> search(
      @Validated @PathVariable("productSku") final String productSku) {
    final var response = this.searchStock.execute(productSku);

    return response
        .<ResponseEntity<Object>>map(stock -> ResponseEntity.ok(presenter.parseToResponse(stock)))
        .orElseGet(
            () ->
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(
                        errorPresenter.toPresenterErrorResponse(
                            format(STOCK_NOT_FOUND_MESSAGE, productSku))));
  }

  @PutMapping("/{productSku}")
  public ResponseEntity<StockPresenterResponse> update(
      @Validated @PathVariable("productSku") final String productSku,
      @Valid @RequestParam(value = "quantity", required = true) final int quantity) {
    final var stock = this.updateStock.execute(productSku, quantity);

    return new ResponseEntity<>(this.presenter.parseToResponse(stock), HttpStatus.OK);
  }

  @DeleteMapping("/{productSku}")
  public ResponseEntity<Void> delete(
      @Validated @PathVariable("productSku") final String productSku) {
    this.deleteStock.execute(productSku);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  private StockDto toStockDto(final Stock stock) {
    return new StockDto(stock.getProductSku(), stock.getAvailableQuantity());
  }
}
