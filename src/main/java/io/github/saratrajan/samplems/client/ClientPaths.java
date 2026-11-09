package io.github.saratrajan.samplems.client;

/** Outbound paths, as constants, the same way many services keep them. */
public final class ClientPaths {
    public static final String PAYMENTS = "/v1/payments";
    public static final String PAYMENT_CHARGE = PAYMENTS + "/charges";
    public static final String PAYMENT_REFUND = PAYMENTS + "/charges/{chargeId}/refunds";
    public static final String INVENTORY_HOLDS = "/v2/inventory/holds";
    public static final String INVENTORY_HOLD = INVENTORY_HOLDS + "/{holdId}";
    public static final String CUSTOMER = "/v1/customers/{customerId}";
    public static final String NOTIFICATIONS = "/v1/notifications";

    private ClientPaths() {
    }
}
