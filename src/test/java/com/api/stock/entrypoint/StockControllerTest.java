package com.api.stock.entrypoint;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.api.stock.core.domain.Stock;
import com.api.stock.core.dto.StockDto;
import com.api.stock.core.usecase.CreateStock;
import com.api.stock.core.usecase.DeleteStock;
import com.api.stock.core.usecase.SearchStock;
import com.api.stock.core.usecase.UpdateStock;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class StockControllerTest {

  private static final String BASE_URL = "/api/stock";
  private static final String BASE_URL_WITH_PRODUCT_SKU = BASE_URL + "/%s";

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private CreateStock createStock;
  @MockitoBean private SearchStock searchStock;
  @MockitoBean private UpdateStock updateStock;
  @MockitoBean private DeleteStock deleteStock;

  @Test
  void shouldCreateStockSuccessfully() throws Exception {
    final var request = new StockDto("BOLA-123-ABC", 10);
    final var response = new Stock(1, "BOLA-123-ABC", 10);

    when(this.createStock.execute(any(StockDto.class))).thenReturn(response);

    mockMvc
        .perform(
            post(BASE_URL)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(response.getId()))
        .andExpect(jsonPath("$.productSku").value(response.getProductSku()))
        .andExpect(jsonPath("$.quantity").value(response.getAvailableQuantity()));

    final ArgumentCaptor<StockDto> stockCaptor = ArgumentCaptor.forClass(StockDto.class);
    verify(this.createStock).execute(stockCaptor.capture());

    assertThat(stockCaptor.getValue()).usingRecursiveComparison().isEqualTo(request);
  }

  @Test
  void shouldSearchStockSuccessfully() throws Exception {
    final var productSku = "BOLA-123-ABC";
    final var response = new Stock(1, "BOLA-123-ABC", 10);

    when(this.searchStock.execute(productSku)).thenReturn(Optional.of(response));

    mockMvc
        .perform(get(format(BASE_URL_WITH_PRODUCT_SKU, productSku)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(response.getId()))
        .andExpect(jsonPath("$.productSku").value(response.getProductSku()))
        .andExpect(jsonPath("$.quantity").value(response.getAvailableQuantity()));

    verify(this.searchStock).execute(productSku);
  }

  @Test
  void shouldSearchStockAndReturnNotFound() throws Exception {
    final var productSku = "BOLA-123-ABC";

    when(this.searchStock.execute(productSku)).thenReturn(Optional.empty());

    mockMvc
        .perform(get(format(BASE_URL_WITH_PRODUCT_SKU, productSku)))
        .andExpect(status().isNotFound());

    verify(this.searchStock).execute(productSku);
  }

  @Test
  void shouldUpdateStockSuccessfully() throws Exception {
    final var productSku = "BOLA-123-ABC";
    final var quantity = 10;
    final var response = new Stock(1, "BOLA-123-ABC", 10);

    when(this.updateStock.execute(productSku, quantity)).thenReturn(response);

    mockMvc
        .perform(
            put(format(BASE_URL_WITH_PRODUCT_SKU, productSku))
                .param("quantity", String.valueOf(quantity))
                .contentType("application/json"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(response.getId()))
        .andExpect(jsonPath("$.productSku").value(response.getProductSku()))
        .andExpect(jsonPath("$.quantity").value(response.getAvailableQuantity()));

    verify(this.updateStock).execute(productSku, quantity);
  }

  @Test
  void shouldDeleteStockSuccessfully() throws Exception {
    final var productSku = "BOLA-123-ABC";

    doNothing().when(this.deleteStock).execute(productSku);

    mockMvc
        .perform(delete(format(BASE_URL_WITH_PRODUCT_SKU, productSku)))
        .andExpect(status().isNoContent());

    verify(this.deleteStock).execute(productSku);
  }
}
