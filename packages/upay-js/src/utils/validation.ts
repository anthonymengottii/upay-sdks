/**
 * Utilitários de validação
 */

/**
 * Valida se um valor é um número positivo
 */
export function validatePositiveNumber(value: number, fieldName: string): void {
  if (
    typeof value !== 'number' ||
    !Number.isFinite(value) ||
    value <= 0
  ) {
    throw new Error(`${fieldName} deve ser um número positivo`);
  }
}

/**
 * Valida se um valor está em centavos (inteiro)
 */
export function validateCents(value: number, fieldName: string): void {
  if (!Number.isInteger(value) || value <= 0) {
    throw new Error(`${fieldName} deve ser um número inteiro positivo em centavos`);
  }
}

/**
 * Valida formato de email
 */
export function validateEmail(email: string): boolean {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
}

/**
 * Valida formato de CPF/CNPJ (apenas formato básico)
 */
export function validateDocument(document: string): boolean {
  // Remove caracteres não numéricos
  const cleanDoc = document.replace(/\D/g, '');
  // CPF tem 11 dígitos, CNPJ tem 14
  return cleanDoc.length === 11 || cleanDoc.length === 14;
}

/**
 * Valida formato de UUID
 */
export function validateUUID(uuid: string): boolean {
  const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
  return uuidRegex.test(uuid);
}

/**
 * Valida URL
 */
export function validateURL(url: string): boolean {
  try {
    new URL(url);
    return true;
  } catch {
    return false;
  }
}

/**
 * Valida data de expiração (deve ser futura)
 */
export function validateExpirationDate(date: Date | string): boolean {
  const expirationDate = typeof date === 'string' ? new Date(date) : date;

  // Verifica se a data é válida
  if (Number.isNaN(expirationDate.getTime())) {
    return false;
  }

  return expirationDate > new Date();
}
