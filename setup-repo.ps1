# Script de setup do repositório upay-sdks
# Execute este script na pasta sdks/

Write-Host "🚀 Configurando repositório upay-sdks..." -ForegroundColor Cyan

# Criar pasta packages se não existir
if (!(Test-Path "packages")) {
    New-Item -ItemType Directory -Path "packages" | Out-Null
    Write-Host "✅ Pasta packages criada" -ForegroundColor Green
}

# Copiar upay-js para packages/upay-js
if (Test-Path "upay-js") {
    if (Test-Path "packages\upay-js") {
        Write-Host "⚠️  packages\upay-js já existe. Removendo..." -ForegroundColor Yellow
        Remove-Item -Recurse -Force "packages\upay-js"
    }
    
    Write-Host "📦 Copiando upay-js para packages..." -ForegroundColor Cyan
    Copy-Item -Path "upay-js" -Destination "packages\upay-js" -Recurse -Force
    Write-Host "✅ SDK copiado com sucesso" -ForegroundColor Green
} else {
    Write-Host "❌ Pasta upay-js não encontrada!" -ForegroundColor Red
    exit 1
}

# Remover arquivos que não devem ir para o repositório
Write-Host "🧹 Limpando arquivos temporários..." -ForegroundColor Cyan

$filesToRemove = @(
    "packages\upay-js\node_modules",
    "packages\upay-js\dist",
    "packages\upay-js\test-with-key.ts",
    "packages\upay-js\packages"
)

foreach ($item in $filesToRemove) {
    if (Test-Path $item) {
        if ((Get-Item $item).PSIsContainer) {
            Remove-Item -Recurse -Force $item -ErrorAction SilentlyContinue
        } else {
            Remove-Item -Force $item -ErrorAction SilentlyContinue
        }
        Write-Host "   Removido: $item" -ForegroundColor Gray
    }
}

Write-Host "✅ Limpeza concluída" -ForegroundColor Green
Write-Host ""
Write-Host "📝 Próximos passos:" -ForegroundColor Cyan
Write-Host "   1. cd packages\upay-js" -ForegroundColor White
Write-Host "   2. npm install" -ForegroundColor White
Write-Host "   3. npm run build" -ForegroundColor White
Write-Host "   4. Volte para a raiz do repositório" -ForegroundColor White
Write-Host "   5. git add ." -ForegroundColor White
Write-Host "   6. git commit -m 'feat: adiciona SDK JavaScript/TypeScript'" -ForegroundColor White
Write-Host "   7. git push -u origin main" -ForegroundColor White
