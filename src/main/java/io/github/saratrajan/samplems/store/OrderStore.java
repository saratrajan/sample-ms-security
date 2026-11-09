package io.github.saratrajan.samplems.store;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/** In-memory order store. Stands in for a database so the sample needs no infrastructure. */
@Component
public class OrderStore {

    private final Map<String, OrderRecord> orders = new ConcurrentHashMap<>();

    public void save(OrderRecord order) {
        orders.put(order.orderId(), order);
    }

    public Optional<OrderRecord> find(String orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }

    public Collection<OrderRecord> all() {
        return orders.values();
    }
}
