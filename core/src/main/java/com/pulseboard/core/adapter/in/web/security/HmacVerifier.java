package com.pulseboard.core.adapter.in.web.security;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class HmacVerifier {

    private static final String PREFIX = "sha256=";

    private HmacVerifier() {
    }

    public static String sign(String secret, byte[] body) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return PREFIX + HexFormat.of().formatHex(mac.doFinal(body));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException(e);
        }
    }

    /** Header format: sha256=&lt;hex&gt;. A blank secret never verifies. */
    public static boolean verify(String secret, byte[] body, String header) {
        if (secret == null || secret.isBlank() || header == null) {
            return false;
        }
        byte[] expected = sign(secret, body).getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(expected, header.getBytes(StandardCharsets.UTF_8));
    }
}
