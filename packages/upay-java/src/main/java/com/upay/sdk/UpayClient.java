package com.upay.sdk;

import com.upay.sdk.http.HttpClientWrapper;
import com.upay.sdk.resources.PaymentLinksResource;
import com.upay.sdk.resources.TransactionsResource;
import com.upay.sdk.resources.ProductsResource;
import com.upay.sdk.resources.CouponsResource;
import com.upay.sdk.utils.WebhookUtils;

public class UpayClient {

    private final HttpClientWrapper http;

    public final PaymentLinksResource paymentLinks;
    public final TransactionsResource transactions;
    public final ProductsResource products;
    public final CouponsResource coupons;

    public UpayClient(String apiKey, String baseUrl, String version, int timeoutSeconds) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("API key is required");
        }
        if (version == null || version.isBlank()) {
            throw new IllegalArgumentException("Version is required");
        }
        if (timeoutSeconds <= 0) {
            throw new IllegalArgumentException("Timeout must be a positive integer");
        }
        String url = (baseUrl != null && !baseUrl.trim().isEmpty()) ? baseUrl : "https://upay-sistema-api.onrender.com";
        this.http = new HttpClientWrapper(apiKey, url, version, timeoutSeconds);

        this.paymentLinks = new PaymentLinksResource(http);
        this.transactions = new TransactionsResource(http);
        this.products = new ProductsResource(http);
        this.coupons = new CouponsResource(http);
    }

    public UpayClient(String apiKey) {
        this(apiKey, null, "v1", 30);
    }

    public boolean verifyWebhookSignature(String payload, String signature, String secret) {
        if (payload == null) {
            throw new IllegalArgumentException("Payload is required");
        }
        if (signature == null) {
            throw new IllegalArgumentException("Signature is required");
        }
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("Secret is required");
        }
        return WebhookUtils.verifySignature(payload, signature, secret);
    }
}
