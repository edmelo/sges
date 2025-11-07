# Módulo de Avaliações e Notas — Alterações e Instruções

Este documento resume a implementação do módulo de Avaliações e Notas, com backend (API), testes e página estática.

## Resumo
- CRUD de Avaliações: título, data, turma, descrição (opcional), peso (1..100 opcional)
- Lançamento/edição/remoção de Notas por Avaliação: valor 0.00..10.00, observação (opcional)
- Validação e tratamento de erros consistentes
- Página web simples para gerenciar avaliações e notas

## Arquivos criados
- Backend
  - src/main/java/com/sges/sges/avaliacoes/
    - Avaliacao.java, Nota.java
    - AvaliacaoRepository.java, NotaRepository.java
    - AvaliacaoService.java, NotaService.java
    - AvaliacaoController.java, NotaController.java
  - DTOs
    - src/main/java/com/sges/sges/avaliacoes/dto/
      - AvaliacaoRequest.java, AvaliacaoResponse.java
      - NotaRequest.java, NotaResponse.java
- Testes
  - src/test/java/com/sges/sges/avaliacoes/AvaliacaoControllerTest.java
- UI
  - src/main/resources/static/avaliacoes.html

## Endpoints
- Avaliações (/api/avaliacoes)
  - POST / — criar
  - GET /{id} — buscar
  - GET / — listar (opcional: ?turmaId=)
  - PUT /{id} — atualizar
  - DELETE /{id} — remover (bloqueado se possuir notas)
- Notas (/api/avaliacoes/{avaliacaoId}/notas)
  - POST / — criar
  - GET / — listar da avaliação
  - PUT /{notaId} — atualizar
  - DELETE /{notaId} — remover

## Regras e erros
- 400: campos obrigatórios/padrões inválidos
- 404: turma/avaliação/aluno não encontrado
- 409: duplicidade de nota por avaliação; exclusão de avaliação com notas

## UI
- Página: /avaliacoes.html
- Navegação adicionada às páginas existentes

## Como executar
- Testes
```cmd
mvnw.cmd clean test
```
- Rodar
```cmd
mvnw.cmd spring-boot:run
```
- Acessar UI: http://localhost:8081/avaliacoes.html

