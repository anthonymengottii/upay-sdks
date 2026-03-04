# 🧪 Como Testar o SDK

## Pré-requisitos

1. Ter uma API Key da Upay
2. Node.js instalado (versão 18 LTS ou superior)
## Passo 1: Configurar API Key

### Windows (PowerShell):
```powershell
$env:UPAY_API_KEY="sua_api_key_aqui"
```

### Linux/Mac:
```bash
export UPAY_API_KEY="sua_api_key_aqui"
```

## Passo 2: Executar Testes

### Opção 1: Usando ts-node (recomendado para desenvolvimento)

```bash
npm install -g ts-node
npx ts-node test.ts
```

### Opção 2: Compilar e executar

```bash
npm run build
node dist/test.js
```

## O que o teste faz?

O script de teste (`test.ts`) executa os seguintes testes:

1. ✅ **Listar Payment Links** - Testa leitura de links de pagamento
2. ✅ **Listar Transações** - Testa leitura de transações
3. ✅ **Listar Produtos** - Testa leitura de produtos
4. ✅ **Listar Clientes** - Testa leitura de clientes
5. ✅ **Validar Cupom** - Testa validação de cupom (endpoint público)
6. ✅ **Criar Payment Link** - Testa criação de link (e depois deleta)

## Exemplo de Saída Esperada

```
🧪 Testando SDK Upay...

📝 Configuração:
   Base URL: https://upay-sistema-api.onrender.com
   Version: v1
   API Key: abc12345...

📋 Teste 1: Listar Payment Links...
✅ Sucesso! Encontrados 5 links
   Primeiro link: Produto Premium (produto-premium)

💳 Teste 2: Listar Transações...
✅ Sucesso! Encontradas 10 transações
   Primeira transação: Produto Teste - R$ 50.00

...

✅ Testes concluídos!
```

## Troubleshooting

### Erro: "API key inválida"
- Verifique se a API key está correta
- Certifique-se de que a API key está ativa no dashboard

### Erro: "Timeout"
- Verifique sua conexão com a internet
- A API pode estar temporariamente indisponível

### Erro: "Permissão negada"
- Verifique se sua API key tem as permissões necessárias:
  - `paymentLinks:read`
  - `transactions:read`
  - `products:read`
  - `clients:read`
  - `paymentLinks:write` (para criar links)

## Próximos Passos

Após os testes básicos passarem, você pode:

1. Integrar o SDK no seu projeto
2. Criar seus próprios scripts de teste
3. Ver os exemplos em `examples/`
