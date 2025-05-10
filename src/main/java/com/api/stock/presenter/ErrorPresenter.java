package com.api.stock.presenter;

import com.api.stock.presenter.response.ErrorPresenterResponse;
import org.springframework.stereotype.Component;

@Component
public class ErrorPresenter {

  public ErrorPresenterResponse toPresenterErrorResponse(final String errorMessage) {
    return ErrorPresenterResponse.builder().errorMessage(errorMessage).build();
  }
}
