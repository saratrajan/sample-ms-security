package io.github.saratrajan.samplems.service;

import io.github.saratrajan.samplems.api.dto.OrderStatusResponse;
import io.github.saratrajan.samplems.error.OrderNotFoundException;
import io.github.saratrajan.samplems.store.OrderStore;
import org.springframework.stereotype.Service;

/** Status lookups touch only the local store: no outbound calls, no signing. */
@Service
public class OrderStatusService {

    private final OrderStore store;

    public OrderStatusService(OrderStore store) {
        this.store = store;
    }

    public OrderStatusResponse status(String orderId) {
        return store.find(orderId)
                .map(o -> OrderStatusResponse.builder().orderId(o.orderId()).state(o.state()).updatedAt(o.updatedAt()).build())
                .orElseThrow(() -> new OrderNotFoundException("Order " + orderId + " not found"));
    }
}
