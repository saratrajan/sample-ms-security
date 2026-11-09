package io.github.saratrajan.samplems.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {

    @NotBlank
    @Pattern(regexp = "^C\\d{6}$", message = "customerId must look like C123456")
    @JsonProperty("customer_id")
    private String customerId;

    @NotBlank
    @Size(min = 3, max = 3)
    private String currency;

    @NotEmpty
    @Size(max = 20)
    private List<@Valid LineItem> items;

    @NotNull
    @Valid
    private PaymentDetails payment;

    @NotNull
    @JsonProperty("requested_at")
    private OffsetDateTime requestedAt;

    @Size(max = 200)
    private String notes;
}
