package io.github.saratrajan.samplems.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetails {

    @NotNull
    private PaymentMethod method;

    @NotBlank
    @Size(min = 16, max = 64)
    private String token;
}
