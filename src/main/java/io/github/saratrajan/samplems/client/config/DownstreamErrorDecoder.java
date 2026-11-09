package io.github.saratrajan.samplems.client.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import io.github.saratrajan.samplems.error.DownstreamServiceException;
import io.github.saratrajan.samplems.error.OrderNotFoundException;

/** Maps downstream failures to service exceptions that GlobalExceptionHandler turns into responses. */
public class DownstreamErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();
        String service = response.request() == null ? "unknown" : clientName(methodKey);
        if (status == 404 && methodKey.startsWith("CustomerClient")) {
            return new OrderNotFoundException("Customer not found for " + methodKey);
        }
        return new DownstreamServiceException(service, status, status >= 500);
    }

    private static String clientName(String methodKey) {
        int hash = methodKey.indexOf('#');
        String type = hash > 0 ? methodKey.substring(0, hash) : methodKey;
        return type.replace("Client", "").toLowerCase();
    }
}
