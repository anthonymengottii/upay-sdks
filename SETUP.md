# Instruções de Setup do Repositório

## Passo 1: Clonar o repositório vazio

```bash
git clone https://github.com/anthonymengottii/upay-sdks.git
cd upay-sdks
```

## Passo 2: Copiar arquivos do SDK atual

Copie a pasta `upay-js` para `packages/upay-js`:

```bash
# No Windows PowerShell
mkdir packages
xcopy /E /I upay-js packages\upay-js

# No Linux/Mac
mkdir -p packages
cp -r upay-js packages/upay-js
```

## Passo 3: Remover arquivos temporários

Remova os seguintes arquivos/pastas antes de fazer commit:

```bash
# Remover node_modules
Remove-Item -Recurse -Force packages\upay-js\node_modules

# Remover dist (será gerado no build)
Remove-Item -Recurse -Force packages\upay-js\dist

# Remover arquivos de teste com API key
Remove-Item packages\upay-js\test-with-key.ts -ErrorAction SilentlyContinue
```

## Passo 4: Primeiro commit

```bash
git add .
git commit -m "feat: adiciona SDK JavaScript/TypeScript inicial"
git push -u origin main
```

## Estrutura Final

```
upay-sdks/
├── README.md
├── LICENSE
├── CONTRIBUTING.md
├── .gitignore
└── packages/
    ├── upay-js/
    │   ├── src/
    │   ├── package.json
    │   ├── tsconfig.json
    │   └── README.md
    ├── upay-java/
    │   ├── src/
    │   ├── pom.xml
    │   └── README.md
    └── upay-python/
        ├── upay/
        ├── setup.py
        ├── requirements.txt
        └── README.md
```
