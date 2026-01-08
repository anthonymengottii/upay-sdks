/**
 * Exemplo: Processar webhooks
 */

import express from 'express';
import UpayClient from '../src/index';

const app = express();
const upay = new UpayClient({
  apiKey: process.env.UPAY_API_KEY || 'sua_api_key_aqui',
});

// Middleware para capturar o body raw (necessário para verificar assinatura)
app.use('/webhook', express.raw({ type: 'application/json' }));

app.post('/webhook', (req, res) => {
  try {
    // 1. Extrair assinatura do header
    const signature = upay.extractWebhookSignature(req.headers);
    
    if (!signature) {
      console.warn('⚠️ Assinatura não encontrada no header');
      return res.status(401).json({ error: 'Assinatura não encontrada' });
    }

    // 2. Verificar assinatura
    const secret = process.env.WEBHOOK_SECRET || 'seu_webhook_secret';
    const isValid = upay.verifyWebhookSignature(req.body, signature, secret);

    if (!isValid) {
      console.warn('⚠️ Assinatura inválida');
      return res.status(401).json({ error: 'Assinatura inválida' });
    }

    // 3. Parse do evento
    const event = JSON.parse(req.body.toString());
    
    console.log('✅ Webhook recebido:', event.type);
    console.log('Dados:', JSON.stringify(event.data, null, 2));

    // 4. Processar evento
    switch (event.type) {
      case 'transaction.created':
        console.log('📝 Nova transação criada:', event.data.id);
        // Atualizar status no seu sistema
        break;

      case 'transaction.paid':
        console.log('💰 Transação paga:', event.data.id);
        // Liberar produto/serviço
        // Enviar email de confirmação
        break;

      case 'transaction.failed':
        console.log('❌ Transação falhou:', event.data.id);
        // Notificar cliente
        break;

      case 'transaction.cancelled':
        console.log('🚫 Transação cancelada:', event.data.id);
        // Reverter estoque
        break;

      case 'transaction.refunded':
        console.log('↩️ Transação estornada:', event.data.id);
        // Reverter produto/serviço
        break;

      case 'payment_link.created':
        console.log('🔗 Link de pagamento criado:', event.data.id);
        break;

      case 'payment_link.updated':
        console.log('✏️ Link de pagamento atualizado:', event.data.id);
        break;

      default:
        console.log('ℹ️ Evento desconhecido:', event.type);
    }

    // 5. Responder com sucesso
    res.json({ received: true });

  } catch (error: any) {
    console.error('❌ Erro ao processar webhook:', error);
    res.status(500).json({ error: 'Erro ao processar webhook' });
  }
});

// Iniciar servidor apenas quando executado diretamente
// Verifica se o arquivo está sendo executado diretamente (não importado)
// Usa uma verificação que funciona tanto em CommonJS quanto ESM
const isMainModule = (() => {
  // CommonJS
  if (typeof require !== 'undefined' && require.main === module) {
    return true;
  }
  // ESM: verifica se o arquivo sendo executado corresponde a este módulo
  if (typeof import.meta !== 'undefined' && import.meta.url && typeof process !== 'undefined' && process.argv[1]) {
    const currentFile = import.meta.url.replace(/^file:\/\//, '').replace(/\\/g, '/');
    const executedFile = process.argv[1].replace(/\\/g, '/');
    return currentFile === executedFile || executedFile.includes('webhook');
  }
  return false;
})();

if (isMainModule) {
  const PORT = process.env.PORT || 3000;
  app.listen(PORT, () => {
    console.log(`🚀 Servidor de webhooks rodando na porta ${PORT}`);
    console.log(`📡 Endpoint: http://localhost:${PORT}/webhook`);
  });
}

export default app;
