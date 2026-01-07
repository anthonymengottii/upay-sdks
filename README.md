<div align="center">
  <img src="https://raw.githubusercontent.com/anthonymengottii/upay-sdks/main/logo/light.png" alt="Upay Logo" width="200"/>
</div>

# Upay SDKs

SDKs oficiais da Upay para facilitar a integração da plataforma de pagamentos em diferentes linguagens de programação.

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![npm version](https://img.shields.io/npm/v/@upay/upay-js.svg)](https://www.npmjs.com/package/@upay/upay-js)

## 📦 SDKs Disponíveis

### ✅ JavaScript/TypeScript
- **Pacote**: `@upay/upay-js`
- **Status**: ✅ Pronto para uso
- **Documentação**: [packages/upay-js/README.md](./packages/upay-js/README.md)
- **Instalação**: `npm install @upay/upay-js`

### ✅ Python
- **Pacote**: `upay-python`
- **Status**: ✅ Pronto para uso
- **Documentação**: [packages/upay-python/README.md](./packages/upay-python/README.md)
- **Instalação**: `pip install upay-python`

### ✅ PHP
- **Pacote**: `upay/upay-php`
- **Status**: ✅ Pronto para uso
- **Documentação**: [packages/upay-php/README.md](./packages/upay-php/README.md)
- **Instalação**: `composer require upay/upay-php`

### 🚧 Em Desenvolvimento
- **Java**: SDK Java para integração com Upay

## 🚀 Início Rápido

### JavaScript/TypeScript

```bash
npm install @upay/upay-js
```

```typescript
import UpayClient from '@upay/upay-js';

const upay = new UpayClient({
  apiKey: 'sua_api_key_aqui',
  baseUrl: 'https://api.upay-sistema.onrender.com', // Opcional
});

// Criar um link de pagamento
const paymentLink = await upay.paymentLinks.create({
  title: 'Produto Premium',
  amount: 10000, // R$ 100,00 em centavos
  description: 'Descrição do produto',
});

console.log(`Link criado: ${upay.paymentLinks.getCheckoutUrl(paymentLink.slug)}`);

// Listar transações
const { data: transactions } = await upay.transactions.list({
  page: 1,
  limit: 10,
});
```

### Python

```bash
pip install upay-python
```

```python
from upay import UpayClient

upay = UpayClient(api_key="sua_api_key_aqui")

payment_link = upay.payment_links.create({
    "title": "Produto Premium",
    "amount": 10000,  # R$ 100,00 em centavos
})
```

### PHP

```bash
composer require upay/upay-php
```

```php
<?php

use Upay\UpayClient;

$upay = new UpayClient(apiKey: "sua_api_key_aqui");

$paymentLink = $upay->paymentLinks->create([
    'title' => 'Produto Premium',
    'amount' => 10000,  // R$ 100,00 em centavos
]);
```

## 📚 Documentação

Cada SDK possui sua própria documentação completa:

- [SDK JavaScript/TypeScript](./packages/upay-js/README.md)
- [SDK Python](./packages/upay-python/README.md)
- [SDK PHP](./packages/upay-php/README.md)

## 🔑 Obter API Key

Para usar os SDKs, você precisa de uma API Key. Obtenha a sua:

1. Acesse o [Dashboard Upay](https://app.upaybr.com)
2. Vá em **Configurações** > **API Keys**
3. Crie uma nova API Key
4. Copie e use no seu código

## 🛠️ Recursos Disponíveis

Todos os SDKs suportam:

- ✅ **Payment Links** - Criar e gerenciar links de pagamento
- ✅ **Transações** - Listar e consultar transações
- ✅ **Produtos** - Gerenciar produtos
- ✅ **Clientes** - Gerenciar clientes
- ✅ **Cupons** - Validar cupons de desconto
- ✅ **Webhooks** - Verificar assinaturas de webhooks

## 📝 Exemplos

Confira a pasta `examples/` em cada SDK para exemplos práticos de uso.

## 🤝 Contribuindo

Contribuições são bem-vindas! Por favor:

1. Faça um fork do repositório
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](./LICENSE) para detalhes.

## 🔗 Links Úteis

- [Documentação da API](https://docs.upaybr.com)
- [Dashboard](https://app.upaybr.com)
- [Suporte](mailto:suporte@upaybr.com)
- [Website](https://upaybr.com)

## 📞 Suporte

Se você encontrar algum problema ou tiver dúvidas:

- Abra uma [Issue](https://github.com/anthonymengottii/upay-sdks/issues)
- Entre em contato: suporte@upaybr.com

---

Feito com ❤️ pela equipe Upay
