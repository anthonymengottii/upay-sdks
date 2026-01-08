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
        // Validação de tipo e valor para name
        if (!isset($data['name']) || !is_string($data['name'])) {
            throw new \InvalidArgumentException('Nome do cliente é obrigatório e deve ser uma string');
        }
        
        $name = trim($data['name']);
        if (strlen($name) === 0) {
            throw new \InvalidArgumentException('Nome do cliente é obrigatório');
        }
        
        // Validação de tipo e valor para email
        if (!isset($data['email']) || !is_string($data['email'])) {
            throw new \InvalidArgumentException('Email é obrigatório e deve ser uma string');
        }
        
        if (!$this->isValidEmail($data['email'])) {
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
        // Validação estrita que aceita "0" como ID válido
        // Como o parâmetro é tipado como string, apenas verificamos se está vazio após trim
        if (trim($id) === '') {
            throw new \InvalidArgumentException('ID é obrigatório');
        }
        
        $encodedId = rawurlencode($id);
        return $this->http->get("/clients/{$encodedId}");
    }
    
    public function update(string $id, array $data): array
    {
        // Validação estrita que aceita "0" como ID válido
        // Como o parâmetro é tipado como string, apenas verificamos se está vazio após trim
        if (trim($id) === '') {
            throw new \InvalidArgumentException('ID é obrigatório');
        }
        
        // Validação de tipo para email se fornecido
        if (isset($data['email'])) {
            if (!is_string($data['email'])) {
                throw new \InvalidArgumentException('Email deve ser uma string');
            }
            if (!$this->isValidEmail($data['email'])) {
                throw new \InvalidArgumentException('Email inválido');
            }
        }
        
        $encodedId = rawurlencode($id);
        return $this->http->patch("/clients/{$encodedId}", $data);
    }
    
    private function isValidEmail(string $email): bool
    {
        return filter_var($email, FILTER_VALIDATE_EMAIL) !== false;
    }
}
