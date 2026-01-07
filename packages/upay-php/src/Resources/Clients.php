<?php

namespace Upay\Resources;

use Upay\HttpClient;

class Clients
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
            throw new \InvalidArgumentException('Nome do cliente é obrigatório');
        }
        
        if (empty($data['email']) || !$this->isValidEmail($data['email'])) {
            throw new \InvalidArgumentException('Email inválido');
        }
        
        return $this->http->post('/clients', $data);
    }
    
    public function list(?array $params = null): array
    {
        $response = $this->http->get('/clients', $params);
        
        return [
            'data' => $response['clients'] ?? $response['data'] ?? [],
            'pagination' => $response['pagination'] ?? ['total' => 0, 'page' => 1, 'limit' => 10]
        ];
    }
    
    public function get(string $id): array
    {
        if (empty($id)) {
            throw new \InvalidArgumentException('ID é obrigatório');
        }
        
        return $this->http->get("/clients/{$id}");
    }
    
    public function update(string $id, array $data): array
    {
        if (empty($id)) {
            throw new \InvalidArgumentException('ID é obrigatório');
        }
        
        if (isset($data['email']) && !$this->isValidEmail($data['email'])) {
            throw new \InvalidArgumentException('Email inválido');
        }
        
        return $this->http->patch("/clients/{$id}", $data);
    }
    
    private function isValidEmail(string $email): bool
    {
        return filter_var($email, FILTER_VALIDATE_EMAIL) !== false;
    }
}
