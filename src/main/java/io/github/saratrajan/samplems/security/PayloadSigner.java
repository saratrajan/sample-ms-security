package io.github.saratrajan.samplems.security;

import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Signs outbound payment payloads with HMAC-SHA256 using BouncyCastle. */
@Component
public class PayloadSigner {

    private final byte[] key;

    public PayloadSigner(@Value("${signing.key}") String key) {
        this.key = key.getBytes(StandardCharsets.UTF_8);
    }

    public String sign(String payload) {
        HMac mac = new HMac(new SHA256Digest());
        mac.init(new KeyParameter(key));
        byte[] input = payload.getBytes(StandardCharsets.UTF_8);
        mac.update(input, 0, input.length);
        byte[] out = new byte[mac.getMacSize()];
        mac.doFinal(out, 0);
        return HexFormat.of().formatHex(out);
    }
}
