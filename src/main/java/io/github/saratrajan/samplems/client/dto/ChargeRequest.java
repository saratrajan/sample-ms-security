package io.github.saratrajan.samplems.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChargeRequest {
    @JsonProperty("order_ref")
    private String orderReference;
    private String customerId;
    private BigDecimal amount;
    private String currency;
    private String paymentToken;
    private String method;
}
