/**
 * Utilitários para verificação de webhooks
 */

// Detecta ambiente Node.js de forma robusta
const isNode =
  typeof process !== 'undefined' &&
  !!(process as any).versions &&
  !!(process as any).versions.node;

// Função para obter implementação de crypto de forma bundler-friendly
// Tenta usar Web Crypto API primeiro, depois fallback para Node crypto
function getCrypto(): typeof import('crypto') | null {
  // Tentar Web Crypto API primeiro (browser ou Node 19+)
  // Nota: Web Crypto API não suporta HMAC SHA256 diretamente de forma simples,
  // então precisamos usar Node crypto para HMAC
  
  // Node.js - usar require dinâmico dentro da função
  // Bundlers modernos podem tree-shake isso se não for usado em builds browser
  if (isNode) {
    try {
      // eslint-disable-next-line @typescript-eslint/no-require-imports
      return require('crypto');
    } catch {
      return null;
    }
  }
  
  return null;
}

/**
 * Verifica a assinatura de um webhook usando HMAC SHA256
 * 
 * @param payload - Corpo da requisição (string ou Buffer)
 * @param signature - Assinatura recebida no header
 * @param secret - Secret da API key
 * @returns true se a assinatura for válida
 */
export function verifyWebhookSignature(
  payload: string | Buffer,
  signature: string,
  secret: string
): boolean {
  if (!payload || !signature || !secret) {
    return false;
  }

  // Validação de formato hex da assinatura
  // SHA256 produz hash de 32 bytes = 64 caracteres hex
  const hexRegex = /^[0-9a-fA-F]+$/;
  if (!hexRegex.test(signature) || signature.length !== 64) {
    return false;
  }

  const cryptoImpl = getCrypto();
  
  // Se estiver em Node.js, usa crypto do Node
  if (isNode && cryptoImpl) {
    try {
      const hash = cryptoImpl
        .createHmac('sha256', secret)
        .update(payload)
        .digest('hex');

      // Comparação segura para prevenir timing attacks
      const hashBuffer = Buffer.from(hash, 'hex');
      const signatureBuffer = Buffer.from(signature, 'hex');

      // Verificar comprimentos antes de comparar
      if (hashBuffer.length !== signatureBuffer.length) {
        return false;
      }

      return cryptoImpl.timingSafeEqual(hashBuffer, signatureBuffer);
    } catch (error) {
      // Sanitizar log de erro - não expor detalhes sensíveis ou stack traces
      console.error('Error verifying webhook signature');
      return false;
    }
  }

  // Para browser, usa Web Crypto API
  // Nota: Web Crypto API não suporta HMAC diretamente de forma simples
  // Recomenda-se usar uma biblioteca como crypto-js ou fazer no backend
  console.warn('Verificação de webhook no browser não é recomendada. Use no backend.');
  return false;
}

/**
 * Extrai a assinatura do header da requisição
 * 
 * @param headers - Headers da requisição
 * @returns A assinatura ou null se não encontrada
 */
export function extractWebhookSignature(headers: Record<string, string | string[] | undefined>): string | null {
  // Tenta diferentes formatos de header
  const signatureHeader = 
    headers['x-upay-signature'] ||
    headers['x-upay-signature-256'] ||
    headers['upay-signature'] ||
    headers['signature'];

  if (!signatureHeader) {
    return null;
  }

  // Se for array, pega o primeiro
  if (Array.isArray(signatureHeader)) {
    return signatureHeader[0] || null;
  }

  // Remove prefixo "sha256=" se existir
  return signatureHeader.replace(/^sha256=/, '');
}

/**
 * Tipos de eventos de webhook
 */
export enum WebhookEventType {
  TRANSACTION_CREATED = 'transaction.created',
  TRANSACTION_PAID = 'transaction.paid',
  TRANSACTION_FAILED = 'transaction.failed',
  TRANSACTION_CANCELLED = 'transaction.cancelled',
  TRANSACTION_REFUNDED = 'transaction.refunded',
  PAYMENT_LINK_CREATED = 'payment_link.created',
  PAYMENT_LINK_UPDATED = 'payment_link.updated',
  PAYMENT_LINK_DELETED = 'payment_link.deleted',
}
