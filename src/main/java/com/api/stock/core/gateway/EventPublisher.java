package com.api.stock.core.gateway;

public interface EventPublisher {

  void publish(final Object event);
}
