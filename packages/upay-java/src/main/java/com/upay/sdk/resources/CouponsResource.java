package com.upay.sdk.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.upay.sdk.http.HttpClientWrapper;
import com.upay.sdk.utils.UpayException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class CouponsResource {

    private final HttpClientWrapper http;

    public CouponsResource(HttpClientWrapper http) {
        this.http = http;
    }

    /**
     * Valida um cupom de desconto usando o endpoint público /api/coupons/validate
     */
    public JsonNode validate(String code, int amountCents) throws IOException, InterruptedException {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Código do cupom é obrigatório");
        }
        if (amountCents < 100) {
            throw new IllegalArgumentException("Valor mínimo é R$ 1,00 (100 centavos)");
        }

        String base = http.getBaseUrl();
        String url = base + "/api/coupons/validate";

        Map<String, Object> body = new HashMap<>();
        body.put("code", code.trim());
        body.put("amountCents", amountCents);

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(body)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        int status = response.statusCode();
        String respBody = response.body();

        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        JsonNode json = respBody != null && !respBody.isEmpty()
                ? mapper.readTree(respBody)
                : mapper.createObjectNode();

        if (status >= 400) {
            String message = json.path("message").asText("HTTP " + status);
            throw new UpayException(message, null, status);
        }

        return json;
    }
}
