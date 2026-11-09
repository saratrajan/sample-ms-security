package io.github.saratrajan.samplems.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.saratrajan.samplems.api.dto.CheckoutRequest;
import io.github.saratrajan.samplems.api.dto.CheckoutResponse;
import io.github.saratrajan.samplems.api.dto.LineItem;
import io.github.saratrajan.samplems.api.dto.OrderState;
import io.github.saratrajan.samplems.api.dto.PaymentDetails;
import io.github.saratrajan.samplems.api.dto.PaymentMethod;
import io.github.saratrajan.samplems.client.CustomerClient;
import io.github.saratrajan.samplems.client.InventoryClient;
import io.github.saratrajan.samplems.client.NotificationClient;
import io.github.saratrajan.samplems.client.PaymentClient;
import io.github.saratrajan.samplems.client.dto.ChargeResult;
import io.github.saratrajan.samplems.client.dto.CustomerProfile;
import io.github.saratrajan.samplems.client.dto.HoldResult;
import io.github.saratrajan.samplems.error.InvalidOrderStateException;
import io.github.saratrajan.samplems.security.PayloadSigner;
import io.github.saratrajan.samplems.store.OrderStore;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CheckoutServiceImplTest {

    private CustomerClient customerClient;
    private InventoryClient inventoryClient;
    private PaymentClient paymentClient;
    private NotificationClient notificationClient;
    private OrderStore store;
    private CheckoutServiceImpl service;

    @BeforeEach
    void setUp() {
        customerClient = mock(CustomerClient.class);
        inventoryClient = mock(InventoryClient.class);
        paymentClient = mock(PaymentClient.class);
        notificationClient = mock(NotificationClient.class);
        store = new OrderStore();
        service = new CheckoutServiceImpl(customerClient, inventoryClient, paymentClient, notificationClient,
                new PayloadSigner("k"), mock(AuditPublisher.class), store);
    }

    @Test
    void confirmsAuthorizedOrder() {
        when(customerClient.getCustomer("C123456")).thenReturn(new CustomerProfile("C123456", "GOLD", true, "a@b.c"));
        when(inventoryClient.reserve(any())).thenReturn(new HoldResult("H1", false, List.of()));
        when(paymentClient.charge(anyString(), any())).thenReturn(new ChargeResult("CH1", "AUTHORIZED", new BigDecimal("20.00")));

        CheckoutResponse response = service.checkout("ORD-00000001", "web-app", request());

        assertThat(response.getState()).isEqualTo(OrderState.CONFIRMED);
        assertThat(response.getTotal()).isEqualByComparingTo("20.00");
        assertThat(response.getConfirmationNumber()).isEqualTo("CH1");
        assertThat(store.find("ORD-00000001")).isPresent();
        verify(notificationClient).send(any());
    }

    @Test
    void rejectsInactiveCustomer() {
        when(customerClient.getCustomer("C123456")).thenReturn(new CustomerProfile("C123456", "GOLD", false, null));

        assertThatThrownBy(() -> service.checkout("ORD-00000002", "web-app", request()))
                .isInstanceOf(InvalidOrderStateException.class);
        verify(paymentClient, never()).charge(anyString(), any());
    }

    @Test
    void reportsPartialHoldAsWarning() {
        when(customerClient.getCustomer("C123456")).thenReturn(new CustomerProfile("C123456", "GOLD", true, null));
        when(inventoryClient.reserve(any())).thenReturn(new HoldResult("H2", true, List.of("ABC-0002")));
        when(paymentClient.charge(anyString(), any())).thenReturn(new ChargeResult("CH2", "PENDING", null));

        CheckoutResponse response = service.checkout("ORD-00000003", "web-app", request());

        assertThat(response.getState()).isEqualTo(OrderState.PENDING_PAYMENT);
        assertThat(response.getWarnings()).containsExactly("Unavailable: ABC-0002");
    }

    private static CheckoutRequest request() {
        return CheckoutRequest.builder()
                .customerId("C123456")
                .currency("USD")
                .items(List.of(new LineItem("ABC-0001", 2, new BigDecimal("10.00"))))
                .payment(new PaymentDetails(PaymentMethod.CARD, "tok_1234567890abcdef"))
                .requestedAt(OffsetDateTime.parse("2026-11-09T10:00:00-08:00"))
                .build();
    }
}
