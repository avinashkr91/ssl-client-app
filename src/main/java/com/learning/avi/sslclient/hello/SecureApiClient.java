package com.learning.avi.sslclient.hello;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class SecureApiClient {

  private final RestClient restClient;

  public SecureApiClient(RestClient restClient) {
    this.restClient = restClient;
  }

  public String callSecureApi() {
    return restClient.get().uri("/api/hello").retrieve().body(String.class);
  }
}
