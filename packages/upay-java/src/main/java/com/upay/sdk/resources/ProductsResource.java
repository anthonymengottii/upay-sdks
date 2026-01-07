package com.upay.sdk.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.upay.sdk.http.HttpClientWrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProductsResource {

    private final HttpClientWrapper http;

    public ProductsResource(HttpClientWrapper http) {
        this.http = http;
    }

    public JsonNode list(Integer page, Integer limit) throws IOException, InterruptedException {
        Map<String, Object> params = new HashMap<>();
        if (page != null) params.put("page", page);
        if (limit != null) params.put("limit", limit);

        return http.get("/products", params);
    }
}
