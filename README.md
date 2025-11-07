# SGES - Sistema de Gestão Escolar

Sistema Web para gestão escolar com foco no módulo de Alunos, incluindo API REST, validações, persistência em banco em memória (H2) e interface web simples (SPA) servida pela própria aplicação.

Status: MVP finalizado e testado end-to-end (API + UI) com testes de integração.

## Tecnologias
- Java 21
- Spring Boot 3 (Web, Data JPA, Validation)
- H2 Database (ambiente de desenvolvimento/testes)
- Lombok (models/DTOs)
- Testes com Spring Boot Test e MockMvc
- Maven Wrapper (mvnw.cmd)

## Requisitos
- Windows com JDK 21 disponível no PATH
- Sem dependência de banco externo (H2 em memória)
- IDE opcional: para usar Lombok na IDE, habilite Annotation Processing

## Como executar (Windows)
1) Rodar os testes
```cmd
mvnw.cmd clean test
```

2) Subir a aplicação (porta 8081)
```cmd
mvnw.cmd spring-boot:run
```
Acesse a interface web em: http://localhost:8081/

3) (Opcional) Empacotar e executar o JAR
```cmd
mvnw.cmd clean package
java -jar target\sges-0.0.1-SNAPSHOT.jar
```

## Configuração em runtime
Arquivo: `src/main/resources/application.properties`
- Server: `server.port=8081`
- H2 console: habilitado em `/h2-console`
- JDBC URL: `jdbc:h2:mem:sges` (com `DB_CLOSE_DELAY=-1` para manter até o encerramento da JVM)
- Usuário: `sa` | Senha: (vazio)
- JPA: `hibernate.ddl-auto=update` e SQL logado formatado

Observações
- O banco em memória é recriado a cada inicialização (ambiente de dev/test). Dados são voláteis.
- A UI consome a API na mesma origem, não há CORS adicional configurado.

## Módulo disponível: Alunos
Entidade: `Aluno` (tabela `alunos`)
- Campos: `id (Long)`, `nome (String, obrigatório, <=120)`, `matricula (String, obrigatório, único, <=30)`, `dataNascimento (LocalDate, obrigatório, passado)`, `email (String, opcional, e-mail válido, <=180)`, `telefone (String, opcional, <=30)`

DTOs
- Entrada: `AlunoRequest { nome, matricula, dataNascimento, email?, telefone? }`
- Saída: `AlunoResponse { id, nome, matricula, dataNascimento, email, telefone }`

Validações
- 400 Bad Request quando campos inválidos (payload inclui lista `errors`)
- 409 Conflict para matrícula duplicada

## API (REST)
Base URL: `/api/alunos`

- POST `/api/alunos` — cria um aluno
- GET `/api/alunos/{id}` — busca por id
- GET `/api/alunos` — lista todos
- PUT `/api/alunos/{id}` — atualiza um aluno
- DELETE `/api/alunos/{id}` — remove um aluno

Regras gerais
- Datas no formato ISO `yyyy-MM-dd` (mapeadas para `LocalDate`)
- Ao criar, retorna `201 Created` com `Location: /api/alunos/{id}` e o corpo do recurso

Exemplo de requisição (POST)
```json
{
  "nome": "Maria Souza",
  "matricula": "MAT-001",
  "dataNascimento": "2005-03-10",
  "email": "maria@example.com",
  "telefone": "(11) 99999-9999"
}
```
Respostas comuns
- 201 Created — criação com Location do recurso
- 200 OK — busca/lista/atualização
- 204 No Content — exclusão
- 400 Bad Request — erro de validação
- 404 Not Found — recurso inexistente
- 409 Conflict — matrícula duplicada

Modelo de erro (payload)
```json
{
  "timestamp": "2025-11-07T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Erro de validação",
  "path": "/api/alunos",
  "errors": [
    { "field": "nome", "message": "must not be blank" },
    { "field": "dataNascimento", "message": "must be a past date" }
  ]
}
```
Para conflitos (409) e não encontrados (404) o campo `errors` pode vir ausente e apenas `message` é retornado.

### Teste rápido via curl (Windows)
Criar
```cmd
curl -X POST http://localhost:8081/api/alunos ^
  -H "Content-Type: application/json" ^
  -d "{\"nome\":\"Maria Souza\",\"matricula\":\"MAT-001\",\"dataNascimento\":\"2005-03-10\",\"email\":\"maria@example.com\",\"telefone\":\"(11) 99999-9999\"}"
```
Listar
```cmd
curl http://localhost:8081/api/alunos
```
Buscar por ID (ex.: 1)
```cmd
curl http://localhost:8081/api/alunos/1
```
Atualizar (ex.: 1)
```cmd
curl -X PUT http://localhost:8081/api/alunos/1 ^
  -H "Content-Type: application/json" ^
  -d "{\"nome\":\"Maria S. Souza\",\"matricula\":\"MAT-001\",\"dataNascimento\":\"2005-03-10\"}"
```
Excluir (ex.: 1)
```cmd
curl -X DELETE http://localhost:8081/api/alunos/1
```

## Interface Web (SPA)
- Local: `src/main/resources/static/index.html`
- Acesso: http://localhost:8081/
- Funcionalidades: listar, filtrar, criar, editar e excluir alunos via API
- Tratamento de erros: mensagens de validação e conflitos são exibidas no formulário

## Testes
- Suite de testes de integração (MockMvc) cobrindo: criação, busca/lista, validações e conflito de matrícula
- Executar
```cmd
mvnw.cmd clean test
```
- Relatórios: `target/surefire-reports`

## Estrutura principal do projeto
- `src/main/java/com/sges/sges/alunos` — entidade, controller, service e repository de Aluno
- `src/main/java/com/sges/sges/common` — modelo e tratador global de erros (ApiError, GlobalExceptionHandler)
- `src/test/java/com/sges/sges/alunos` — testes de integração do módulo
- `src/main/resources/static/index.html` — interface web

## Solução de problemas (FAQ)
- Porta já em uso (8081): altere `server.port` em `application.properties` e reinicie
- H2 Console não abre: verifique `spring.h2.console.enabled=true` e acesse `/h2-console`
- Erro de login no H2: use `jdbc:h2:mem:sges`, usuário `sa` e senha vazia
- Lombok não reconhecido na IDE: habilite "Annotation Processing" nas configurações da IDE
- Matrícula duplicada: a API retornará 409; ajuste o valor no formulário

## Licença
Este repositório não possui uma licença explicitamente definida. Adicione uma licença conforme a necessidade do projeto.
