<?php

namespace Upay;

use Upay\HttpClient;
use Upay\Resources\PaymentLinks;
use Upay\Resources\Transactions;
use Upay\Resources\Products;
use Upay\Resources\Clients;
use Upay\Resources\Coupons;
use Upay\Utils\Webhooks;

class UpayClient
{
    private HttpClient $http;
    
    public PaymentLinks $paymentLinks;
    public Transactions $transactions;
    public Products $products;
    public Clients $clients;
    public Coupons $coupons;
    
    public function __construct(
        string $apiKey,
        ?string $baseUrl = null,
        string $version = 'v1',
        int $timeout = 30
    ) {
        // Validar API key
        if (empty($apiKey)) {
            throw new \InvalidArgumentException('API Key é obrigatória');
        }
        
        // Validar version
        if (empty($version) || trim($version) === '') {
            throw new \InvalidArgumentException('Version deve ser uma string não vazia');
        }
        
        // Validar timeout
        if ($timeout <= 0) {
            throw new \InvalidArgumentException('Timeout deve ser um inteiro positivo');
        }
        
        // Validar baseUrl se fornecido
        $finalBaseUrl = $baseUrl ?? 'https://upay-sistema-api.onrender.com/';
        if ($baseUrl !== null) {
            $validatedUrl = filter_var($baseUrl, FILTER_VALIDATE_URL);
            if ($validatedUrl === false) {
                throw new \InvalidArgumentException('baseUrl deve ser uma URL válida');
            }
            $finalBaseUrl = $validatedUrl;
        }
        
        $this->http = new HttpClient(
            $apiKey,
            $finalBaseUrl,
            $version,
            $timeout
        );
        
        // Inicializa recursos
        $this->paymentLinks = new PaymentLinks($this->http);
        $this->transactions = new Transactions($this->http);
        $this->products = new Products($this->http);
        $this->clients = new Clients($this->http);
        $this->coupons = new Coupons($this->http);
    }
    
    /**
     * Verifica a assinatura de um webhook
     * 
     * @param string $payload Corpo da requisição
     * @param string $signature Assinatura recebida no header
     * @param string $secret Secret da API key
     * @return bool True se a assinatura for válida
     */
    public function verifyWebhookSignature(
        string $payload,
        string $signature,
        string $secret
    ): bool {
        return Webhooks::verify($payload, $signature, $secret);
    }
    
    /**
     * Extrai a assinatura do header da requisição
     * 
     * @param array $headers Headers da requisição
     * @return string|null A assinatura ou null se não encontrada
     */
    public function extractWebhookSignature(array $headers): ?string
    {
        return Webhooks::extractSignature($headers);
    }
}
