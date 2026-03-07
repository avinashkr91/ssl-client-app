package com.learning.avi.sslclient.hello;

import org.springframework.boot.autoconfigure.web.client.RestClientSsl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

  @Bean
  public RestClient restClient(RestClient.Builder builder, RestClientSsl ssl) {
    return builder
        .baseUrl("https://localhost:8443")
        .apply(ssl.fromBundle("secure-api-bundle"))
        .build();
  }
}
