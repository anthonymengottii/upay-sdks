/**
 * Recurso de Clientes
 */

import { HttpClient } from '../utils/http';
import { ValidationError } from '../utils/errors';
import type {
  CreateClientRequest,
  Client,
  PaginationParams,
  PaginatedResponse,
} from '../types';

export class ClientsResource {
  constructor(private http: HttpClient) {}

  /**
   * Cria um novo cliente
   */
  async create(data: CreateClientRequest): Promise<Client> {
    // Validar campos obrigatórios
    this.validateName(data.name, true);
    this.validateEmail(data.email, true);

    return this.http.post<Client>('/clients', data);
  }

  /**
   * Lista clientes
   */
  async list(params?: PaginationParams): Promise<PaginatedResponse<Client>> {
    return this.http.get<PaginatedResponse<Client>>('/clients', {
      page: params?.page,
      limit: params?.limit,
      cursor: params?.cursor,
      orderBy: params?.orderBy,
      orderDirection: params?.orderDirection,
    });
  }

  /**
   * Obtém um cliente por ID
   */
  async get(id: string): Promise<Client> {
    if (!id || id.trim().length === 0) {
      throw new ValidationError('ID is required', 'id');
    }
    
    // URL-encode o ID para garantir segurança
    const encodedId = encodeURIComponent(id);
    return this.http.get<Client>(`/clients/${encodedId}`);
  }

  /**
   * Atualiza um cliente
   */
  async update(id: string, data: Partial<CreateClientRequest>): Promise<Client> {
    // Validar ID
    if (!id || id.trim().length === 0) {
      throw new ValidationError('ID is required', 'id');
    }

    // Validar campos apenas se presentes no Partial
    if (data.name !== undefined) {
      this.validateName(data.name, true);
    }

    if (data.email !== undefined) {
      this.validateEmail(data.email, true);
    }

    // URL-encode o ID para garantir segurança
    const encodedId = encodeURIComponent(id);
    return this.http.patch<Client>(`/clients/${encodedId}`, data);
  }

  /**
   * Valida nome do cliente
   */
  private validateName(name: string | undefined, required: boolean = false): void {
    if (required && (!name || name.trim().length === 0)) {
      throw new ValidationError('Client name is required', 'name');
    }
  }

  /**
   * Valida email do cliente
   */
  private validateEmail(email: string | undefined, required: boolean = false): void {
    if (required && !email) {
      throw new ValidationError('Email is required', 'email');
    }

    if (email && !this.isValidEmail(email)) {
      throw new ValidationError('Invalid email format', 'email');
    }
  }

  /**
   * Valida formato de email
   */
  private isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }
}
