# Módulo de Frequência — Documentação de alterações e instruções

Este documento descreve todas as alterações implementadas neste prompt para adicionar o Módulo de Frequência ao projeto SGES, incluindo modelagem, APIs, testes, UI e instruções de uso.

## 1) Modelagem e Persistência

- Entidade `Frequencia` (JPA):
  - Tabela: `frequencias`
  - Campos:
    - `id` (PK)
    - `turma` (FK → turmas.id, not null)
    - `aluno` (FK → alunos.id, not null)
    - `data` (LocalDate, not null)
    - `status` (Enum STRING, not null) — valores em `StatusFrequencia`: `PRESENTE`, `AUSENTE`, `ATRASO`, `JUSTIFICADO`
    - `observacao` (varchar(255), opcional)
  - Regra de unicidade (chave natural): `turma_id + aluno_id + data` (unique)
- Enum `StatusFrequencia`: controla o estado da presença (armazenado como STRING no banco).
- Repositório `FrequenciaRepository`: métodos auxiliares para checar duplicidade, listar por turma/data e por aluno/período.

Arquivos criados (backend):
- `src/main/java/com/sges/sges/frequencias/Frequencia.java`
- `src/main/java/com/sges/sges/frequencias/StatusFrequencia.java`
- `src/main/java/com/sges/sges/frequencias/FrequenciaRepository.java`
- `src/main/java/com/sges/sges/frequencias/dto/FrequenciaRequest.java`
- `src/main/java/com/sges/sges/frequencias/dto/FrequenciaResponse.java`
- `src/main/java/com/sges/sges/frequencias/FrequenciaService.java`
- `src/main/java/com/sges/sges/frequencias/FrequenciaController.java`

Configuração de banco (já existente): H2 em memória, `spring.jpa.hibernate.ddl-auto=update`, aplica automaticamente a tabela/constraints.

## 2) Regras de Negócio e Validações

- É proibido lançar frequência duplicada para o mesmo (turma, aluno, data). Em caso de tentativa: HTTP 409 (CONFLICT).
- Ao criar/atualizar uma frequência:
  - Valida existência de `turmaId` e `alunoId` (404 se não existirem).
  - Valida dados obrigatórios (`turmaId`, `alunoId`, `data`, `status`).
  - Ao atualizar, se a “chave natural” (turma/aluno/data) for alterada, a regra de duplicidade é reavaliada.
- Tratamento de erros padronizado pelo `GlobalExceptionHandler` já presente no projeto (400/404/409).

## 3) API REST

Base: `/api/frequencias`

- Criar frequência
  - `POST /api/frequencias`
  - Body:
    ```json
    {
      "turmaId": 1,
      "alunoId": 10,
      "data": "2025-10-10",
      "status": "PRESENTE",
      "observacao": "opcional"
    }
    ```
  - 201 Created, Location: `/api/frequencias/{id}`

- Buscar por ID
  - `GET /api/frequencias/{id}`

- Listar por turma e data
  - `GET /api/frequencias?turmaId={id}&data=YYYY-MM-DD`

- Listar por aluno em um período
  - `GET /api/frequencias?alunoId={id}&inicio=YYYY-MM-DD&fim=YYYY-MM-DD`

- Atualizar
  - `PUT /api/frequencias/{id}` (mesmo payload do POST)

- Deletar
  - `DELETE /api/frequencias/{id}`

Observações:
- Se os parâmetros de listagem não estiverem numa das combinações válidas acima, retorna 400.

### Exemplos rápidos (curl)

Criação:
```bat
curl -X POST http://localhost:8081/api/frequencias ^
  -H "Content-Type: application/json" ^
  -d "{\"turmaId\":1,\"alunoId\":10,\"data\":\"2025-10-10\",\"status\":\"PRESENTE\"}"
```

Listagem por turma/data:
```bat
curl "http://localhost:8081/api/frequencias?turmaId=1&data=2025-10-10"
```

## 4) Camada de Serviço (Resumo)

- `FrequenciaService.criar(request)`: valida entidades, checa duplicidade, persiste e retorna DTO.
- `buscarPorId(id)`: 404 se inexistente.
- `listarPorTurmaData(turmaId, data)`: retorna lista para um dia.
- `listarPorAlunoPeriodo(alunoId, inicio, fim)`: retorna lista no intervalo.
- `atualizar(id, request)`: revalida chave natural → duplicidade.
- `deletar(id)`: 404 se inexistente.

## 5) Testes Automatizados

Arquivo criado:
- `src/test/java/com/sges/sges/frequencias/FrequenciaControllerTest.java`

Coberturas:
- Fluxo básico: criar turma e aluno, lançar frequência, buscar por id, listar por turma/data e impedir duplicidades.
- Validação de parâmetros de listagem (400 quando faltam combinações obrigatórias).

Como rodar (Windows, cmd.exe):
```bat
E:\IntellijIdea\sges\mvnw.cmd -f E:\IntellijIdea\sges\pom.xml test
```

## 6) UI Estática

- Página nova: `src/main/resources/static/frequencias.html`
  - Formulário para lançar frequência (IDs de turma e aluno, data, status e observação).
  - Seção para listar frequências filtrando por turma e data.
  - Consome os endpoints REST acima.
- Navegação atualizada para incluir o link “Frequências”:
  - Arquivo alterado: `src/main/resources/static/index.html` (adição do link no menu)

Como acessar no navegador (após subir a aplicação):
- `http://localhost:8081/frequencias.html`

## 7) Qualidade e Status

- Build: OK
- Testes: OK (inclui a nova suíte de frequências)
- Banco: H2 em memória, esquema criado/atualizado automaticamente via JPA (ddl-auto=update)

## 8) Próximos Passos (opcional)

- UI: autocomplete para seleção de turma/aluno por nome/código (evitar entrada manual de IDs).
- Relatórios: taxa de presença por turma/período; exportação CSV/Excel.
- Regras adicionais: janela de lançamento/edição por período, justificativas com anexos.

---

Referência rápida de arquivos criados/alterados neste prompt:

- Criados (backend):
  - `src/main/java/com/sges/sges/frequencias/Frequencia.java`
  - `src/main/java/com/sges/sges/frequencias/StatusFrequencia.java`
  - `src/main/java/com/sges/sges/frequencias/FrequenciaRepository.java`
  - `src/main/java/com/sges/sges/frequencias/dto/FrequenciaRequest.java`
  - `src/main/java/com/sges/sges/frequencias/dto/FrequenciaResponse.java`
  - `src/main/java/com/sges/sges/frequencias/FrequenciaService.java`
  - `src/main/java/com/sges/sges/frequencias/FrequenciaController.java`

- Criados (testes):
  - `src/test/java/com/sges/sges/frequencias/FrequenciaControllerTest.java`

- Criados/alterados (UI):
  - `src/main/resources/static/frequencias.html` (criado)
  - `src/main/resources/static/index.html` (link do menu para Frequências adicionado)

