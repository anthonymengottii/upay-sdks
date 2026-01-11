package com.upay.sdk.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.upay.sdk.http.HttpClientWrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class PaymentLinksResource {

    private final HttpClientWrapper http;

    public PaymentLinksResource(HttpClientWrapper http) {
        this.http = Objects.requireNonNull(http, "HttpClientWrapper cannot be null");
    }

    public JsonNode create(String title, Integer amountCents, Map<String, Object> options)
            throws IOException, InterruptedException {

        if (title == null || title.trim().length() < 3) {
            throw new IllegalArgumentException("Title must be at least 3 characters");
        }
        if ((amountCents == null || amountCents < 100) && (options == null || !options.containsKey("products"))) {
            throw new IllegalArgumentException("Either products must be provided or amountCents must be at least 100");
        }

        Map<String, Object> body = new HashMap<>();
        body.put("title", title);
        if (amountCents != null) {
            body.put("amountCents", amountCents);
        }
        if (options != null) {
            // Defensive copy: only add options that don't override validated fields
            for (Map.Entry<String, Object> entry : options.entrySet()) {
                String key = entry.getKey();
                if (!"title".equals(key) && !"amountCents".equals(key)) {
                    body.put(key, entry.getValue());
                }
            }
        }

        // Return full response for consistency
        return http.post("/payment-links", body);
    }

    public JsonNode list(Integer page, Integer limit) throws IOException, InterruptedException {
        Map<String, Object> params = new HashMap<>();
        if (page != null) params.put("page", page);
        if (limit != null) params.put("limit", limit);

        return http.get("/payment-links", params);
    }

    public JsonNode get(String id) throws IOException, InterruptedException {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID is required");
        }
        String encodedId = URLEncoder.encode(id, StandardCharsets.UTF_8);
        // Return full response for consistency
        return http.get("/payment-links/" + encodedId, null);
    }
}
