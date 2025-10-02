# SGES — Módulo de Professores: Interface e Instruções

Este documento registra as alterações e instruções realizadas nesta sprint/prompt para o módulo de Professores do SGES.

## Entregas desta sprint
- Interface Web (SPA simples em HTML/JS) para gestão de Professores.
  - Local: `src/main/resources/static/professores.html`
  - Ações suportadas: listar, filtrar, criar, editar e excluir professores.
  - Navegação adicionada entre Alunos (`/`) e Professores (`/professores.html`).
- API REST para Professores pronta e integrada à interface:
  - Controller: `src/main/java/com/sges/sges/professores/ProfessorController.java`
  - Service: `src/main/java/com/sges/sges/professores/ProfessorService.java`
  - Repository: `src/main/java/com/sges/sges/professores/ProfessorRepository.java`
  - Entidade: `src/main/java/com/sges/sges/professores/Professor.java`
  - DTOs: `src/main/java/com/sges/sges/professores/dto/ProfessorRequest.java` e `ProfessorResponse.java`
- Validações e tratamento de erros:
  - `nome`: obrigatório
  - `registro`: obrigatório e único (conflitos retornam HTTP 409)
  - `dataNascimento`: obrigatória e no passado
  - `email`: formato válido quando informado
  - Tratamento global de erros em `common/GlobalExceptionHandler.java` com payload consistente (mensagem + lista de field errors quando aplicável).
- README atualizado com instruções de execução e documentação dos módulos de Alunos e Professores.

## Interface Web — Professores (professores.html)
A página oferece um formulário de cadastro/edição e uma tabela com listagem e filtro rápido.

Campos do formulário:
- Nome (obrigatório)
- Registro (obrigatório, único)
- Data de Nascimento (obrigatório — ISO yyyy-MM-dd)
- E-mail (opcional)
- Telefone (opcional)

Principais interações:
- Salvar: cria ou atualiza o professor conforme o campo oculto de ID esteja vazio ou preenchido.
- Limpar: reseta o formulário e volta ao modo “Novo”.
- Filtro rápido: filtra por nome/registro conforme digitação.
- Recarregar: busca novamente os dados do backend.
- Editar: carrega dados do professor no formulário para edição.
- Excluir: remove o professor (com confirmação) e atualiza a tabela.

Tratamento de erros na UI:
- Exibe mensagens de validação vindas da API (incluindo conflitos de `registro`).
- Mostra toasts de feedback para ações concluídas e falhas inesperadas.

Formato de datas:
- UI envia/espera `dataNascimento` como `yyyy-MM-dd` (compatível com `LocalDate`). Exibição na lista em `dd/MM/yyyy`.

## API REST — Professores
Base: `/api/professores`

Endpoints:
- POST `/api/professores` — cria um professor
- GET `/api/professores/{id}` — busca por id
- GET `/api/professores` — lista todos
- PUT `/api/professores/{id}` — atualiza um professor
- DELETE `/api/professores/{id}` — remove um professor

Exemplo (POST):
```json
{
  "nome": "Carlos Lima",
  "registro": "REG-001",
  "dataNascimento": "1980-05-20",
  "email": "carlos.lima@example.com",
  "telefone": "(11) 90000-0000"
}
```

Respostas comuns:
- 201 Created — criação com Location do recurso
- 200 OK — buscar/listar/atualizar
- 204 No Content — remoção
- 400 Bad Request — erros de validação (payload inclui `errors` com campo/mensagem)
- 404 Not Found — recurso inexistente
- 409 Conflict — violação de unicidade (`registro` duplicado)

## Como executar (Windows, cmd.exe)
Pré-requisitos: JDK 21 disponível no PATH.

1) Rodar testes
```cmd
mvnw.cmd clean test
```

2) Subir a aplicação (porta 8081)
```cmd
mvnw.cmd spring-boot:run
```

3) Acessar a UI
- Alunos: http://localhost:8081/
- Professores: http://localhost:8081/professores.html

Opcional — empacotar e rodar o JAR:
```cmd
mvnw.cmd clean package
java -jar target\sges-0.0.1-SNAPSHOT.jar
```

Banco de dados (H2):
- Console: http://localhost:8081/h2-console
- JDBC URL: `jdbc:h2:mem:sges`
- Usuário: `sa` | Senha: (vazio)

## Testes rápidos via curl (Windows)
Criar professor:
```cmd
curl -X POST http://localhost:8081/api/professores ^
  -H "Content-Type: application/json" ^
  -d "{\"nome\":\"Carlos Lima\",\"registro\":\"REG-001\",\"dataNascimento\":\"1980-05-20\",\"email\":\"carlos.lima@example.com\",\"telefone\":\"(11) 90000-0000\"}"
```
Listar professores:
```cmd
curl http://localhost:8081/api/professores
```

## Notas técnicas
- Porta configurada em `server.port=8081` (`src/main/resources/application.properties`).
- Banco em memória H2 recriado a cada inicialização. `spring.jpa.hibernate.ddl-auto=update`.
- Constraint de unicidade: `registro` (tabela `professores`).
- A interface consome a API na mesma origem (caminhos relativos `/api/...`).

## Próximos passos sugeridos
- Paginação e ordenação nos endpoints de listagem.
- Busca avançada por nome/registro.
- Máscaras/validação mais ricas no front (telefone, e-mail) e feedback inline.
- Campos adicionais (ex.: área/disciplina, carga horária) conforme necessidades do domínio.

