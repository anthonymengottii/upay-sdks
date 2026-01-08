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
            baseUrl = "https://upay-sistema-api.onrender.com";
        }

        System.out.println("Testando SDK Upay Java...\n");
        System.out.println("Base URL: " + baseUrl + "\n");

        UpayClient upay = new UpayClient(apiKey, baseUrl, "v1", 30);

        // Teste 1: Listar Payment Links
        JsonNode links = upay.paymentLinks.list(1, 5);
        testListEndpoint("Teste 1: Listar Payment Links...", links, "Payment Links");

        // Teste 2: Listar Produtos
        JsonNode products = upay.products.list(1, 5);
        testListEndpoint("\nTeste 2: Listar Produtos...", products, "Produtos");

        System.out.println("\n[OK] Testes do SDK Java concluídos.");
    }

    /**
     * Helper method to test list endpoints and print results
     * @param title Title to print before the test
     * @param response JsonNode response from the API
     * @param itemTypeLabel Label for the item type (e.g., "Payment Links", "Produtos")
     */
    private static void testListEndpoint(String title, JsonNode response, String itemTypeLabel) {
        System.out.println(title);
        
        if (response.has("data") && response.get("data").isArray()) {
            JsonNode dataArray = response.get("data");
            int count = dataArray.size();
            System.out.println(itemTypeLabel + " encontrados: " + count);
            
            if (count > 0) {
                System.out.println("Primeiros IDs: ");
                for (int i = 0; i < Math.min(count, 3); i++) {
                    JsonNode item = dataArray.get(i);
                    if (item.has("id")) {
                        System.out.println("  - " + item.get("id").asText());
                    }
                }
            }
        } else {
            System.out.println("Resposta recebida (estrutura não esperada)");
        }
    }
}
