package com.upay.sdk.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.upay.sdk.http.HttpClientWrapper;

import java.io.IOException;
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

        Map<String, Object> body = new HashMap<>();
        body.put("code", code.trim());
        body.put("amountCents", amountCents);

        return http.postPublic("/coupons/validate", body);
    }
}
