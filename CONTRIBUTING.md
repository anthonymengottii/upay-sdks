# Guia de Contribuição

Obrigado por considerar contribuir com os SDKs da Upay! 🎉

## Como Contribuir

### Reportando Bugs

Se você encontrou um bug:

1. Verifique se já existe uma issue sobre o problema
2. Se não existir, crie uma nova issue com:
   - Descrição clara do problema
   - Passos para reproduzir
   - Comportamento esperado vs. comportamento atual
   - Versão do SDK e ambiente (Node.js, Python, etc.)

### Sugerindo Melhorias

1. Abra uma issue com a tag `enhancement`
2. Descreva a melhoria proposta
3. Explique por que seria útil

### Enviando Pull Requests

1. Faça um fork do repositório
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Faça suas alterações
4. Adicione testes se aplicável
5. Certifique-se de que todos os testes passam
6. Commit suas mudanças (`git commit -m 'Adiciona nova feature'`)
7. Push para a branch (`git push origin feature/nova-feature`)
8. Abra um Pull Request

## Padrões de Código

### JavaScript/TypeScript

- Use TypeScript para novos códigos
- Siga as convenções do ESLint configurado
- Adicione tipos TypeScript apropriados
- Escreva testes para novas funcionalidades

### Commits

Use mensagens de commit descritivas:

- `feat: adiciona suporte a webhooks`
- `fix: corrige erro na validação de cupons`
- `docs: atualiza documentação do SDK`
- `test: adiciona testes para payment links`

## Estrutura do Projeto

```
upay-sdks/
├── packages/
│   ├── upay-js/          # SDK JavaScript/TypeScript
│   ├── upay-python/      # SDK Python (futuro)
│   └── ...
├── examples/             # Exemplos de uso
├── docs/                 # Documentação adicional
└── README.md
```

## Testes

Antes de enviar um PR, certifique-se de:

- [ ] Todos os testes passam
- [ ] Código segue os padrões do projeto
- [ ] Documentação foi atualizada se necessário

## Dúvidas?

Se tiver dúvidas, abra uma issue ou entre em contato!
