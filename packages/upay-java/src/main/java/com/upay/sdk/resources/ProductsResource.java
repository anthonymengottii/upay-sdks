package com.upay.sdk.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.upay.sdk.http.HttpClientWrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProductsResource {

    private final HttpClientWrapper http;

    public ProductsResource(HttpClientWrapper http) {
        this.http = Objects.requireNonNull(http, "http must not be null");
    }

    public JsonNode list(Integer page, Integer limit) throws IOException, InterruptedException {
        if (page != null && page <= 0) {
            throw new IllegalArgumentException("page must be > 0");
        }
        if (limit != null && limit <= 0) {
            throw new IllegalArgumentException("limit must be > 0");
        }

        Map<String, Object> params = new HashMap<>();
        if (page != null) params.put("page", page);
        if (limit != null) params.put("limit", limit);

        return http.get("/products", params);
    }
}
