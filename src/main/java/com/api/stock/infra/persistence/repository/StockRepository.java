package com.api.stock.infra.persistence.repository;

import com.api.stock.infra.persistence.entity.StockEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<StockEntity, Integer> {

  Optional<StockEntity> findByProductSku(final String productSku);

  void deleteByProductSku(final String productSku);
}
