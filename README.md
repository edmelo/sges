# SGES - Sistema de Gestão Escolar

Este repositório contém o projeto do Sistema de Gestão Escolar (SGES). As primeiras sprints implementam os módulos de Cadastro de Alunos e Cadastro de Professores com API REST, validações e persistência em banco em memória (H2).

## Tecnologias
- Java 21
- Spring Boot 3 (Web, Data JPA, Validation)
- H2 Database (ambiente de dev/test)
- Lombok
- Testes com Spring Boot Test e MockMvc

## O que foi implementado nesta(s) sprint(s)

### Cadastro de Alunos
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
- Interface Web (SPA simples em HTML/JS) para gerenciar alunos.

### Cadastro de Professores
- Entidade `Professor` com campos: `id`, `nome`, `registro` (único), `dataNascimento`, `email`, `telefone`.
- DTOs (`ProfessorRequest`, `ProfessorResponse`).
- Camadas Repository, Service e Controller.
- Validações:
  - `nome`: obrigatório
  - `registro`: obrigatório e único (409 quando duplicado)
  - `dataNascimento`: obrigatório e no passado
  - `email`: formato válido quando informado
- Testes de integração cobrindo criação, busca/lista e registro duplicado.

## Como executar
Pré-requisitos: JDK 21 instalado e disponível no PATH.

1) Rodar os testes
```cmd
mvnw.cmd clean test
```

2) Subir a aplicação (porta 8081)
```cmd
mvnw.cmd spring-boot:run
```

Acesse a interface web em: http://localhost:8081/

Opcional: empacotar e executar o JAR
```cmd
mvnw.cmd clean package
java -jar target\sges-0.0.1-SNAPSHOT.jar
```

### Interface Web (Alunos)
- Local: `src/main/resources/static/index.html`
- URL: `http://localhost:8081/`
- Funcionalidades: listar, filtrar, criar, editar e excluir alunos via API.
- Erros de validação e conflitos (ex.: matrícula duplicada) são exibidos no formulário.

### Interface Web (Professores)
- Local: `src/main/resources/static/professores.html`
- URL: `http://localhost:8081/professores.html`
- Funcionalidades: listar, filtrar, criar, editar e excluir professores via API.
- Navegação: na barra superior há links para alternar entre Alunos e Professores.

### Banco de Dados (H2)
- Console H2: http://localhost:8081/h2-console
- JDBC URL: `jdbc:h2:mem:sges`
- Usuário: `sa` | Senha: (vazio)

## Endpoints (API)

### Alunos
Base: `/api/alunos`
- POST `/api/alunos` — cria um aluno
- GET `/api/alunos/{id}` — busca por id
- GET `/api/alunos` — lista todos
- PUT `/api/alunos/{id}` — atualiza um aluno
- DELETE `/api/alunos/{id}` — remove um aluno

Exemplo de requisição (POST):
```json
{
  "nome": "Maria Souza",
  "matricula": "MAT-001",
  "dataNascimento": "2005-03-10",
  "email": "maria@example.com",
  "telefone": "(11) 99999-9999"
}
```

### Professores
Base: `/api/professores`
- POST `/api/professores` — cria um professor
- GET `/api/professores/{id}` — busca por id
- GET `/api/professores` — lista todos
- PUT `/api/professores/{id}` — atualiza um professor
- DELETE `/api/professores/{id}` — remove um professor

Exemplo de requisição (POST):
```json
{
  "nome": "Carlos Lima",
  "registro": "REG-001",
  "dataNascimento": "1980-05-20",
  "email": "carlos.lima@example.com",
  "telefone": "(11) 90000-0000"
}
```

### Respostas comuns
- 201 Created (Location com URL do recurso) ao criar
- 200 OK ao buscar/listar/atualizar
- 204 No Content ao deletar
- 400 Bad Request para erros de validação (payload inclui `errors` com os campos)
- 404 Not Found quando não existir
- 409 Conflict para chave única duplicada (`matricula` para alunos, `registro` para professores)

### Testes rápidos via curl (Windows)
Alunos — Criar:
```cmd
curl -X POST http://localhost:8081/api/alunos ^
  -H "Content-Type: application/json" ^
  -d "{\"nome\":\"Maria Souza\",\"matricula\":\"MAT-001\",\"dataNascimento\":\"2005-03-10\",\"email\":\"maria@example.com\",\"telefone\":\"(11) 99999-9999\"}"
```
Alunos — Listar:
```cmd
curl http://localhost:8081/api/alunos
```

Professores — Criar:
```cmd
curl -X POST http://localhost:8081/api/professores ^
  -H "Content-Type: application/json" ^
  -d "{\"nome\":\"Carlos Lima\",\"registro\":\"REG-001\",\"dataNascimento\":\"1980-05-20\",\"email\":\"carlos.lima@example.com\",\"telefone\":\"(11) 90000-0000\"}"
```
Professores — Listar:
```cmd
curl http://localhost:8081/api/professores
```

## Estrutura principal
- `src/main/java/com/sges/sges/alunos` — entidade, controller, service e repository de Aluno
- `src/main/java/com/sges/sges/professores` — entidade, controller, service e repository de Professor
- `src/main/java/com/sges/sges/common` — modelos e tratador global de erros
- `src/test/java/com/sges/sges/alunos` — testes de integração do módulo Alunos
- `src/test/java/com/sges/sges/professores` — testes de integração do módulo Professores
- `src/main/resources/static/index.html` — interface web (Alunos)
- `src/main/resources/static/professores.html` — interface web (Professores)

## Notas de desenvolvimento
- Banco em memória (H2) é recriado a cada inicialização.
- O console do H2 está habilitado em `/h2-console` para facilitar inspeção durante o desenvolvimento.

## Próximas sprints (roadmap)
- Gestão de Turmas
- Notas e Avaliações
- Frequência
- Relatórios
- Comunicação
- Financeiro
