package io.github.saratrajan.samplems.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CancelResponse {

    @JsonProperty("order_id")
    private String orderId;

    private OrderState state;

    @JsonProperty("refunded_amount")
    private BigDecimal refundedAmount;
}
