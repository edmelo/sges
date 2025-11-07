# Módulo de Turmas — Alterações e Instruções

Este documento descreve tudo o que foi implementado neste prompt para o módulo de Turmas (cadastro e gestão), incluindo mudanças no código, endpoints, validações, UI, como executar e testar.

## Resumo do que foi entregue
- CRUD completo de Turmas via API REST.
- DTOs de entrada/saída, validações e tratamento de erros consistente com o resto do projeto.
- Testes de integração (MockMvc) cobrindo cenários principais.
- Página web simples (HTML/JS) para criar, editar, listar, filtrar e excluir turmas.
- Navegação adicionada nas páginas existentes.
- README atualizado com documentação do módulo.

## Arquivos criados
- Backend (Java):
  - `src/main/java/com/sges/sges/turmas/Turma.java`
  - `src/main/java/com/sges/sges/turmas/TurmaRepository.java`
  - `src/main/java/com/sges/sges/turmas/TurmaService.java`
  - `src/main/java/com/sges/sges/turmas/TurmaController.java`
  - `src/main/java/com/sges/sges/turmas/dto/TurmaRequest.java`
  - `src/main/java/com/sges/sges/turmas/dto/TurmaResponse.java`
- Testes:
  - `src/test/java/com/sges/sges/turmas/TurmaControllerTest.java`
- UI (estático):
  - `src/main/resources/static/turmas.html`

## Arquivos alterados
- `src/main/resources/static/index.html` — adicionado link “Turmas” na navegação.
- `src/main/resources/static/professores.html` — adicionado link “Turmas” na navegação.
- `README.md` — documentação expandida com o módulo de Turmas (endpoints, UI e exemplos de uso).

## Modelo de dados (Turma)
- Campos:
  - `id` (Long, gerado)
  - `nome` (String, obrigatório, até 120)
  - `codigo` (String, obrigatório, único, até 30)
  - `descricao` (String, opcional, até 255)
  - `capacidade` (Integer, obrigatório, > 0)
- Tabela: `turmas`
- Restrição única: `uk_turmas_codigo` em `codigo`

## DTOs
- `TurmaRequest` (entrada):
  - `nome`: string, obrigatório
  - `codigo`: string, obrigatório, até 30
  - `descricao`: string, opcional, até 255
  - `capacidade`: inteiro, obrigatório, positivo (> 0)
- `TurmaResponse` (saída):
  - `id`, `nome`, `codigo`, `descricao`, `capacidade`

## Endpoints da API (Turmas)
Base: `/api/turmas`
- POST `/api/turmas` — cria uma turma
- GET `/api/turmas/{id}` — busca por id
- GET `/api/turmas` — lista todas
- PUT `/api/turmas/{id}` — atualiza uma turma
- DELETE `/api/turmas/{id}` — remove uma turma

Exemplo (POST):
```json
{
  "nome": "Turma 1A",
  "codigo": "T-1A-2025",
  "descricao": "Turma do primeiro ano - A",
  "capacidade": 35
}
```

## Validações e erros
- Regras:
  - `nome` obrigatório; `codigo` obrigatório e único; `capacidade` > 0.
- Erros comuns:
  - 400 Bad Request — violações de validação (payload inclui `errors` por campo)
  - 404 Not Found — recurso não encontrado
  - 409 Conflict — `codigo` duplicado
- Tratamento global via `GlobalExceptionHandler`, alinhado com módulos de Alunos/Professores.

## Página Web (Turmas)
- Arquivo: `src/main/resources/static/turmas.html`
- URL: `http://localhost:8081/turmas.html`
- Funcionalidades: listar, filtrar, criar, editar e excluir turmas.
- Navegação entre módulos disponível (Alunos, Professores, Turmas) nas páginas estáticas.

## Como executar
Pré-requisito: JDK 21.

- Rodar os testes:
```cmd
mvnw.cmd clean test
```
- Subir a aplicação (porta 8081):
```cmd
mvnw.cmd spring-boot:run
```
- Acessar UI:
  - Alunos: `http://localhost:8081/`
  - Professores: `http://localhost:8081/professores.html`
  - Turmas: `http://localhost:8081/turmas.html`

## Testes automatizados
- Classe: `TurmaControllerTest` (MockMvc)
- Casos cobertos:
  - Criar, buscar por id e listar turmas
  - Rejeitar `codigo` duplicado (409)
  - Validar campos obrigatórios e regras (400)
- Resultado (local): 3 testes, 0 falhas/erros (vide `target/surefire-reports/TEST-com.sges.sges.turmas.TurmaControllerTest.xml`).

## Exemplos (curl — Windows cmd)
- Criar turma:
```cmd
curl -X POST http://localhost:8081/api/turmas ^
  -H "Content-Type: application/json" ^
  -d "{\"nome\":\"Turma 1A\",\"codigo\":\"T-1A-2025\",\"descricao\":\"Turma do primeiro ano - A\",\"capacidade\":35}"
```
- Listar turmas:
```cmd
curl http://localhost:8081/api/turmas
```

## Banco H2 (dev/test)
- Console: `http://localhost:8081/h2-console`
- JDBC URL: `jdbc:h2:mem:sges`
- Usuário: `sa` | Senha: (vazio)

## Quality gates (local)
- Build: PASS (compilação e contexto Spring OK)
- Testes: PASS (Alunos, Professores, Turmas)
- Lint/Typecheck: n/a — sem erros reportados

## Próximos passos sugeridos
- Paginação/ordenação em `GET /api/turmas`.
- Filtros server-side (nome/código por query string).
- Relacionamentos (alunos matriculados, professor responsável) e regras de lotação.
- Validações adicionais (por exemplo, limites máximos de capacidade por nível/curso).

