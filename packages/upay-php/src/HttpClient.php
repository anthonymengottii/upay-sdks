<?php

namespace Upay;

use Upay\Utils\Errors;

class HttpClient
{
    private string $apiKey;
    public string $baseUrl;
    private string $version;
    public int $timeout;
    
    public function __construct(
        string $apiKey,
        string $baseUrl,
        string $version = 'v1',
        int $timeout = 30
    ) {
        $this->apiKey = $apiKey;
        $this->baseUrl = rtrim($baseUrl, '/');
        $this->version = $version;
        $this->timeout = $timeout;
    }
    
    /**
     * Faz uma requisição HTTP
     * 
     * @param string $method Método HTTP
     * @param string $endpoint Endpoint da API
     * @param array|null $data Dados para enviar no body
     * @param array|null $params Parâmetros de query
     * @return array Resposta da API parseada
     * @throws \Exception
     */
    public function request(
        string $method,
        string $endpoint,
        ?array $data = null,
        ?array $params = null
    ): array {
        $url = "{$this->baseUrl}/api/{$this->version}{$endpoint}";
        
        // Adiciona query params
        if ($params) {
            $params = array_filter($params, fn($v) => $v !== null);
            if (!empty($params)) {
                $url .= '?' . http_build_query($params);
            }
        }
        
        $ch = curl_init($url);
        
        $headers = [
            "Authorization: Bearer {$this->apiKey}",
            "Content-Type: application/json",
            "User-Agent: Upay-PHP-SDK/1.0.0"
        ];
        
        curl_setopt_array($ch, [
            CURLOPT_RETURNTRANSFER => true,
            CURLOPT_CUSTOMREQUEST => $method,
            CURLOPT_HTTPHEADER => $headers,
            CURLOPT_TIMEOUT => $this->timeout,
            CURLOPT_SSL_VERIFYPEER => true,
        ]);
        
        if ($data && in_array($method, ['POST', 'PATCH', 'PUT'])) {
            curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($data));
        }
        
        $response = curl_exec($ch);
        $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        $error = curl_error($ch);
        curl_close($ch);
        
        if ($error) {
            throw new \Exception("Erro na requisição: {$error}");
        }
        
        $body = json_decode($response, true);
        
        if ($httpCode >= 400) {
            throw Errors::handle($httpCode, $body ?? []);
        }
        
        return $body ?? [];
    }
    
    public function get(string $endpoint, ?array $params = null): array
    {
        return $this->request('GET', $endpoint, null, $params);
    }
    
    public function post(string $endpoint, ?array $data = null): array
    {
        return $this->request('POST', $endpoint, $data);
    }
    
    public function patch(string $endpoint, ?array $data = null): array
    {
        return $this->request('PATCH', $endpoint, $data);
    }
    
    public function delete(string $endpoint): array
    {
        return $this->request('DELETE', $endpoint);
    }
}
