package io.github.saratrajan.samplems.client.config;

import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import io.github.saratrajan.samplems.web.CorrelationIdFilter;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;

/**
 * Shared Feign configuration. Deliberately not a @Configuration class, so it applies only
 * to the clients that reference it. Metrics come from feign-micrometer through Spring Cloud
 * OpenFeign auto-configuration: there is no code reference to it anywhere in this service.
 */
public class FeignClientsConfig {

    @Bean
    ErrorDecoder downstreamErrorDecoder() {
        return new DownstreamErrorDecoder();
    }

    @Bean
    RequestInterceptor correlationIdPropagator() {
        return template -> {
            String correlationId = MDC.get(CorrelationIdFilter.MDC_KEY);
            if (correlationId != null) {
                template.header(CorrelationIdFilter.HEADER, correlationId);
            }
        };
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }
}
