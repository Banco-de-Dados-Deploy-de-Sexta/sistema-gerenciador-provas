# 📖 Manual de Instalação

## Pré-requisitos

- **JDK 17** ou superior
- **Maven** (ou uma IDE com suporte a Maven, como o IntelliJ IDEA)
- **MySQL Server 8** ou superior

## 1. Clonar o repositório

```bash
git clone https://github.com/Banco-de-Dados-Deploy-de-Sexta/sistema-gerenciador-provas.git
cd sistema-gerenciador-provas
```

## 2. Criar o banco de dados

Com o serviço do MySQL em execução, rode o script [`data/banco_provas.sql`](../data/banco_provas.sql). Ele cria o banco `banco`, as tabelas (`usuario`, `prova`, `prova_aluno`, `questao`, `prova_questao` e `alternativa`) e alguns dados de exemplo, incluindo professores e alunos para o login:

```bash
mysql -u root -p < data/banco_provas.sql
```

> Se você já tinha o banco criado antes, rode também os scripts de migração, nesta ordem: [`data/migracao_peso_prova.sql`](../data/migracao_peso_prova.sql) (peso das questões, se a coluna ainda não existir), [`data/migracao_limites_questao.sql`](../data/migracao_limites_questao.sql) (novos limites de enunciado e alternativas) e [`data/migracao_usuario_prova_aluno.sql`](../data/migracao_usuario_prova_aluno.sql) (Sprint 3: tabela `usuario`, coluna `registro_professor` na prova e tabela `prova_aluno`).

Registros de exemplo para o login: professores `SGP-P1231`, `SGP-P1232` e `SGP-P1233`; alunos `SGP-A1231` a `SGP-A1238`.

## 3. Configurar a conexão

Copie o arquivo `app/.env.example` para `app/.env` e preencha com os dados do seu MySQL:

```env
DB_URL=jdbc:mysql://127.0.0.1:3306/banco
DB_USER=root
DB_PASSWORD=sua_senha
```

> O `.env` contém credenciais e **não deve ser versionado** (já está no `.gitignore`).

## 4. Executar

O arquivo `.env` é lido a partir da pasta de execução, por isso execute a aplicação **a partir da pasta `app`**:

```bash
cd app
mvn javafx:run
```

No IntelliJ IDEA, abra a pasta `app` como projeto Maven e execute a classe `Main` (o *Working directory* da configuração de execução deve ser a pasta `app`).
