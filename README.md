# Spring Boot SSL RestClient (Using SSLBundle) + Docker HTTPS Test Server

## Overview

This repository demonstrates how to build a **secure REST client in Spring Boot 3** that connects to an *
*HTTPS-protected external API** using:

* **Spring Boot `RestClient`**
* **Spring Boot `SSLBundle` configuration**
* **Docker-based local HTTPS server for testing**

The goal of this project is to provide a **clear and reproducible setup** for backend engineers who need to integrate with **SSL-secured third-party APIs** while developing and testing locally.

This project simulates a real-world enterprise scenario where:

```
Spring Boot Service(Client) --->  Secure External API
        │                              │
        │  HTTPS (TLS)                 │
        │                              │
        ▼                              ▼
   RestClient                      HTTPS Server
```

---

# Why This Project?

When integrating with external APIs, developers frequently face challenges such as:

* Connecting to **SSL-secured endpoints**
* Handling **truststore / certificates**
* Authenticating using **API credentials**
* Testing **HTTPS locally**
* Verifying that **TLS is actually used**

This repository solves those problems by providing:

* A **Spring Boot client configured with SSLBundle**
* A **local HTTPS server running in Docker**
* A **repeatable workflow for certificate generation**
* Instructions to **verify SSL handshakes**

---

# Technology Stack

| Technology      | Purpose                       |
|-----------------|-------------------------------|
| Spring Boot 3.x | Backend framework             |
| RestClient      | HTTP client for external APIs |
| SSLBundle       | Spring Boot TLS configuration |
| Docker          | Local HTTPS test server       |
| NGINX           | Lightweight HTTPS endpoint    |
| OpenSSL         | Certificate generation        |

---

# Project Structure

```
ssl-client-app
│
├── src/main/java/com/learning/avi/sslclient
│   ├── hello
│   │     ├── SecureApiClient.java
│   │     ├── RestClientConfig.java
│   │     └── HelloController.java
|   └── DemoApplication.java
│      
├── src/main/resources
│   ├── application.yml
│   └── truststore.p12
│
├── nginx
│   └── nginx.conf
|
│── certs
|   ├── server.crt
|   └── server.key
|
│── Dockerfile
|── docker-compose.yaml
└── README.md
```

---

# Architecture

```
+-----------------------+
|  Spring Boot Client   |
|                       |
|   RestClient          |
|   SSLBundle           |        
+----------+------------+
           |
           | HTTPS (TLS)
           |
           ▼
+-----------------------+
|  Docker HTTPS Server  |
|       (NGINX)         |
+-----------------------+
```

---

# Spring Boot SSLBundle Configuration

Spring Boot 3 introduces **SSL Bundles** to simplify TLS configuration.

Instead of manually creating an `SSLContext`, Spring manages certificates declaratively.

## application.yml

```yaml
spring:
  ssl:
    bundle:
      jks:
        secured-api-bundle:
          truststore:
            location: classpath:truststore.p12
            password: changeit
            type: PKCS12
```

This creates a named SSL configuration:

```
secured-api-bundle
```

The bundle provides:

* TLS configuration
* Truststore loading
* SSLContext creation

---
# Local HTTPS Server (Docker)

To simulate an external secure API, this project uses **NGINX running inside Docker with TLS enabled**.

---

# Step 1 — Generate Certificates

Create certificates for the HTTPS server.

```bash
mkdir certs

openssl req -newkey rsa:2048 -x509 -sha256 -keyout server.key -out server.crt -days 365 -subj "/CN=localhost" -nodes
```

---

# Step 2 — Create Truststore for Client

Convert certificate into PKCS12 format which will be used by client application to authenticate

```bash
openssl pkcs12 -export -in server.crt -inkey server.key -out truststore.p12 -name local-cert
```

Password:

```
changeit
```

Move the generated `truststore.p12` to:

```
src/main/resources
```

---

# Step 3 — NGINX HTTPS Configuration

Create a `nginx.conf` file in nginx directory

Copy below nginx configuration into `nginx.config` file.

```nginx
events {}

http {

  server {
      listen 8443 ssl;
      ssl_certificate /etc/nginx/certs/server.crt;
      ssl_certificate_key /etc/nginx/certs/server.key;
      location /api/hello {
          return 200 "Hello from HTTPS Docker Server";
      }
  }
}
```

---

# Step 4 — Dockerfile

Below DockerFile creates an docker image with SSL configuration

```dockerfile
FROM nginx:latest
COPY nginx.conf /etc/nginx/nginx.conf
COPY certs /etc/nginx/certs
EXPOSE 8443
```

---

# Step 5 — Build Docker Image and run container

Run `docker compose up -d ` to run ssl secured nginx docker container

```yaml
services:
  nginx:
    container_name: ssl-secured-server
    build:
      dockerfile: Dockerfile
    ports:
      - "8443:8443"
```
---

Server is now accessible at:

```
https://localhost:8443/api/hello
```

---

# Running the Spring Boot Client

Start the application:

```
gradlew spring-boot:run
```

Test endpoint:

```
GET http://localhost:8080/hello
```

Expected response:

```
Hello from HTTPS Docker Server
```

---
# Verifying SSL Handshake

To ensure the client actually uses TLS, enable SSL debugging.

Run the application with:

```
-Djavax.net.debug=ssl:handshake
```

Logs will display TLS handshake details:

```
ClientHello
ServerHello
Certificate
Finished
```

This confirms that:

* SSL negotiation occurred
* Certificates were validated
* Secure channel was established

---

# Testing Using Curl

```
curl https://localhost:8443/api/hello
```

Without trusted certificate you may see:

```
SSL certificate problem: self signed certificate
```

Once the truststore is configured correctly, the request succeeds.

---

# Learning Outcomes

After running this project you will understand:

* How **HTTPS communication works in Spring Boot**
* How to configure **SSLBundle**
* How to build **secure REST clients**
* How to **simulate external HTTPS APIs locally**
* How to **verify TLS handshake behavior**
