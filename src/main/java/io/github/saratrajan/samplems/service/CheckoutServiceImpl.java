package io.github.saratrajan.samplems.service;

import io.github.saratrajan.samplems.api.dto.CheckoutRequest;
import io.github.saratrajan.samplems.api.dto.CheckoutResponse;
import io.github.saratrajan.samplems.api.dto.LineItem;
import io.github.saratrajan.samplems.api.dto.OrderState;
import io.github.saratrajan.samplems.client.CustomerClient;
import io.github.saratrajan.samplems.client.InventoryClient;
import io.github.saratrajan.samplems.client.NotificationClient;
import io.github.saratrajan.samplems.client.PaymentClient;
import io.github.saratrajan.samplems.client.dto.ChargeRequest;
import io.github.saratrajan.samplems.client.dto.ChargeResult;
import io.github.saratrajan.samplems.client.dto.CustomerProfile;
import io.github.saratrajan.samplems.client.dto.HoldLine;
import io.github.saratrajan.samplems.client.dto.HoldRequest;
import io.github.saratrajan.samplems.client.dto.HoldResult;
import io.github.saratrajan.samplems.client.dto.NotificationRequest;
import io.github.saratrajan.samplems.error.InvalidOrderStateException;
import io.github.saratrajan.samplems.security.PayloadSigner;
import io.github.saratrajan.samplems.store.OrderRecord;
import io.github.saratrajan.samplems.store.OrderStore;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class CheckoutServiceImpl implements CheckoutService {

    private final CustomerClient customerClient;
    private final InventoryClient inventoryClient;
    private final PaymentClient paymentClient;
    private final NotificationClient notificationClient;
    private final PayloadSigner signer;
    private final AuditPublisher audit;
    private final OrderStore store;

    public CheckoutServiceImpl(CustomerClient customerClient, InventoryClient inventoryClient, PaymentClient paymentClient,
                               NotificationClient notificationClient, PayloadSigner signer, AuditPublisher audit,
                               OrderStore store) {
        this.customerClient = customerClient;
        this.inventoryClient = inventoryClient;
        this.paymentClient = paymentClient;
        this.notificationClient = notificationClient;
        this.signer = signer;
        this.audit = audit;
        this.store = store;
    }

    @Override
    public CheckoutResponse checkout(String orderId, String clientId, CheckoutRequest request) {
        store.find(orderId).ifPresent(existing -> {
            throw new InvalidOrderStateException("Order " + orderId + " is already " + existing.state());
        });

        CustomerProfile customer = customerClient.getCustomer(request.getCustomerId());
        if (!customer.isActive()) {
            throw new InvalidOrderStateException("Customer " + customer.getId() + " is not active");
        }

        HoldResult hold = inventoryClient.reserve(HoldRequest.builder()
                .orderReference(orderId)
                .lines(request.getItems().stream().map(i -> new HoldLine(i.getSku(), i.getQuantity())).toList())
                .build());

        List<String> warnings = new ArrayList<>();
        if (hold.isPartial()) {
            warnings.add("Unavailable: " + String.join(",", hold.getUnavailableSkus()));
        }

        BigDecimal total = total(request.getItems());
        ChargeRequest charge = ChargeRequest.builder()
                .orderReference(orderId)
                .customerId(customer.getId())
                .amount(total)
                .currency(request.getCurrency())
                .paymentToken(request.getPayment().getToken())
                .method(request.getPayment().getMethod().name())
                .build();
        String signature = signer.sign(orderId + "|" + total.toPlainString() + "|" + request.getCurrency());
        ChargeResult result = paymentClient.charge(signature, charge);

        OrderState state = "AUTHORIZED".equals(result.getStatus()) ? OrderState.CONFIRMED : OrderState.PENDING_PAYMENT;
        OffsetDateTime now = OffsetDateTime.now();
        store.save(new OrderRecord(orderId, customer.getId(), state, result.getChargeId(), hold.getHoldId(), total,
                request.getCurrency(), now));

        if (customer.getEmail() != null) {
            notificationClient.send(NotificationRequest.builder()
                    .channel("EMAIL").recipient(customer.getEmail()).template("order-confirmation")
                    .data(Map.of("orderId", orderId, "total", total.toPlainString())).build());
        }
        audit.publish("CHECKOUT", orderId, Map.of("clientId", clientId, "state", state.name(), "total", total));

        return CheckoutResponse.builder()
                .orderId(orderId)
                .confirmationNumber(result.getChargeId())
                .state(state)
                .total(total)
                .currency(request.getCurrency())
                .completedAt(now)
                .warnings(warnings.isEmpty() ? null : warnings)
                .build();
    }

    static BigDecimal total(List<LineItem> items) {
        return items.stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
