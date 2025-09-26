# SGES - Sistema de Gestão Escolar

Este repositório contém o projeto do Sistema de Gestão Escolar (SGES). A primeira sprint implementa o Módulo de Gestão de Alunos (cadastro de alunos) com API REST, validações e persistência em banco em memória (H2).

## Tecnologias
- Java 21
- Spring Boot 3 (Web, Data JPA, Validation)
- H2 Database (ambiente de dev/test)
- Lombok
- Testes com Spring Boot Test e MockMvc

## O que foi implementado nesta sprint (Cadastro de Alunos)
- Entidade `Aluno` com campos: `id`, `nome`, `matricula` (única), `dataNascimento`, `email`, `telefone`.
- DTOs de entrada/saída (`AlunoRequest`, `AlunoResponse`).
- Camadas Repository, Service e Controller.
- Validações:
  - `nome`: obrigatório
  - `matricula`: obrigatória e única (retorna 409 em caso de duplicidade)
  - `dataNascimento`: obrigatória e no passado
  - `email`: formato válido quando informado
- Tratamento global de erros com payload consistente.
- Testes de integração cobrindo criação, busca/lista e matrícula duplicada.

## Como executar
Pré-requisitos: JDK 21 instalado e disponível no PATH.

1) Rodar os testes
```cmd
mvnw.cmd clean test
```

2) Subir a aplicação (porta 8080)
```cmd
mvnw.cmd spring-boot:run
```

Opcional: empacotar e executar o JAR
```cmd
mvnw.cmd clean package
java -jar target\sges-0.0.1-SNAPSHOT.jar
```

### Banco de Dados (H2)
- Console H2: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:sges`
- Usuário: `sa` | Senha: (vazio)

## Endpoints (API Alunos)
Base: `/api/alunos`
- POST `/api/alunos` — cria um aluno
- GET `/api/alunos/{id}` — busca por id
- GET `/api/alunos` — lista todos
- PUT `/api/alunos/{id}` — atualiza um aluno
- DELETE `/api/alunos/{id}` — remove um aluno

### Exemplo de requisição (POST)
```json
{
  "nome": "Maria Souza",
  "matricula": "MAT-001",
  "dataNascimento": "2005-03-10",
  "email": "maria@example.com",
  "telefone": "(11) 99999-9999"
}
```

### Respostas comuns
- 201 Created (Location com URL do recurso) ao criar
- 200 OK ao buscar/listar/atualizar
- 204 No Content ao deletar
- 400 Bad Request para erros de validação (payload inclui `errors` com os campos)
- 404 Not Found quando não existir
- 409 Conflict para matrícula duplicada

### Teste rápido via curl (Windows)
Criar:
```cmd
curl -X POST http://localhost:8080/api/alunos ^
  -H "Content-Type: application/json" ^
  -d "{\"nome\":\"Maria Souza\",\"matricula\":\"MAT-001\",\"dataNascimento\":\"2005-03-10\",\"email\":\"maria@example.com\",\"telefone\":\"(11) 99999-9999\"}"
```
Listar:
```cmd
curl http://localhost:8080/api/alunos
```

## Estrutura principal
- `src/main/java/com/sges/sges/alunos` — entidade, controller, service e repository de Aluno
- `src/main/java/com/sges/sges/common` — modelos e tratador global de erros
- `src/test/java/com/sges/sges/alunos` — testes de integração do módulo

## Notas de desenvolvimento
- Banco em memória (H2) é recriado a cada inicialização.
- O console do H2 está habilitado em `/h2-console` para facilitar inspeção durante o desenvolvimento.

## Próximas sprints (roadmap)
- Gestão de Professores
- Gestão de Turmas
- Notas e Avaliações
- Frequência
- Relatórios
- Comunicação
- Financeiro

