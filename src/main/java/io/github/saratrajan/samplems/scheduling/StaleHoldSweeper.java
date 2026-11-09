package io.github.saratrajan.samplems.scheduling;

import io.github.saratrajan.samplems.api.dto.OrderState;
import io.github.saratrajan.samplems.client.InventoryClient;
import io.github.saratrajan.samplems.store.OrderStore;
import java.time.OffsetDateTime;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Releases inventory holds for orders stuck in PENDING_PAYMENT. Off by default (conditional bean). */
@Component
@ConditionalOnProperty(name = "sweeper.enabled", havingValue = "true")
public class StaleHoldSweeper {

    private final OrderStore store;
    private final InventoryClient inventoryClient;

    public StaleHoldSweeper(OrderStore store, InventoryClient inventoryClient) {
        this.store = store;
        this.inventoryClient = inventoryClient;
    }

    @Scheduled(fixedDelayString = "${sweeper.fixed-delay:PT5M}")
    public void sweep() {
        OffsetDateTime cutoff = OffsetDateTime.now().minusMinutes(30);
        store.all().stream()
                .filter(o -> o.state() == OrderState.PENDING_PAYMENT && o.updatedAt().isBefore(cutoff) && o.holdId() != null)
                .forEach(o -> {
                    inventoryClient.release(o.holdId());
                    store.save(o.withState(OrderState.CANCELLED, OffsetDateTime.now()));
                });
    }
}
