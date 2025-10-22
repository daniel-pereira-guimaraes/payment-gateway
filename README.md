# Payment Gateway API

## ✅ Testes Manuais

Mesmo com uma elevada cobertura de testes automatizados, unitários e de integração, 
executei uma bateria de testes manuais cobrindo 52 cenários. 

Nesta etapa foram identificados 5 bugs, todos de validação de dados,
dos quais apenas um era mais relevante. Todos foram corrigidos e os 
respectivos testes foram repetidos, com êxito.

[Planilha de testes manuals - Google Sheets](https://docs.google.com/spreadsheets/d/1Vn1TLUCuKpbDOMiU2uNNofOcIm2Y1hBNCA6T32BERrQ/edit?usp=sharing)


## 🚀 Como Executar o Projeto com Docker Compose

Este projeto usa **Docker Compose** para orquestrar o ambiente (Spring Boot + MySQL 8) e um **build multi-stage** para compilar o JAR. As **Variáveis de Ambiente** necessárias são obrigatoriamente carregadas via Host.

### Pré-requisitos

Docker e Docker Compose (ou o comando `docker compose`) instalados.

---

### 1. Configurar Variáveis de Ambiente

As variáveis de configuração e segredos a seguir são essenciais. Você deve defini-las no seu arquivo de perfil ou no sistema, dependendo do seu SO.

#### Linux / macOS

1. **Edite o arquivo de perfil** (ex: `nano ~/.bashrc` ou `nano ~/.zshrc`) e adicione as variáveis com `export`:

    ```bash
    export MYSQL_PASSWORD="sua_senha_secreta"
    export JWT_SECRET_KEY="sua_chave_secreta_jwt"
    export PAYMENT_AUTHORIZER_URL="http://outro-servico-local:8081/api"
    ```

2. **Recarregue o perfil** na sessão atual:

    ```bash
    source ~/.bashrc
    # ou
    source ~/.zshrc
    ```

#### Windows (PowerShell)

1. **Defina as variáveis de ambiente** na sessão atual do PowerShell:

    ```powershell
    setx MYSQL_PASSWORD "sua_senha_secreta"
    setx JWT_SECRET_KEY "sua_chave_secreta_jwt"
    setx PAYMENT_AUTHORIZER_URL "http://outro-servico-local:8081/api"
    ```

> ⚠️ No Windows, pode ser necessário abrir um **novo PowerShell** para que as variáveis entrem em vigor.

**Variáveis de Ambiente:**

| Variável | Descrição | Observação                              |
| :--- | :--- |:----------------------------------------|
| `MYSQL_PASSWORD` | Senha do usuário `root` do MySQL. | Mínimo de 8 caracteres.                 |
| `JWT_SECRET_KEY` | Chave secreta para assinatura de tokens (HS256). | **Mínimo de 32 caracteres (256 bits)**. |
| `PAYMENT_AUTHORIZER_URL`| URL do serviço externo de autorização. | Deve ser uma URL válida.                |

---

### 2. Compilar e Iniciar o Ambiente

Use o comando com `--build` apenas quando houver alterações no código ou no Dockerfile.

| Condição | Comando | Observação |
| :--- | :--- | :--- |
| **Primeira execução** ou **Código/Dockerfile alterado** | `docker compose up --build` | Força a compilação do JAR e a criação da imagem. |
| **Execuções subsequentes** ou **Variáveis de Ambiente alteradas** | `docker compose up` | Reutiliza a imagem existente, aplicando novas variáveis de ambiente e reiniciando os containers. |

Execute a partir do diretório raiz do projeto:

```bash
docker compose up --build
