package com.upay.sdk.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upay.sdk.http.HttpClientWrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TransactionsResource {

    private final HttpClientWrapper http;
    private final ObjectMapper mapper = new ObjectMapper();

    public TransactionsResource(HttpClientWrapper http) {
        this.http = Objects.requireNonNull(http, "HttpClientWrapper cannot be null");
    }

    /**
     * List transactions with pagination
     * @param page Page number (must be positive if provided)
     * @param limit Items per page (must be positive if provided)
     * @return TransactionListResponse with typed response data
     * @throws IllegalArgumentException if page or limit are non-null and <= 0
     * @throws IOException if HTTP request fails
     * @throws InterruptedException if request is interrupted
     */
    public TransactionListResponse list(Integer page, Integer limit) throws IOException, InterruptedException {
        // Validate page parameter
        if (page != null && page <= 0) {
            throw new IllegalArgumentException("page must be a positive integer (>= 1), got: " + page);
        }
        
        // Validate limit parameter
        if (limit != null && limit <= 0) {
            throw new IllegalArgumentException("limit must be a positive integer (>= 1), got: " + limit);
        }
        
        Map<String, Object> params = new HashMap<>();
        if (page != null) params.put("page", page);
        if (limit != null) params.put("limit", limit);

        JsonNode response = http.get("/transactions", params);
        
        // Map JsonNode to TransactionListResponse DTO
        try {
            return mapper.treeToValue(response, TransactionListResponse.class);
        } catch (com.fasterxml.jackson.databind.JsonMappingException e) {
            throw new IOException("Failed to map response to TransactionListResponse: " + e.getMessage(), e);
        }
    }
}
