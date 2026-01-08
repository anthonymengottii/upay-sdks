package com.upay.sdk.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upay.sdk.utils.UpayException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

public class HttpClientWrapper {

    private final String apiKey;
    private final String baseUrl;
    private final String version;
    private final int timeoutSeconds;
    private final HttpClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public HttpClientWrapper(String apiKey, String baseUrl, String version, int timeoutSeconds) {
        // Validate apiKey
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("API key is required");
        }
        this.apiKey = apiKey;
        
        // Validate baseUrl
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Base URL is required");
        }
        String trimmedBaseUrl = baseUrl.trim();
        this.baseUrl = trimmedBaseUrl.endsWith("/") ? trimmedBaseUrl.substring(0, trimmedBaseUrl.length() - 1) : trimmedBaseUrl;
        
        // Validate and set version
        if (version == null || version.trim().isEmpty()) {
            this.version = "v1";
        } else {
            this.version = version.trim();
        }
        
        // Validate and set timeoutSeconds
        if (timeoutSeconds <= 0) {
            this.timeoutSeconds = 30;
        } else {
            this.timeoutSeconds = timeoutSeconds;
        }
        
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(this.timeoutSeconds))
                .build();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public JsonNode get(String endpoint, Map<String, Object> params) throws IOException, InterruptedException {
        return request("GET", endpoint, null, params);
    }

    public JsonNode post(String endpoint, Map<String, Object> body) throws IOException, InterruptedException {
        return request("POST", endpoint, body, null);
    }

    public JsonNode patch(String endpoint, Map<String, Object> body) throws IOException, InterruptedException {
        return request("PATCH", endpoint, body, null);
    }

    public JsonNode delete(String endpoint) throws IOException, InterruptedException {
        return request("DELETE", endpoint, null, null);
    }

    public JsonNode postPublic(String endpoint, Map<String, Object> body) throws IOException, InterruptedException {
        return requestPublic("POST", endpoint, body, null);
    }

    private JsonNode requestPublic(String method, String endpoint, Map<String, Object> body, Map<String, Object> params)
            throws IOException, InterruptedException {

        StringBuilder url = new StringBuilder(baseUrl)
                .append(endpoint.startsWith("/") ? endpoint : "/" + endpoint);

        if (params != null && !params.isEmpty()) {
            String query = params.entrySet().stream()
                    .filter(e -> e.getValue() != null)
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .reduce((a, b) -> a + "&" + b)
                    .orElse("");
            if (!query.isEmpty()) url.append("?").append(query);
        }

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url.toString()))
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .header("Content-Type", "application/json")
                .header("User-Agent", "Upay-Java-SDK/1.0.0");

        if (body != null) {
            builder.method(method, HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)));
        } else {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        }

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        String respBody = response.body();

        JsonNode json;
        try {
            json = respBody != null && !respBody.isEmpty()
                    ? mapper.readTree(respBody)
                    : mapper.createObjectNode();
        } catch (Exception e) {
            json = mapper.createObjectNode();
        }

        if (status >= 400) {
            String message = json.path("message").asText("HTTP " + status);
            String code = json.path("code").asText(null);
            throw new UpayException(message, code, status);
        }

        return json;
    }

    private JsonNode request(String method, String endpoint, Map<String, Object> body, Map<String, Object> params)
            throws IOException, InterruptedException {

        StringBuilder url = new StringBuilder(baseUrl)
                .append("/api/")
                .append(version)
                .append(endpoint);

        if (params != null && !params.isEmpty()) {
            String query = params.entrySet().stream()
                    .filter(e -> e.getValue() != null)
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .reduce((a, b) -> a + "&" + b)
                    .orElse("");
            if (!query.isEmpty()) url.append("?").append(query);
        }

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url.toString()))
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .header("User-Agent", "Upay-Java-SDK/1.0.0");

        if (body != null) {
            builder.method(method, HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)));
        } else {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        }

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        String respBody = response.body();

        JsonNode json;
        try {
            json = respBody != null && !respBody.isEmpty()
                    ? mapper.readTree(respBody)
                    : mapper.createObjectNode();
        } catch (Exception e) {
            json = mapper.createObjectNode();
        }

        if (status >= 400) {
            String message = json.path("message").asText("HTTP " + status);
            String code = json.path("code").asText(null);
            throw new UpayException(message, code, status);
        }

        return json;
    }
}
