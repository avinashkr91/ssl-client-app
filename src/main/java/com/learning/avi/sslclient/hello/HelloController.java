package com.learning.avi.sslclient.hello;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

  private final SecureApiClient client;

  public HelloController(SecureApiClient client) {
    this.client = client;
  }

  @GetMapping("/hello")
  public String call() {
    return client.callApi();
  }
}
