package com.upay.sdk.examples;

import com.fasterxml.jackson.databind.JsonNode;
import com.upay.sdk.UpayClient;

public class Main {
    public static void main(String[] args) throws Exception {
        // Nunca deixe a API Key hardcoded em código-fonte.
        // Defina a variável de ambiente UPAY_API_KEY antes de executar.
        String apiKey = System.getenv("UPAY_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Environment variable UPAY_API_KEY is required to run this example.");
        }
        String baseUrl = System.getenv("UPAY_BASE_URL");
        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = "https://api.upay-sistema.onrender.com";
        }

        System.out.println("Testando SDK Upay Java...\n");
        System.out.println("Base URL: " + baseUrl + "\n");

        UpayClient upay = new UpayClient(apiKey, baseUrl, "v1", 30);

        // Teste 1: Listar Payment Links
        System.out.println("Teste 1: Listar Payment Links...");
        JsonNode links = upay.paymentLinks.list(1, 5);
        System.out.println(links.toPrettyString());

        // Teste 2: Listar Produtos
        System.out.println("\nTeste 2: Listar Produtos...");
        JsonNode products = upay.products.list(1, 5);
        System.out.println(products.toPrettyString());

        System.out.println("\n[OK] Testes do SDK Java concluídos.");
    }
}
