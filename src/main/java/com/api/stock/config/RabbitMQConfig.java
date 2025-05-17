package com.api.stock.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

  public static final String EXCHANGE_NAME = "order.events";
  public static final String RESERVE_STOCK_QUEUE = "reserve-stock";
  public static final String RELEASE_STOCK_QUEUE = "release-stock";
  public static final String STOCK_RESERVED_QUEUE = "stock-reserved";

  @Bean
  public TopicExchange orderExchange() {
    return new TopicExchange(EXCHANGE_NAME);
  }

  @Bean
  public Queue reserveStockQueue() {
    return new Queue(RESERVE_STOCK_QUEUE, true);
  }

  @Bean
  public Queue releaseStockQueue() {
    return new Queue(RELEASE_STOCK_QUEUE, true);
  }

  @Bean
  public Queue stockReservedQueue() {
    return new Queue(STOCK_RESERVED_QUEUE, true);
  }

  @Bean
  public Binding reserveStockBinding(Queue reserveStockQueue, TopicExchange orderExchange) {
    return BindingBuilder.bind(reserveStockQueue).to(orderExchange).with(RESERVE_STOCK_QUEUE);
  }

  @Bean
  public Binding releaseStockBinding(Queue releaseStockQueue, TopicExchange orderExchange) {
    return BindingBuilder.bind(releaseStockQueue).to(orderExchange).with(RELEASE_STOCK_QUEUE);
  }

  @Bean
  public Binding stockReservedBinding(Queue stockReservedQueue, TopicExchange orderExchange) {
    return BindingBuilder.bind(stockReservedQueue).to(orderExchange).with(STOCK_RESERVED_QUEUE);
  }
}
