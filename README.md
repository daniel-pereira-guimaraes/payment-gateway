
# Payment Gateway API

## 📌 Descrição do Projeto

O **Payment Gateway API** é um serviço desenvolvido em **Java 21** com **Spring Boot 3.1.4**, que oferece gerenciamento básico de cobranças e pagamentos, incluindo:

- Cadastro de usuários.
- Registro de depósitos, cobranças e pagamentos.
- Integração com serviço externo de autorização de pagamento.
- Validação de dados e regras de negócio robustas.
- Geração e validação de **JWT** para autenticação segura.
- API documentada com **Swagger**.

Desenvolvido para fins **didáticos** e **demonstração de conhecimentos**.

> ### Esforço
> - 🗓️ Período de desenvolvimento: **16/10/2025** a **23/10/2025**
> - ⏱️ Carga horária aproximada: **70 horas**

---

## ✅ Testes Automatizados e Manuais

O projeto conta com mais de **450 testes automatizados**, incluindo:

- **Testes unitários** com **JUnit 5**, **Mockito** e **Hamcrest**.
- **Testes de integração** cobrindo **controllers** e **repositórios**, utilizando **MockMvc** e **H2Database** para simulações em memória.
- Cobertura abrangente de regras de negócio, validações e fluxos de erro.

Mesmo com elevada cobertura automatizada, foram realizados mais de **50 testes manuais**, identificando 5 bugs de validação de dados, todos corrigidos.

[Planilha de testes manuais - Google Sheets](https://docs.google.com/spreadsheets/d/1Vn1TLUCuKpbDOMiU2uNNofOcIm2Y1hBNCA6T32BERrQ/edit?usp=sharing)

---

## 🚀 Como Executar o Projeto com Docker Compose

O projeto utiliza **Docker Compose** para orquestrar o ambiente (**Spring Boot + MySQL 8**) e um **build multi-stage** para compilar o JAR. 
As variáveis de ambiente necessárias devem ser carregadas via **host**, não no código.

### Pré-requisitos

- Docker e Docker Compose (ou `docker compose`) instalados.
- Configuração das variáveis de ambiente.

---

### 1. Configurar Variáveis de Ambiente

#### Linux / macOS

1. Edite seu arquivo de perfil (`~/.bashrc` ou `~/.zshrc`) e adicione:

```bash
export MYSQL_PASSWORD="sua_senha_secreta"
export JWT_SECRET_KEY="sua_chave_secreta_jwt"
export PAYMENT_AUTHORIZER_URL="http://outro-servico-local:8081/api"
```

2. Recarregue o perfil:

```bash
source ~/.bashrc
# ou
source ~/.zshrc
```

#### Windows (PowerShell)

```powershell
setx MYSQL_PASSWORD "sua_senha_secreta"
setx JWT_SECRET_KEY "sua_chave_secreta_jwt"
setx PAYMENT_AUTHORIZER_URL "http://outro-servico-local:8081/api"
```

> ⚠️ Pode ser necessário abrir um **novo PowerShell** para que as variáveis entrem em vigor.

**Variáveis de Ambiente:**

| Variável | Descrição |
| :--- | :--- |
| `MYSQL_PASSWORD` | Senha do usuário `root` do MySQL |
| `JWT_SECRET_KEY` | Chave secreta para assinatura de tokens (HS256) |
| `PAYMENT_AUTHORIZER_URL` | URL do serviço externo de autorização |

---

### 2. Compilar e Iniciar o Ambiente

Use `--build` apenas quando houver alterações no código ou Dockerfile.

| Condição | Comando | Observação |
| :--- | :--- | :--- |
| Primeira execução ou alterações no código/Dockerfile | `docker compose up --build` | Compila o JAR e cria a imagem |
| Execuções subsequentes ou alteração de variáveis | `docker compose up` | Reutiliza a imagem existente e reinicia os containers |

Execute a partir do diretório raiz:

```bash
docker compose up --build
```

---

## 🛠 Tecnologias Utilizadas

- **Java 21**, **Spring Boot 3.1.4**
- **Banco de dados:** MySQL 8, H2Database (testes)
- **Persistência:** JDBC, Liquibase
- **Segurança:** JWT (HS256)
- **Testes:** JUnit 5, Mockito, Hamcrest
- **Documentação:** Swagger
- **Infraestrutura:** Docker, Docker Compose

---

## 📄 Acesso à Documentação Swagger

Após iniciar a aplicação, a documentação Swagger está disponível em:

```
http://localhost:8080/swagger-ui/index.html
```

Todos os endpoints, exemplos de requisições e respostas estão documentados.

---

## ⚡ Boas Práticas e Observações Gerais

- Boas práticas de **Clean Code**, **Clean Architecture**, **DDD** e **TDD** foram aplicadas.
- Uso de vários recursos modernos das versões mais novas do **Java**.
- Segredos e variáveis sensíveis **não estão no código**, apenas no host.
- Uso de **JDBC puro** para máxima performance no acesso aos dados.
- Uso de **Liquibase** para versionamento da estrutura do banco de dados.
- Mascaração de dados sensíveis em exceptions e logs.

---

## ⚠️ Integração com Autorizador e Proteção de Dados

- A integração com o **serviço externo autorizador** é **simplificada**, apenas para demonstrar o básico da integração; não reflete produção.

- **Dados de cartão são salvos no banco de dados apenas para fins didáticos.**  
  Em produção, isso **não é recomendado**. Soluções seguras incluem:

   1. **Nunca gravar dados completos do cartão no banco:**
      - Repassar os dados apenas para o autorizador.
      - Salvar apenas o ID da autorização para cancelamentos futuros.

   2. **Salvar apenas hash do número do cartão:**
      - Útil para cancelamentos, mas menos seguro que a primeira opção.

> Em geral, evite armazenar dados sensíveis no banco; deixe o tratamento sob responsabilidade do autorizador.

---

## 🤖 Uso de IAs no Desenvolvimento

Durante o desenvolvimento, foram utilizadas **IAs** como **ChatGPT** e **Gemini**, exclusivamente via navegador web, para:

- Auxílio na geração de trechos de código.
- Agilidade para encontrar bugs no código.
- Sugestões de refatoração e melhorias de design.
- Revisão de conceitos de Java, Spring Boot, arquitetura e padrões de projeto.

> O desenvolvedor mantém **total responsabilidade** sobre qualidade, consistência, design, arquitetura e testes. As IAs são apenas ferramentas de apoio.

### Considerações sobre uso de IAs

- **Não é confiável**, exigindo supervisão e revisão humana sempre.
- **Ajuda muito**, mas em alguns momentos **pode atrapalhar**.
- O **uso no navegador** dificulta a **contextualização**, exigindo prompts enormes, 
  algo que pode ser resolvido com **IDE** integrada com IAs, como **Cursor** e similares.
 
---

## Números do projeto

- **7** dias desenvolvendo
- **70** horas de dedicação, aproximadamente
- **10** endpoints
- **4** tabelas no MySQL
- Mais de **200** commits
- Mais de **180** arquivos *.java*
- Mais de **8000** linhas de código Java
- **96%** de cobertura de testes automatizados
- Mais de **450** testes automatizado
- Mais de **50** testes manuais
- Apenas **5** bugs detectados nos testes manuais (deve ter outros escondidos! rsrs)

---

## 📜 Licença

Este projeto foi desenvolvido para fins **didáticos** e **demonstração de conhecimentos.**
Todo o código e demais artefatos do projeto podem ser usados para estudos. O uso total ou 
parcial para outros fins **só é permitido mediante autorização expressa do autor/desenvolvedor**.