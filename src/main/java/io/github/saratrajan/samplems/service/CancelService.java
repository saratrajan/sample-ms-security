package io.github.saratrajan.samplems.service;

import io.github.saratrajan.samplems.api.dto.CancelRequest;
import io.github.saratrajan.samplems.api.dto.CancelResponse;

public interface CancelService {
    CancelResponse cancel(String orderId, String clientId, CancelRequest request);
}
