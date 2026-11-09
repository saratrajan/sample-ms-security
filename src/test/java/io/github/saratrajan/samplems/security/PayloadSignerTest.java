package io.github.saratrajan.samplems.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PayloadSignerTest {

    @Test
    void signsWithHmacSha256() {
        // Reference value computed independently with Python's hmac module.
        assertThat(new PayloadSigner("k").sign("ORD-00000001|20.00|USD"))
                .isEqualTo("2adff004ed19b0ed0e573dd9871c8b8e8d7b1ca7f3afd8705b23f672cf3ddcd5");
    }
}
