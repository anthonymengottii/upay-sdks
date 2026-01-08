<?php

namespace Upay\Utils\Exceptions;

class UpayError extends \Exception
{
    public ?string $errorCode;
    public ?int $status;
    public ?array $details;
    
    public function __construct(
        string $message,
        ?string $errorCode = null,
        ?int $status = null,
        ?array $details = null,
        ?\Throwable $previous = null
    ) {
        parent::__construct($message, 0, $previous);
        $this->errorCode = $errorCode;
        $this->status = $status;
        $this->details = $details;
    }
    
    public function getErrorCode(): ?string
    {
        return $this->errorCode;
    }
}

class UpayAuthenticationError extends UpayError
{
    public function __construct(string $message = 'Falha na autenticação. Verifique sua API key.', ?\Throwable $previous = null)
    {
        parent::__construct($message, 'AUTHENTICATION_ERROR', 401, null, $previous);
    }
}

class UpayValidationError extends UpayError
{
    public function __construct(string $message, ?array $details = null, ?\Throwable $previous = null)
    {
        parent::__construct($message, 'VALIDATION_ERROR', 400, $details, $previous);
    }
}

class UpayNotFoundError extends UpayError
{
    public function __construct(string $message, ?string $resourceId = null, ?\Throwable $previous = null)
    {
        $finalMessage = $resourceId 
            ? "{$message} (ID: {$resourceId})"
            : $message;
        parent::__construct($finalMessage, 'NOT_FOUND', 404, null, $previous);
    }
}

class UpayRateLimitError extends UpayError
{
    public function __construct(string $message = 'Limite de requisições excedido. Tente novamente mais tarde.', ?\Throwable $previous = null)
    {
        parent::__construct($message, 'RATE_LIMIT_ERROR', 429, null, $previous);
    }
}

class UpayServerError extends UpayError
{
    public function __construct(string $message = 'Erro interno do servidor. Tente novamente mais tarde.', ?\Throwable $previous = null)
    {
        parent::__construct($message, 'SERVER_ERROR', 500, null, $previous);
    }
}
