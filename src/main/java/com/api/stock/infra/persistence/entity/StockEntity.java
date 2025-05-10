package com.api.stock.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stock")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "product_sku", nullable = false, unique = true)
  private String productSku;

  @Column(name = "quantity", nullable = false)
  private Integer quantity;
}
