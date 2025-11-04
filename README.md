# SGES - Sistema de Gestão Escolar

Este repositório contém o projeto do Sistema de Gestão Escolar (SGES). As primeiras sprints implementam os módulos de Cadastro de Alunos, Cadastro de Professores e Cadastro/Gestão de Turmas com API REST, validações e persistência em banco em memória (H2).

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
- Interface Web (SPA simples em HTML/JS) para gerenciar professores.

### Cadastro e Gestão de Turmas
- Entidade `Turma` com campos: `id`, `nome`, `codigo` (único), `descricao` (opcional), `capacidade` (> 0).
- DTOs (`TurmaRequest`, `TurmaResponse`).
- Camadas Repository, Service e Controller.
- Validações:
  - `nome`: obrigatório
  - `codigo`: obrigatório e único (409 quando duplicado)
  - `capacidade`: obrigatória e positiva (> 0)
- Testes de integração cobrindo criação, busca/lista e código duplicado.
- Interface Web (HTML/JS) para listar, filtrar, criar, editar e excluir turmas.

## Módulo de Avaliações e Notas

Funcionalidades:
- CRUD de Avaliações (título, data, turma, descrição opcional, peso opcional 1..100)
- Lançamento e gestão de Notas por Avaliação (valor 0.00..10.00, observação opcional)

Entidades:
- Avaliacao { id, titulo, descricao, data, turmaId, peso }
- Nota { id, avaliacaoId, alunoId, valor, observacao }

Endpoints
- Avaliações (base `/api/avaliacoes`):
  - POST `/` cria
  - GET `/{id}` busca por id
  - GET `/` lista (aceita `?turmaId=` opcional)
  - PUT `/{id}` atualiza
  - DELETE `/{id}` remove (bloqueado se possuir notas)
- Notas (base `/api/avaliacoes/{avaliacaoId}/notas`):
  - POST `/` cria nota para aluno
  - GET `/` lista notas da avaliação
  - PUT `/{notaId}` atualiza nota
  - DELETE `/{notaId}` remove nota

Validações e erros:
- 400: validação de campos (mensagens por campo)
- 404: turma/avaliação/aluno não encontrados
- 409: duplicidade de nota (um aluno por avaliação) e exclusão de avaliação com notas

UI estática
- Página: `/avaliacoes.html`
- Navegação adicionada nas páginas de Alunos, Professores e Turmas

Exemplos (Windows cmd)

```cmd
curl -X POST http://localhost:8081/api/avaliacoes ^
  -H "Content-Type: application/json" ^
  -d "{\"titulo\":\"Prova 1\",\"descricao\":\"Conteúdo A\",\"data\":\"2025-10-01\",\"turmaId\":1,\"peso\":30}"

curl -X POST http://localhost:8081/api/avaliacoes/1/notas ^
  -H "Content-Type: application/json" ^
  -d "{\"alunoId\":1,\"valor\":8.5,\"observacao\":\"Boa prova\"}"
```

## Módulo de Comunicação

Funcionalidades:
- Enviar/registrar comunicados para uma Turma ou para um Aluno específico
- CRUD completo via API
- Validações: título e conteúdo obrigatórios; data obrigatória; pelo menos um destinatário (alunoId ou turmaId)

Entidade:
- Comunicacao { id, titulo, conteudo, data, autor?, turmaId?, alunoId? }

Endpoints (base `/api/comunicacoes`):
- POST `/` cria
- GET `/{id}` busca por id
- GET `/` lista (aceita `?turmaId=` e `?alunoId=` opcionais)
- PUT `/{id}` atualiza
- DELETE `/{id}` remove

Exemplos (Windows cmd)
```cmd
curl -X POST http://localhost:8081/api/comunicacoes ^
  -H "Content-Type: application/json" ^
  -d "{\"titulo\":\"Aviso de Reunião\",\"conteudo\":\"Reunião amanhã às 10h\",\"data\":\"2025-10-05\",\"autor\":\"Coordenação\",\"turmaId\":1}"

curl http://localhost:8081/api/comunicacoes?turmaId=1
```

UI estática
- Página: `/comunicacoes.html`
- Navegação adicionada nas páginas de Alunos, Professores, Turmas, Avaliações e Frequências

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
- Navegação: links para alternar entre Alunos, Professores e Turmas.

### Interface Web (Turmas)
- Local: `src/main/resources/static/turmas.html`
- URL: `http://localhost:8081/turmas.html`
- Funcionalidades: listar, filtrar, criar, editar e excluir turmas via API.
- Regras: capacidade deve ser maior que zero.

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

### Turmas
Base: `/api/turmas`
- POST `/api/turmas` — cria uma turma
- GET `/api/turmas/{id}` — busca por id
- GET `/api/turmas` — lista todas
- PUT `/api/turmas/{id}` — atualiza uma turma
- DELETE `/api/turmas/{id}` — remove uma turma

Exemplo de requisição (POST):
```json
{
  "nome": "Turma 1A",
  "codigo": "T-1A-2025",
  "descricao": "Turma do primeiro ano - A",
  "capacidade": 35
}
```

### Avaliações
Base: `/api/avaliacoes`
- POST `/api/avaliacoes` — cria uma avaliação
- GET `/api/avaliacoes/{id}` — busca por id
- GET `/api/avaliacoes` — lista todas (opcional: `?turmaId=`)
- PUT `/api/avaliacoes/{id}` — atualiza uma avaliação
- DELETE `/api/avaliacoes/{id}` — remove uma avaliação (bloqueado se tiver notas)

