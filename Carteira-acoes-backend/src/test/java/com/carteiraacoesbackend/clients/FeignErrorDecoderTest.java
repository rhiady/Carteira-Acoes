package com.carteiraacoesbackend.clients;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.carteiraacoesbackend.exceptions.ApiException;
import feign.Request;
import feign.Response;
import feign.RetryableException;
import org.junit.jupiter.api.Test;

class FeignErrorDecoderTest {

  private final FeignErrorDecoder decoder = new FeignErrorDecoder();

  @Test
  void classifiesNotFoundAuthenticationRateLimitAndUnavailableResponses() {
    assertError(404, 404, "EXTERNAL_RESOURCE_NOT_FOUND");
    assertError(401, 502, "EXTERNAL_API_AUTHENTICATION");
    assertError(429, 429, "EXTERNAL_API_RATE_LIMIT");
    assertError(500, 502, "EXTERNAL_API_ERROR");
    Exception unavailable = decoder.decode("client#operation", response(503));
    assertInstanceOf(RetryableException.class, unavailable);
  }

  private void assertError(int providerStatus, int expectedStatus, String expectedCode) {
    Exception error = decoder.decode("client#operation", response(providerStatus));
    ApiException apiError = assertInstanceOf(ApiException.class, error);
    assertEquals(expectedStatus, apiError.getStatus().value());
    assertEquals(expectedCode, apiError.getCode());
  }

  private Response response(int providerStatus) {
    return Response.builder()
        .status(providerStatus)
        .reason("test")
        .request(
            Request.create(
                Request.HttpMethod.GET,
                "http://provider.test",
                java.util.Map.of(),
                null,
                null,
                null))
        .build();
  }
}
