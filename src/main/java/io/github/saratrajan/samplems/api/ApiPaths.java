package io.github.saratrajan.samplems.api;

/** Inbound paths. Kept as constants on purpose: impact tools must resolve them. */
public final class ApiPaths {
    public static final String ORDERS = "/orders";
    public static final String ORDER_ID = "/{orderId}";
    public static final String CHECKOUT = ORDER_ID + ":checkout";
    public static final String CANCEL = ORDER_ID + ":cancel";
    public static final String STATUS = ORDER_ID + "/status";

    /** Order ids look like "ORD-" followed by 8 digits. */
    public static final String ORDER_ID_PATTERN = "^ORD-\\d{8}$";

    private ApiPaths() {
    }
}
