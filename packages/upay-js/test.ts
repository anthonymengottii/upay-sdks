/**
 * Script de teste do SDK Upay
 * 
 * Para executar:
 * npx ts-node test.ts
 * 
 * Ou após compilar:
 * node dist/test.js
 */

import UpayClient from './src/index';
import type { UpayConfig } from './src/types';

async function testSDK() {
  console.log('🧪 Testando SDK Upay...\n');

  // Verificar se a API key foi fornecida
  const apiKey = process.env.UPAY_API_KEY;
  
  if (!apiKey) {
    console.error('❌ Erro: UPAY_API_KEY não encontrada nas variáveis de ambiente');
    console.log('\n💡 Para testar, defina a variável de ambiente:');
    console.log('   Windows: $env:UPAY_API_KEY="sua_api_key_aqui"');
    console.log('   Linux/Mac: export UPAY_API_KEY="sua_api_key_aqui"');
    console.log('\n   Ou edite este arquivo e adicione sua API key diretamente.');
    return;
  }

  // Inicializar cliente
  const config: UpayConfig = {
    apiKey: apiKey,
    baseUrl: process.env.UPAY_BASE_URL || 'https://upay-sistema-api.onrender.com',
    version: 'v1',
    timeout: 30000,
  };

  console.log('📝 Configuração:');
  console.log(`   Base URL: ${config.baseUrl}`);
  console.log(`   Version: ${config.version}`);
  console.log('   API Key: [API KEY HIDDEN]\n');

  const upay = new UpayClient(config);

  try {
    // Teste 1: Listar Payment Links
    console.log('📋 Teste 1: Listar Payment Links...');
    try {
      const { data: links, pagination } = await upay.paymentLinks.list({
        page: 1,
        limit: 5,
      });
      console.log(`✅ Sucesso! Encontrados ${pagination.total || links.length} links`);
      if (links.length > 0) {
        console.log(`   Primeiro link: ${links[0].title} (${links[0].slug})`);
      }
    } catch (error: any) {
      console.log(`⚠️  Erro: ${error.message}`);
    }

    // Teste 2: Listar Transações
    console.log('\n💳 Teste 2: Listar Transações...');
    try {
      const { data: transactions, pagination } = await upay.transactions.list({
        page: 1,
        limit: 5,
      });
      console.log(`✅ Sucesso! Encontradas ${pagination.total || transactions.length} transações`);
      if (transactions.length > 0) {
        const tx = transactions[0];
        const amount = Number.isFinite(tx?.amountCents) ? (tx.amountCents / 100).toFixed(2) : 'N/A';
        console.log(`   Primeira transação: ${tx.product} - R$ ${amount}`);
      }
    } catch (error: any) {
      console.log(`⚠️  Erro: ${error.message}`);
    }

    // Teste 3: Listar Produtos
    console.log('\n📦 Teste 3: Listar Produtos...');
    try {
      const { data: products, pagination } = await upay.products.list({
        page: 1,
        limit: 5,
      });
      console.log(`✅ Sucesso! Encontrados ${pagination.total || products.length} produtos`);
      if (products.length > 0) {
        const product = products[0];
        const price = (product && Number.isFinite(product.price))
          ? (product.price / 100).toFixed(2)
          : 'N/A';
        console.log(`   Primeiro produto: ${product.name} - R$ ${price}`);
      }
    } catch (error: any) {
      console.log(`⚠️  Erro: ${error.message}`);
    }

    // Teste 4: Listar Clientes
    console.log('\n👥 Teste 4: Listar Clientes...');
    try {
      const { data: clients, pagination } = await upay.clients.list({
        page: 1,
        limit: 5,
      });
      console.log(`✅ Sucesso! Encontrados ${pagination.total || clients.length} clientes`);
      if (clients.length > 0) {
        console.log(`   Primeiro cliente: ${clients[0].name} (${clients[0].email})`);
      }
    } catch (error: any) {
      console.log(`⚠️  Erro: ${error.message}`);
    }

    // Teste 5: Validar Cupom (endpoint público)
    console.log('\n🎟️  Teste 5: Validar Cupom...');
    try {
      // Este teste pode falhar se não houver cupom válido, mas testa a conexão
      const validation = await upay.coupons.validate({
        code: 'TESTE123',
        amountCents: 10000,
      });
      if (validation.valid) {
        const discountCents = typeof validation.discountCents === 'number'
          ? validation.discountCents
          : 0;

        if (typeof validation.discountCents !== 'number') {
          console.warn('⚠️  Cupom marcado como válido, mas discountCents não veio numérico. Usando 0 como fallback.');
        }

        console.log(`✅ Cupom válido! Desconto: R$ ${(discountCents / 100).toFixed(2)}`);
      } else {
        console.log(`ℹ️  Cupom inválido (esperado para código de teste): ${validation.message}`);
      }
    } catch (error: any) {
      console.log(`⚠️  Erro: ${error.message}`);
    }

    // Teste 6: Criar Payment Link (teste de escrita)
    console.log('\n📝 Teste 6: Criar Payment Link...');
    try {
      const testLink = await upay.paymentLinks.create({
        title: `Teste SDK - ${new Date().toISOString()}`,
        description: 'Link criado pelo teste do SDK',
        amount: 1000, // R$ 10,00
        currency: 'BRL',
        settings: {
          pixEnabled: true,
          creditCardEnabled: true,
        },
      });
      console.log(`✅ Link criado com sucesso!`);
      console.log(`   ID: ${testLink.id}`);
      console.log(`   Slug: ${testLink.slug}`);
      console.log(`   URL: ${upay.paymentLinks.getCheckoutUrl(testLink.slug)}`);
      
      // Tentar deletar o link de teste
      try {
        await upay.paymentLinks.delete(testLink.id);
        console.log(`   🗑️  Link de teste deletado`);
      } catch (deleteError: any) {
        console.log(`   ⚠️  Não foi possível deletar o link: ${deleteError.message}`);
      }
    } catch (error: any) {
      console.log(`⚠️  Erro: ${error.message}`);
      if (error.details) {
        console.log(`   Detalhes:`, error.details);
      }
    }

    console.log('\n✅ Testes concluídos!');

  } catch (error: any) {
    console.error('\n❌ Erro geral:', error.message);
    if (error.stack) {
      console.error('Stack:', error.stack);
    }
  }
}

// Executar testes
testSDK().catch(console.error);
