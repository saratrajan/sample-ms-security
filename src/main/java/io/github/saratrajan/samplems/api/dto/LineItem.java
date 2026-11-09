package io.github.saratrajan.samplems.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LineItem {

    @NotBlank
    @Pattern(regexp = "^[A-Z]{3}-\\d{4}$")
    private String sku;

    @Min(1)
    @Max(10)
    private int quantity;

    // The message uses an EL expression (${validatedValue}), so Jakarta EL (tomcat-embed-el) is exercised.
    @NotNull
    @DecimalMin(value = "0.01", message = "unitPrice must be at least {value} but was ${validatedValue}")
    @Digits(integer = 7, fraction = 2)
    private BigDecimal unitPrice;
}
