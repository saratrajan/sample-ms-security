package io.github.saratrajan.samplems.store;

import io.github.saratrajan.samplems.api.dto.OrderState;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record OrderRecord(String orderId, String customerId, OrderState state, String chargeId, String holdId,
                          BigDecimal total, String currency, OffsetDateTime updatedAt) {

    public OrderRecord withState(OrderState newState, OffsetDateTime at) {
        return new OrderRecord(orderId, customerId, newState, chargeId, holdId, total, currency, at);
    }
}
