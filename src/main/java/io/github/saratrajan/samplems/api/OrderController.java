package io.github.saratrajan.samplems.api;

import io.github.saratrajan.samplems.api.dto.CancelRequest;
import io.github.saratrajan.samplems.api.dto.CancelResponse;
import io.github.saratrajan.samplems.api.dto.CheckoutRequest;
import io.github.saratrajan.samplems.api.dto.CheckoutResponse;
import io.github.saratrajan.samplems.api.dto.OrderStatusResponse;
import io.github.saratrajan.samplems.service.CancelService;
import io.github.saratrajan.samplems.service.CheckoutService;
import io.github.saratrajan.samplems.service.OrderStatusService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.ORDERS)
public class OrderController {

    public static final String CLIENT_ID_HEADER = "X-Client-Id";

    private final CheckoutService checkoutService;
    private final CancelService cancelService;
    private final OrderStatusService orderStatusService;

    public OrderController(CheckoutService checkoutService, CancelService cancelService,
                           OrderStatusService orderStatusService) {
        this.checkoutService = checkoutService;
        this.cancelService = cancelService;
        this.orderStatusService = orderStatusService;
    }

    @PostMapping(path = ApiPaths.CHECKOUT, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CheckoutResponse> checkout(
            @PathVariable @Pattern(regexp = ApiPaths.ORDER_ID_PATTERN) String orderId,
            @RequestHeader(CLIENT_ID_HEADER) String clientId,
            @Valid @RequestBody CheckoutRequest request) {
        CheckoutResponse response = checkoutService.checkout(orderId, clientId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(path = ApiPaths.CANCEL, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CancelResponse> cancel(
            @PathVariable @Pattern(regexp = ApiPaths.ORDER_ID_PATTERN) String orderId,
            @RequestHeader(CLIENT_ID_HEADER) String clientId,
            @Valid @RequestBody CancelRequest request) {
        return ResponseEntity.ok(cancelService.cancel(orderId, clientId, request));
    }

    @GetMapping(path = ApiPaths.STATUS, produces = MediaType.APPLICATION_JSON_VALUE)
    public OrderStatusResponse status(@PathVariable @Pattern(regexp = ApiPaths.ORDER_ID_PATTERN) String orderId) {
        return orderStatusService.status(orderId);
    }
}
