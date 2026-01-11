package com.upay.sdk.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upay.sdk.utils.UpayException;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpClientWrapper {

    private final String apiKey;
    private final String baseUrl;
    private final String version;
    private final int timeoutSeconds;
    private final HttpClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public HttpClientWrapper(String apiKey, String baseUrl, String version, int timeoutSeconds) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("apiKey must not be null or empty");
        }
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException("baseUrl must not be null or empty");
        }
        
        this.apiKey = apiKey;
        String normalizedBaseUrl = baseUrl.trim();
        this.baseUrl = normalizedBaseUrl.endsWith("/") 
                ? normalizedBaseUrl.substring(0, normalizedBaseUrl.length() - 1) 
                : normalizedBaseUrl;
        this.version = version != null ? version : "v1";
        this.timeoutSeconds = timeoutSeconds > 0 ? timeoutSeconds : 30;
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
        return requestPublic("POST", endpoint, body);
    }

    private JsonNode requestPublic(String method, String endpoint, Map<String, Object> body)
            throws IOException, InterruptedException {

        // Normalize endpoint: ensure it starts with a single leading slash
        String normalizedEndpoint = endpoint;
        if (normalizedEndpoint == null || normalizedEndpoint.isEmpty()) {
            normalizedEndpoint = "/";
        } else if (!normalizedEndpoint.startsWith("/")) {
            normalizedEndpoint = "/" + normalizedEndpoint;
        }

        StringBuilder url = new StringBuilder(baseUrl)
                .append("/api/")
                .append(version)
                .append(normalizedEndpoint);

        URI uri;
        try {
            uri = URI.create(url.toString());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid URL constructed: " + url.toString() + ". Original error: " + e.getMessage(), e);
        }

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(uri)
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

        // Normalize endpoint: ensure it starts with a single leading slash
        String normalizedEndpoint = endpoint;
        if (normalizedEndpoint == null || normalizedEndpoint.isEmpty()) {
            normalizedEndpoint = "/";
        } else if (!normalizedEndpoint.startsWith("/")) {
            normalizedEndpoint = "/" + normalizedEndpoint;
        }

        StringBuilder url = new StringBuilder(baseUrl)
                .append("/api/")
                .append(version)
                .append(normalizedEndpoint);

        if (params != null && !params.isEmpty()) {
            String query = params.entrySet().stream()
                    .filter(e -> e.getValue() != null)
                    .map(e -> {
                        String encodedKey = URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8);
                        String encodedValue = URLEncoder.encode(e.getValue().toString(), StandardCharsets.UTF_8);
                        return encodedKey + "=" + encodedValue;
                    })
                    .collect(Collectors.joining("&"));
            if (!query.isEmpty()) url.append("?").append(query);
        }

        URI uri;
        try {
            uri = URI.create(url.toString());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid URL constructed: " + url.toString() + ". Original error: " + e.getMessage(), e);
        }

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(uri)
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
