<?php

namespace Upay\Resources;

use Upay\HttpClient;

class Products
{
    private HttpClient $http;
    
    public function __construct(HttpClient $http)
    {
        $this->http = $http;
    }
    
    public function create(array $data): array
    {
        // Validação básica
        if (empty($data['name']) || strlen(trim($data['name'])) === 0) {
            throw new \InvalidArgumentException('Nome do produto é obrigatório');
        }
        
        // Validar presença de priceCents
        if (!isset($data['priceCents'])) {
            throw new \InvalidArgumentException('priceCents é obrigatório');
        }

        // Validar que é numérico
        if (!is_numeric($data['priceCents'])) {
            throw new \InvalidArgumentException('priceCents deve ser numérico');
        }

        $price = (int) $data['priceCents'];
        if ($price < 100) {
            throw new \InvalidArgumentException('Preço mínimo é R$ 1,00 (100 centavos)');
        }
        
        return $this->http->post('/products', $data);
    }
    
    public function list(?array $params = null): array
    {
        $response = $this->http->get('/products', $params);
        
        return [
            'data' => $response['products'] ?? $response['data'] ?? [],
            'pagination' => $response['pagination'] ?? ['total' => 0, 'page' => 1, 'limit' => 10]
        ];
    }
    
    public function get(string $id): array
    {
        if (trim($id) === '') {
            throw new \InvalidArgumentException('ID é obrigatório');
        }
        
        $encodedId = rawurlencode($id);
        return $this->http->get("/products/{$encodedId}");
    }
    
    public function update(string $id, array $data): array
    {
        if (trim($id) === '') {
            throw new \InvalidArgumentException('ID é obrigatório');
        }
        
        if (isset($data['priceCents'])) {
            if (!is_numeric($data['priceCents'])) {
                throw new \InvalidArgumentException('priceCents deve ser numérico');
            }

            $price = (int) $data['priceCents'];
            if ($price < 100) {
                throw new \InvalidArgumentException('Preço mínimo é R$ 1,00 (100 centavos)');
            }
        }
        
        $encodedId = rawurlencode($id);
        return $this->http->patch("/products/{$encodedId}", $data);
    }
    
    public function delete(string $id): void
    {
        if (trim($id) === '') {
            throw new \InvalidArgumentException('ID é obrigatório');
        }
        
        $encodedId = rawurlencode($id);
        $this->http->delete("/products/{$encodedId}");
    }
}
