package io.github.saratrajan.samplems.client;

import io.github.saratrajan.samplems.client.config.FeignClientsConfig;
import io.github.saratrajan.samplems.client.dto.ChargeRequest;
import io.github.saratrajan.samplems.client.dto.ChargeResult;
import io.github.saratrajan.samplems.client.dto.RefundRequest;
import io.github.saratrajan.samplems.client.dto.RefundResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "payment", url = "${clients.payment.url}", configuration = FeignClientsConfig.class)
public interface PaymentClient {

    String SIGNATURE_HEADER = "X-Payload-Signature";

    @PostMapping(path = ClientPaths.PAYMENT_CHARGE, consumes = MediaType.APPLICATION_JSON_VALUE)
    ChargeResult charge(@RequestHeader(SIGNATURE_HEADER) String signature, @RequestBody ChargeRequest request);

    @PostMapping(path = ClientPaths.PAYMENT_REFUND, consumes = MediaType.APPLICATION_JSON_VALUE)
    RefundResult refund(@PathVariable("chargeId") String chargeId, @RequestBody RefundRequest request);
}
