<?php

namespace Upay\Resources;

use Upay\HttpClient;

class Coupons
{
    private HttpClient $http;
    
    public function __construct(HttpClient $http)
    {
        $this->http = $http;
    }
    
    /**
     * Valida um cupom de desconto
     * 
     * Nota: Este endpoint é público e não requer autenticação
     * Endpoint: POST /api/coupons/validate (não /api/v1)
     */
    public function validate(string $code, int $amountCents, ?array $productIds = null): array
    {
        if (empty($code) || strlen(trim($code)) === 0) {
            throw new \InvalidArgumentException('Código do cupom é obrigatório');
        }
        
        if (empty($amountCents) || $amountCents < 100) {
            throw new \InvalidArgumentException('Valor mínimo é R$ 1,00 (100 centavos)');
        }
        
        // Endpoint público em /api/coupons/validate (sem /v1)
        $baseUrl = $this->http->baseUrl ?? 'https://api.upay-sistema.onrender.com';
        $url = "{$baseUrl}/api/coupons/validate";
        
        // Faz requisição sem autenticação usando cURL
        $ch = curl_init($url);
        
        $data = [
            'code' => trim($code),
            'amountCents' => $amountCents,
            'productIds' => $productIds ?? [],
        ];
        
        curl_setopt_array($ch, [
            CURLOPT_RETURNTRANSFER => true,
            CURLOPT_POST => true,
            CURLOPT_POSTFIELDS => json_encode($data),
            CURLOPT_HTTPHEADER => [
                'Content-Type: application/json',
            ],
            CURLOPT_TIMEOUT => $this->http->timeout ?? 30,
            CURLOPT_SSL_VERIFYPEER => true,
        ]);
        
        $response = curl_exec($ch);
        $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        $error = curl_error($ch);
        curl_close($ch);
        
        if ($error) {
            throw new \Exception("Erro na requisição: {$error}");
        }
        
        $result = json_decode($response, true);
        
        if ($httpCode >= 400) {
            $errorMessage = $result['message'] ?? "HTTP {$httpCode}";
            throw new \Exception($errorMessage);
        }
        
        // Normalizar resposta para o formato esperado
        return [
            'valid' => $result['valid'] ?? false,
            'discountCents' => $result['discountAmount'] ?? 0,
            'discountPercentage' => $result['coupon']['discountPercentage'] ?? null,
            'finalAmountCents' => $result['finalAmount'] ?? $amountCents,
            'message' => $result['error'] ?? $result['message'] ?? null,
        ];
    }
}
