package io.github.saratrajan.samplems.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.saratrajan.samplems.api.dto.CheckoutResponse;
import io.github.saratrajan.samplems.api.dto.OrderState;
import io.github.saratrajan.samplems.service.CancelService;
import io.github.saratrajan.samplems.service.CheckoutService;
import io.github.saratrajan.samplems.service.OrderStatusService;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CheckoutService checkoutService;

    @MockitoBean
    private CancelService cancelService;

    @MockitoBean
    private OrderStatusService orderStatusService;

    @Test
    void checkoutReturnsCreated() throws Exception {
        when(checkoutService.checkout(eq("ORD-00000001"), eq("web-app"), any()))
                .thenReturn(CheckoutResponse.builder().orderId("ORD-00000001").confirmationNumber("CH1")
                        .state(OrderState.CONFIRMED).total(new BigDecimal("20.00")).currency("USD").build());

        mockMvc.perform(post("/orders/ORD-00000001:checkout")
                        .header("X-Client-Id", "web-app")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fixture()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.confirmation_number").value("CH1"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"));
    }

    @Test
    void checkoutWithoutClientIdIsRejected() throws Exception {
        mockMvc.perform(post("/orders/ORD-00000001:checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fixture()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MISSING_CLIENT_ID"));
    }

    private static String fixture() throws Exception {
        return new ClassPathResource("fixtures/checkout-request.json").getContentAsString(StandardCharsets.UTF_8);
    }
}
