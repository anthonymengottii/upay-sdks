# Upay Java SDK

SDK oficial da Upay para Java - integração fácil com a API de pagamentos Upay.

## 📦 Requisitos

- Java 17+
- Maven 3.8+
- Acesso à API Upay e uma API Key válida

## 🚀 Instalação (projeto Maven)

No seu pom.xml adicione o repositório/artifact assim que o SDK estiver publicado. Por enquanto, use como módulo local ou include direto no seu projeto.

Para rodar localmente dentro deste repositório:

`ash
cd sdks/packages/upay-java
mvn compile
`

## 🔑 Configuração básica

`java
import com.upay.sdk.UpayClient;

String apiKey = "SUA_API_KEY_AQUI";
String baseUrl = "https://api.upay-sistema.onrender.com"; // ou http://localhost:3001 em dev

UpayClient upay = new UpayClient(apiKey, baseUrl, "v1", 30);
`

## 💳 Payment Links

### Criar Payment Link

`java
import com.fasterxml.jackson.databind.JsonNode;

Map<String, Object> options = new HashMap<>();
options.put("description", "Produto Premium");
options.put("status", "ACTIVE");

JsonNode link = upay.paymentLinks.create(
    "Produto Premium",
    10000, // R$ 100,00 em centavos
    options
);

System.out.println("ID: " + link.get("id").asText());
System.out.println("Slug: " + link.get("slug").asText());
`

### Listar Payment Links

`java
JsonNode links = upay.paymentLinks.list(1, 10);
System.out.println(links.toPrettyString());
`

## 📦 Produtos

`java
JsonNode products = upay.products.list(1, 10);
System.out.println(products.toPrettyString());
`

## 💳 Transações

(Exposição de criação/processamento via API pública pode ser ajustada depois; por enquanto, foque em leitura/listagem)

`java
JsonNode transactions = upay.transactions.list(1, 10);
System.out.println(transactions.toPrettyString());
`

## 🎫 Cupons

`java
JsonNode validation = upay.coupons.validate("CUPOM10", 10000);
System.out.println(validation.toPrettyString());
`

## 🌐 Webhooks

`java
import com.upay.sdk.utils.WebhookUtils;

String payload = requestBody; // JSON recebido
String signature = request.getHeader("X-Upay-Signature");
String secret = "SEU_WEBHOOK_SECRET";

boolean valid = WebhookUtils.verifySignature(payload, signature, secret);
if (!valid) {
    response.setStatus(401);
    return;
}

// processar evento
`

## 🧪 Exemplo pronto (Main)

Já existe um exemplo em src/main/java/com/upay/sdk/examples/Main.java que:

- Usa sua API Key de teste
- Aponta para http://localhost:3001
- Lista Payment Links e Produtos

Para executar:

`ash
cd sdks/packages/upay-java
mvn compile
mvn exec:java
`

## 📝 Tratamento de erros

Todos os erros HTTP são encapsulados em UpayException:

`java
import com.upay.sdk.utils.UpayException;

try {
    JsonNode links = upay.paymentLinks.list(1, 10);
} catch (UpayException e) {
    System.err.println("Erro na API Upay: " + e.getMessage());
    System.err.println("Status: " + e.getStatus());
    System.err.println("Code: " + e.getCode());
}
`

## 📄 Licença

MIT
