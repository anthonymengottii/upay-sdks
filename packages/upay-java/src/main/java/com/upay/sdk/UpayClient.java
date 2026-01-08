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
            throw new IllegalArgumentException("API Key é obrigatória");
        }
        String url = baseUrl != null ? baseUrl : "https://upay-sistema-api.onrender.com";
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
        return WebhookUtils.verifySignature(payload, signature, secret);
    }
}
