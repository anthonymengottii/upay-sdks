/**
 * Tipos TypeScript para o SDK Upay
 */

export interface UpayConfig {
  apiKey: string;
  baseUrl?: string;
  version?: string;
  timeout?: number;
}

export interface PaymentLinkSettings {
  pixEnabled?: boolean;
  boletoEnabled?: boolean;
  creditCardEnabled?: boolean;
  maxInstallments?: number;
  interestFreeInstallments?: number;
  interestRate?: number;
  requirePhone?: boolean;
  requireAddress?: boolean;
}

interface BaseCreatePaymentLinkRequest {
  title: string;
  description?: string;
  currency?: string;
  expiresAt?: Date | string;
  redirectUrl?: string;
  settings?: PaymentLinkSettings;
  status?: 'ACTIVE' | 'INACTIVE';
  metaPixelCode?: string;
  stockQuantity?: number;
  stockEnabled?: boolean;
}

export interface CreatePaymentLinkWithAmountRequest extends BaseCreatePaymentLinkRequest {
  /** Amount in cents (integer) */
  amount: number;
  products?: never;
}

export interface CreatePaymentLinkWithProductsRequest extends BaseCreatePaymentLinkRequest {
  amount?: number;
  products: Array<{
    productId: string;
    quantity: number;
  }>;
}

export type CreatePaymentLinkRequest =
  | CreatePaymentLinkWithAmountRequest
  | CreatePaymentLinkWithProductsRequest;

export interface UpdatePaymentLinkRequest {
  title?: string;
  description?: string;
  /** Amount in cents (integer) */
  amount?: number;
  status?: 'ACTIVE' | 'INACTIVE';
  expiresAt?: Date | string;
  redirectUrl?: string;
  settings?: PaymentLinkSettings;
}

export interface PaymentLink {
  id: string;
  slug: string;
  title: string;
  description?: string;
  /** Amount in cents (integer). This is the canonical field for monetary values. */
  amountCents: number;
  /** Amount in the currency unit (floating-point). Deprecated: use amountCents instead. */
  amount: number;
  currency: string;
  status: 'ACTIVE' | 'INACTIVE';
  expiresAt?: string;
  redirectUrl?: string;
  createdAt: string;
  updatedAt: string;
  settings?: PaymentLinkSettings;
  products?: Array<{
    productId: string;
    quantity: number;
    product: {
      id: string;
      name: string;
      priceCents: number;
    };
  }>;
}

/** Payment method types supported by the API */
export type PaymentMethod = 'PIX' | 'CREDIT_CARD' | 'BOLETO';

export interface CreateTransactionRequest {
  product: string;
  amountCents: number;
  paymentMethod?: PaymentMethod;
  clientId?: string;
  client?: {
    name: string;
    email: string;
    document?: string;
    phone?: string;
  };
  paymentLinkId?: string;
  metadata?: Record<string, any>;
  couponCode?: string;
}

export interface Transaction {
  id: string;
  displayId?: string;
  product: string;
  amountCents: number;
  status: 'PENDING' | 'PAID' | 'FAILED' | 'CANCELLED' | 'REFUNDED';
  paymentMethod: PaymentMethod;
  client?: {
    id: string;
    name: string;
    email: string;
    document?: string;
    phone?: string;
  };
  paymentLink?: {
    id: string;
    title: string;
    slug: string;
  };
  createdAt: string;
  updatedAt: string;
  paidAt?: string;
  pixQrCode?: string;
  pixCopyPaste?: string;
  boletoBarcode?: string;
  boletoUrl?: string;
  metadata?: Record<string, any>;
}

export interface CreateProductRequest {
  name: string;
  description?: string;
  priceCents: number;
  stock?: number;
  imageUrl?: string;
  sku?: string;
  category?: string;
}

export interface Product {
  id: string;
  name: string;
  description?: string;
  priceCents: number;
  stock?: number;
  imageUrl?: string;
  sku?: string;
  category?: string;
  createdAt: string;
  updatedAt: string;
}

export interface ValidateCouponRequest {
  code: string;
  amountCents: number;
  paymentLinkId?: string;
  productIds?: string[]; // Product IDs to resolve coupon ambiguity
}

export interface CouponValidation {
  valid: boolean;
  discountCents?: number;
  discountPercentage?: number;
  finalAmountCents?: number;
  message?: string;
}

export interface PaginationParams {
  page?: number;
  limit?: number;
  cursor?: string;
  orderBy?: string;
  orderDirection?: 'asc' | 'desc';
}

export interface PaginatedResponse<T> {
  data: T[];
  pagination: {
    page?: number;
    limit?: number;
    total?: number;
    totalPages?: number;
    hasNext?: boolean;
    hasPrevious?: boolean;
    cursor?: string;
    nextCursor?: string;
  };
}

export interface ApiError {
  message: string;
  code?: string;
  status?: number;
  details?: any;
}

export interface WebhookEvent {
  id: string;
  type: string;
  data: any;
  createdAt: string;
}

export interface Client {
  id: string;
  name: string;
  email: string;
  document?: string;
  phone?: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateClientRequest {
  name: string;
  email: string;
  document?: string;
  phone?: string;
}

/**
 * Dados do cartão de crédito para processamento de pagamento
 */
export interface CardData {
  /** Número do cartão (sem espaços ou traços) */
  number: string;
  /** Nome do portador do cartão */
  holderName?: string;
  /** Mês de expiração (1-12 ou string "01"-"12") */
  expiryMonth: string | number;
  /** Ano de expiração (4 dígitos ou string "2024") */
  expiryYear: string | number;
  /** Código de segurança (CVV) */
  cvv: string;
  /** Bandeira do cartão (opcional, pode ser detectada automaticamente) */
  brand?: string;
}
