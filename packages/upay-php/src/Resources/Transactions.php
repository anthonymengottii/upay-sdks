<?php

namespace Upay\Resources;

use Upay\HttpClient;

class Transactions
{
    private HttpClient $http;
    
    public function __construct(HttpClient $http)
    {
        $this->http = $http;
    }
    
    /**
     * Valida e codifica o ID da transação
     * 
     * @param string $id ID da transação
     * @return string ID codificado para URL
     * @throws \InvalidArgumentException Se o ID for inválido
     */
    private function validateAndEncodeId(string $id): string
    {
        if (trim($id) === '') {
            throw new \InvalidArgumentException('ID é obrigatório');
        }
        
        return rawurlencode($id);
    }
    
    public function create(array $data): array
    {
        // Validação básica
        if (empty($data['product']) || strlen(trim($data['product'])) === 0) {
            throw new \InvalidArgumentException('Produto é obrigatório');
        }
        
        if (empty($data['amountCents']) || $data['amountCents'] < 100) {
            throw new \InvalidArgumentException('Valor mínimo é R$ 1,00 (100 centavos)');
        }
        
        if (!empty($data['client']) && empty($data['client']['email'])) {
            throw new \InvalidArgumentException('Email do cliente é obrigatório');
        }
        
        return $this->http->post('/transactions', $data);
    }
    
    public function list(?array $params = null): array
    {
        $response = $this->http->get('/transactions', $params);
        
        return [
            'data' => $response['transactions'] ?? $response['data'] ?? [],
            'pagination' => $response['pagination'] ?? ['total' => 0, 'page' => 1, 'limit' => 10]
        ];
    }
    
    public function get(string $id): array
    {
        $encodedId = $this->validateAndEncodeId($id);
        return $this->http->get("/transactions/{$encodedId}");
    }
    
    public function process(string $id, ?array $paymentData = null): array
    {
        $encodedId = $this->validateAndEncodeId($id);
        return $this->http->post("/transactions/{$encodedId}/process", $paymentData);
    }
    
    public function capture(string $id): array
    {
        $encodedId = $this->validateAndEncodeId($id);
        return $this->http->post("/transactions/{$encodedId}/capture");
    }
    
    public function cancel(string $id): array
    {
        $encodedId = $this->validateAndEncodeId($id);
        return $this->http->post("/transactions/{$encodedId}/cancel");
    }
    
    public function refund(string $id, ?int $amountCents = null): array
    {
        $encodedId = $this->validateAndEncodeId($id);
        
        $data = [];
        if ($amountCents !== null) {
            $data['amountCents'] = $amountCents;
        }
        
        return $this->http->post("/transactions/{$encodedId}/refund", $data);
    }
}
