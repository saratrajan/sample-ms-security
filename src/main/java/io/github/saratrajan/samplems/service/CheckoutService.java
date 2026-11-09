package io.github.saratrajan.samplems.service;

import io.github.saratrajan.samplems.api.dto.CheckoutRequest;
import io.github.saratrajan.samplems.api.dto.CheckoutResponse;

public interface CheckoutService {
    CheckoutResponse checkout(String orderId, String clientId, CheckoutRequest request);
}
