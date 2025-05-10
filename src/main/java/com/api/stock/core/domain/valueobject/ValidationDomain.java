package com.api.stock.core.domain.valueobject;

import java.util.List;
import java.util.function.Predicate;

public record ValidationDomain<T>(T field, String message, List<Predicate<T>> predicates) {}