Exemplo de requisição (POST):
```json
{
  "titulo": "Prova 1",
  "descricao": "Conteúdo A",
  "data": "2025-10-01",
  "turmaId": 1,
  "peso": 30
}
```

### Notas
Base: `/api/avaliacoes/{avaliacaoId}/notas`
- POST `/api/avaliacoes/{avaliacaoId}/notas` — cria uma nota para aluno
- GET `/api/avaliacoes/{avaliacaoId}/notas` — lista notas da avaliação
- PUT `/api/avaliacoes/{avaliacaoId}/notas/{notaId}` — atualiza uma nota
- DELETE `/api/avaliacoes/{avaliacaoId}/notas/{notaId}` — remove uma nota

Exemplo de requisição (POST):
```json
{
  "alunoId": 1,
  "valor": 8.5,
  "observacao": "Boa prova"
}
```

### Comunicações
Base: `/api/comunicacoes`
- POST `/api/comunicacoes` — cria uma comunicação
- GET `/api/comunicacoes/{id}` — busca por id
- GET `/api/comunicacoes` — lista todas (opcional: `?turmaId=` ou `?alunoId=`)
- PUT `/api/comunicacoes/{id}` — atualiza uma comunicação
- DELETE `/api/comunicacoes/{id}` — remove uma comunicação

Exemplo de requisição (POST):
```json
{
  "titulo": "Aviso de Reunião",
  "conteudo": "Reunião amanhã às 10h",
  "data": "2025-10-05",
  "autor": "Coordenação",
  "turmaId": 1
}
```

### Respostas comuns
- 201 Created (Location com URL do recurso) ao criar
- 200 OK ao buscar/listar/atualizar
- 204 No Content ao deletar
- 400 Bad Request para erros de validação (payload inclui `errors` com os campos)
- 404 Not Found quando não existir
- 409 Conflict para chave única duplicada (`matricula` para alunos, `registro` para professores, `codigo` para turmas)

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

Turmas — Criar:
```cmd
curl -X POST http://localhost:8081/api/turmas ^
  -H "Content-Type: application/json" ^
  -d "{\"nome\":\"Turma 1A\",\"codigo\":\"T-1A-2025\",\"descricao\":\"Turma do primeiro ano - A\",\"capacidade\":35}"
```
Turmas — Listar:
```cmd
curl http://localhost:8081/api/turmas
```

Avaliações — Criar:
```cmd
curl -X POST http://localhost:8081/api/avaliacoes ^
  -H "Content-Type: application/json" ^
  -d "{\"titulo\":\"Prova 1\",\"descricao\":\"Conteúdo A\",\"data\":\"2025-10-01\",\"turmaId\":1,\"peso\":30}"
```
Avaliações — Listar:
```cmd
curl http://localhost:8081/api/avaliacoes
```

Notas — Criar:
```cmd
curl -X POST http://localhost:8081/api/avaliacoes/1/notas ^
  -H "Content-Type: application/json" ^
  -d "{\"alunoId\":1,\"valor\":8.5,\"observacao\":\"Boa prova\"}"
```
Notas — Listar:
```cmd
curl http://localhost:8081/api/avaliacoes/1/notas
```

Comunicações — Criar:
```cmd
curl -X POST http://localhost:8081/api/comunicacoes ^
  -H "Content-Type: application/json" ^
  -d "{\"titulo\":\"Aviso de Reunião\",\"conteudo\":\"Reunião amanhã às 10h\",\"data\":\"2025-10-05\",\"autor\":\"Coordenação\",\"turmaId\":1}"
```
Comunicações — Listar:
```cmd
curl http://localhost:8081/api/comunicacoes?turmaId=1
```

## Estrutura principal
- `src/main/java/com/sges/sges/alunos` — entidade, controller, service e repository de Aluno
- `src/main/java/com/sges/sges/professores` — entidade, controller, service e repository de Professor
- `src/main/java/com/sges/sges/turmas` — entidade, controller, service e repository de Turma
- `src/main/java/com/sges/sges/avaliacoes` — entidade, controller, service e repository de Avaliação/Nota
- `src/main/java/com/sges/sges/comunicacoes` — entidade, controller, service e repository de Comunicação
- `src/main/java/com/sges/sges/common` — modelos e tratador global de erros
- `src/test/java/com/sges/sges/alunos` — testes de integração do módulo Alunos
- `src/test/java/com/sges/sges/professores` — testes de integração do módulo Professores
- `src/test/java/com/sges/sges/turmas` — testes de integração do módulo Turmas
- `src/test/java/com/sges/sges/avaliacoes` — testes de integração do módulo Avaliações
- `src/main/resources/static/index.html` — interface web (Alunos)
- `src/main/resources/static/professores.html` — interface web (Professores)
- `src/main/resources/static/turmas.html` — interface web (Turmas)
- `src/main/resources/static/avaliacoes.html` — interface web (Avaliações)
- `src/main/resources/static/comunicacoes.html` — interface web (Comunicações)

## Notas de desenvolvimento
- Banco em memória (H2) é recriado a cada inicialização.
- O console do H2 está habilitado em `/h2-console` para facilitar inspeção durante o desenvolvimento.

## Próximas sprints (roadmap)
- Frequência
- Relatórios
- Comunicação
- Financeiro
