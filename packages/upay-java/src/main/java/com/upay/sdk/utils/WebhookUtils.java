package com.upay.sdk.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public final class WebhookUtils {

    private WebhookUtils() {}

    public static boolean verifySignature(String payload, String signature, String secret) {
        if (payload == null || payload.trim().isEmpty() || 
            signature == null || signature.trim().isEmpty() || 
            secret == null || secret.trim().isEmpty()) {
            return false;
        }
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String expected = bytesToHex(hash);
            return constantTimeEquals(expected, stripPrefix(signature));
        } catch (Exception e) {
            return false;
        }
    }

    private static String stripPrefix(String sig) {
        return sig.startsWith("sha256=") ? sig.substring(7) : sig;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) return false;
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}
