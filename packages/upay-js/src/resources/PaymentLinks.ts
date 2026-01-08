/**
 * Recurso de Payment Links
 */

import { HttpClient } from '../utils/http';
import type {
  CreatePaymentLinkRequest,
  UpdatePaymentLinkRequest,
  PaymentLink,
  PaginationParams,
  PaginatedResponse,
} from '../types';

export class PaymentLinksResource {
  constructor(private http: HttpClient) {}

  /**
   * Mapeia a resposta da API para um PaymentLink
   * @private
   */
  private mapPaymentLinkResponse(response: any): PaymentLink {
    return response.paymentLink || response.data || response;
  }

  /**
   * Cria um novo link de pagamento
   */
  async create(data: CreatePaymentLinkRequest): Promise<PaymentLink> {
    // Validação básica
    if (!data.title || data.title.trim().length < 3) {
      throw new Error('Título deve ter pelo menos 3 caracteres');
    }

    if (!data.amount && (!data.products || data.products.length === 0)) {
      throw new Error('É necessário fornecer amount ou products');
    }

    if (data.amount && data.amount < 100) {
      throw new Error('Valor mínimo é R$ 1,00 (100 centavos)');
    }

    const response = await this.http.post<any>('/payment-links', {
      title: data.title,
      description: data.description,
      amountCents: data.amount,
      products: data.products,
      currency: data.currency || 'BRL',
      expiresAt: data.expiresAt,
      redirectUrl: data.redirectUrl,
      settings: data.settings,
      status: data.status || 'ACTIVE',
      metaPixelCode: data.metaPixelCode,
      stockQuantity: data.stockQuantity,
      stockEnabled: data.stockEnabled,
    });
    
    return this.mapPaymentLinkResponse(response);
  }

  /**
   * Lista links de pagamento
   */
  async list(params?: PaginationParams & { status?: string }): Promise<PaginatedResponse<PaymentLink>> {
    const response = await this.http.get<any>('/payment-links', {
      page: params?.page,
      limit: params?.limit,
      cursor: params?.cursor,
      orderBy: params?.orderBy,
      orderDirection: params?.orderDirection,
      status: params?.status,
    });
    
    // Mapear resposta da API: { message, paymentLinks, pagination } -> { data, pagination }
    return {
      data: response.paymentLinks || response.data || [],
      pagination: response.pagination || { total: 0, page: 1, limit: 10 }
    };
  }

  /**
   * Obtém um link de pagamento por ID
   */
  async get(id: string): Promise<PaymentLink> {
    if (!id) {
      throw new Error('ID é obrigatório');
    }
    const response = await this.http.get<any>(`/payment-links/${id}`);
    return this.mapPaymentLinkResponse(response);
  }

  /**
   * Obtém um link de pagamento por slug
   */
  async getBySlug(slug: string): Promise<PaymentLink> {
    if (!slug) {
      throw new Error('Slug é obrigatório');
    }
    const response = await this.http.get<any>(`/payment-links/slug/${slug}`);
    return this.mapPaymentLinkResponse(response);
  }

  /**
   * Atualiza um link de pagamento
   */
  async update(id: string, data: UpdatePaymentLinkRequest): Promise<PaymentLink> {
    if (!id) {
      throw new Error('ID é obrigatório');
    }

    const updateData: any = {};
    if (data.title !== undefined) updateData.title = data.title;
    if (data.description !== undefined) updateData.description = data.description;
    if (data.amount !== undefined) updateData.amountCents = data.amount;
    if (data.status !== undefined) updateData.status = data.status;
    if (data.expiresAt !== undefined) updateData.expiresAt = data.expiresAt;
    if (data.redirectUrl !== undefined) updateData.redirectUrl = data.redirectUrl;
    if (data.settings !== undefined) updateData.settings = data.settings;

    const response = await this.http.patch<any>(`/payment-links/${id}`, updateData);
    return this.mapPaymentLinkResponse(response);
  }

  /**
   * Deleta um link de pagamento
   */
  async delete(id: string): Promise<void> {
    if (!id) {
      throw new Error('ID é obrigatório');
    }
    await this.http.delete(`/payment-links/${id}`);
  }

  /**
   * Obtém a URL pública do checkout
   */
  getCheckoutUrl(slug: string, baseUrl?: string): string {
    // Validar slug
    if (!slug || typeof slug !== 'string' || slug.trim().length === 0) {
      throw new Error('Slug é obrigatório e deve ser uma string não vazia');
    }

    // Normalizar baseUrl (remover trailing slash se existir)
    const normalizedBaseUrl = (baseUrl || 'https://checkout.upaybr.com').trim().replace(/\/+$/, '');
    
    // Normalizar slug (remover leading slashes, trim, e URL-encode)
    const normalizedSlug = slug.trim().replace(/^\/+/, '');
    const encodedSlug = encodeURIComponent(normalizedSlug);
    
    return `${normalizedBaseUrl}/${encodedSlug}`;
  }
}
