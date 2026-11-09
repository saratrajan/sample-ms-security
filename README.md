# sample-ms-security

A small, self-contained Spring Boot 4.1 / Java 21 / Gradle microservice that serves as the test fixture for the **risk-based dependency regression tool** (`riskbased-testing-ms`).

It is written from scratch and contains no proprietary code. Its *shape* is deliberately close to a typical production microservice, so the tool meets the same challenges it meets in real services.

## What the service does

An order coordinator:

| Endpoint | Purpose | Calls |
|---|---|---|
| `POST /orders/{orderId}:checkout` | Validate, reserve inventory, sign and charge payment, notify | customer, inventory, payment, notification |
| `POST /orders/{orderId}:cancel` | Refund and release inventory | payment, inventory |
| `GET /orders/{orderId}/status` | Local lookup only | none |

Order ids look like `ORD-12345678`. Mutating calls need an `X-Client-Id` header (lowercase, 3 to 32 chars).

## Why it is shaped this way

| Feature | What it tests in the tool |
|---|---|
| Paths built from constants (`ApiPaths`, `ClientPaths`) | Constant resolution in endpoint discovery |
| `@Valid` bodies, nested `@Valid` lists, `@Pattern` path variables | Validation-driven negative tests |
| EL expression in a validation message (`LineItem.unitPrice`) | tomcat-embed-el impact (framework-implicit) |
| `@JsonProperty` snake_case, `@JsonInclude`, `@JsonFormat` | Jackson response-shape probes |
| Jackson 3 (`tools.jackson`) in MVC + direct `JsonMapper` use in `AuditPublisher` | Boot 4 Jackson split |
| 2 servlet filters, 1 path-scoped interceptor, 1 `@RestControllerAdvice` | Cross-cutting impact, header assertions, error mapping |
| 4 Feign clients, 6 operations, custom `ErrorDecoder`, request interceptor | Outbound inventory, Feign client slice |
| feign-micrometer with **zero code references** | Framework-implicit detection |
| BouncyCastle used only in the service layer (`PayloadSigner`) | Call-graph reach from endpoint to library |
| `@Scheduled` job behind `@ConditionalOnProperty` (off by default) | Async inventory, conditional beans |
| Versions from: Spring Boot BOM, Spring Cloud BOM, version catalog, `gradle.properties`, `resolutionStrategy` force | Resolved-version validation |
| `codegen` configuration with an older bcprov (1.80) | Stray-version mismatch detection |
| `commons-fileupload` declared but unused | Honest "no surface" result |

## Ground truth

`riskbased/expected-impact.json` lists, for each library, the endpoints, Feign operations and cross-cutting components it should be found to affect, and the expected validation outcome. `riskbased/feeds/` holds sample package feeds for the tool.

## Build and run

```bash
./gradlew build          # compile + unit/slice tests + JaCoCo report
./gradlew bootRun        # downstream URLs default to localhost:9101..9104
```

Requires JDK 21. No Docker, no database: orders live in memory.
