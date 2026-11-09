package io.github.saratrajan.samplems.service;

import io.github.saratrajan.samplems.api.dto.CancelRequest;
import io.github.saratrajan.samplems.api.dto.CancelResponse;
import io.github.saratrajan.samplems.api.dto.OrderState;
import io.github.saratrajan.samplems.client.InventoryClient;
import io.github.saratrajan.samplems.client.PaymentClient;
import io.github.saratrajan.samplems.client.dto.RefundRequest;
import io.github.saratrajan.samplems.client.dto.RefundResult;
import io.github.saratrajan.samplems.error.InvalidOrderStateException;
import io.github.saratrajan.samplems.error.OrderNotFoundException;
import io.github.saratrajan.samplems.store.OrderRecord;
import io.github.saratrajan.samplems.store.OrderStore;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class CancelServiceImpl implements CancelService {

    private final PaymentClient paymentClient;
    private final InventoryClient inventoryClient;
    private final AuditPublisher audit;
    private final OrderStore store;

    public CancelServiceImpl(PaymentClient paymentClient, InventoryClient inventoryClient, AuditPublisher audit,
                             OrderStore store) {
        this.paymentClient = paymentClient;
        this.inventoryClient = inventoryClient;
        this.audit = audit;
        this.store = store;
    }

    @Override
    public CancelResponse cancel(String orderId, String clientId, CancelRequest request) {
        OrderRecord order = store.find(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order " + orderId + " not found"));
        if (order.state() == OrderState.CANCELLED) {
            throw new InvalidOrderStateException("Order " + orderId + " is already cancelled");
        }

        BigDecimal refunded = BigDecimal.ZERO;
        if (order.state() == OrderState.CONFIRMED && order.chargeId() != null) {
            RefundResult refund = paymentClient.refund(order.chargeId(),
                    RefundRequest.builder().amount(order.total()).reason(request.getReason().name()).build());
            refunded = refund.getAmount();
        }
        if (order.holdId() != null) {
            inventoryClient.release(order.holdId());
        }
        store.save(order.withState(OrderState.CANCELLED, OffsetDateTime.now()));
        audit.publish("CANCEL", orderId, Map.of("clientId", clientId, "reason", request.getReason().name()));

        return CancelResponse.builder().orderId(orderId).state(OrderState.CANCELLED).refundedAmount(refunded).build();
    }
}
