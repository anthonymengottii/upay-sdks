<?php

namespace Upay\Resources;

use Upay\HttpClient;

class PaymentLinks
{
    private HttpClient $http;
    
    public function __construct(HttpClient $http)
    {
        $this->http = $http;
    }
    
    public function create(array $data): array
    {
        // Validação básica
        if (empty($data['title']) || strlen(trim($data['title'])) < 3) {
            throw new \InvalidArgumentException('Título deve ter pelo menos 3 caracteres');
        }
        
        if (empty($data['amount']) && empty($data['products'])) {
            throw new \InvalidArgumentException('É necessário fornecer amount ou products');
        }
        
        // Validação robusta de amount: garantir que seja numérico e respeite o mínimo
        if (isset($data['amount'])) {
            if (!is_numeric($data['amount'])) {
                throw new \InvalidArgumentException('Amount deve ser numérico');
            }
            $amount = (int) $data['amount'];
            if ($amount < 100) {
                throw new \InvalidArgumentException('Valor mínimo é R$ 1,00 (100 centavos)');
            }
        }
        
        $requestData = [
            'title' => $data['title'],
            'description' => $data['description'] ?? null,
            'amountCents' => $data['amount'] ?? null,
            'products' => $data['products'] ?? null,
            'currency' => $data['currency'] ?? 'BRL',
            'expiresAt' => $data['expiresAt'] ?? null,
            'redirectUrl' => $data['redirectUrl'] ?? null,
            'settings' => $data['settings'] ?? null,
            'status' => $data['status'] ?? 'ACTIVE',
            'metaPixelCode' => $data['metaPixelCode'] ?? null,
            'stockQuantity' => $data['stockQuantity'] ?? null,
            'stockEnabled' => $data['stockEnabled'] ?? null,
        ];
        
        // Remove valores null
        $requestData = array_filter($requestData, fn($v) => $v !== null);
        
        $response = $this->http->post('/payment-links', $requestData);
        
        return $response['data'] ?? $response;
    }
    
    public function list(?array $params = null): array
    {
        $response = $this->http->get('/payment-links', $params);
        
        return [
            'data' => $response['paymentLinks'] ?? $response['data'] ?? [],
            'pagination' => $response['pagination'] ?? ['total' => 0, 'page' => 1, 'limit' => 10]
        ];
    }
    
    public function get(string $id): array
    {
        if (trim($id) === '') {
            throw new \InvalidArgumentException('ID é obrigatório');
        }
        
        $encodedId = rawurlencode($id);
        $response = $this->http->get("/payment-links/{$encodedId}");
        
        return $response['paymentLink'] ?? $response['data'] ?? $response;
    }
    
    public function getBySlug(string $slug): array
    {
        if (empty($slug)) {
            throw new \InvalidArgumentException('Slug é obrigatório');
        }
        
        $encodedSlug = rawurlencode($slug);
        return $this->http->get("/payment-links/slug/{$encodedSlug}");
    }
    
    public function update(string $id, array $data): array
    {
        if (trim($id) === '') {
            throw new \InvalidArgumentException('ID é obrigatório');
        }
        
        $encodedId = rawurlencode($id);
        $updateData = [];
        if (isset($data['title'])) $updateData['title'] = $data['title'];
        if (isset($data['description'])) $updateData['description'] = $data['description'];
        if (isset($data['amount'])) $updateData['amountCents'] = $data['amount'];
        if (isset($data['status'])) $updateData['status'] = $data['status'];
        if (isset($data['expiresAt'])) $updateData['expiresAt'] = $data['expiresAt'];
        if (isset($data['redirectUrl'])) $updateData['redirectUrl'] = $data['redirectUrl'];
        if (isset($data['settings'])) $updateData['settings'] = $data['settings'];
        
        return $this->http->patch("/payment-links/{$encodedId}", $updateData);
    }
    
    public function delete(string $id): void
    {
        if (trim($id) === '') {
            throw new \InvalidArgumentException('ID é obrigatório');
        }
        
        $encodedId = rawurlencode($id);
        $this->http->delete("/payment-links/{$encodedId}");
    }
    
    public function getCheckoutUrl(string $slug, ?string $baseUrl = null): string
    {
        $checkoutBase = $baseUrl ?? 'https://checkout.upaybr.com';
        return "{$checkoutBase}/{$slug}";
    }
}
