package com.sges.sges.financeiro;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PagamentoControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    private String criarAluno(String matricula) throws Exception {
        String payload = """
                {
                  \"nome\": \"Aluno Financeiro\",
                  \"matricula\": \"%s\",
                  \"dataNascimento\": \"2005-05-05\",
                  \"email\": \"fin@example.com\",
                  \"telefone\": \"(11) 90000-0000\"
                }
                """.formatted(matricula);
        return mvc.perform(post("/api/alunos").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
    }

    private Long idFromLocation(String location) {
        return Long.parseLong(location.substring(location.lastIndexOf('/') + 1));
    }

    @Test
    @DisplayName("Deve criar, buscar e listar pagamentos")
    void criarBuscarListar() throws Exception {
        Long alunoId = idFromLocation(criarAluno("MAT-PG-001"));

        String payload = """
                {
                  \"alunoId\": %d,
                  \"referencia\": \"2025-11\",
                  \"valor\": 750.00,
                  \"vencimento\": \"2025-11-10\",
                  \"dataPagamento\": null,
                  \"status\": \"PENDENTE\",
                  \"observacao\": \"Mensalidade novembro\"
                }
                """.formatted(alunoId);

        String location = mvc.perform(post("/api/pagamentos").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/pagamentos/")))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn().getResponse().getHeader("Location");

        mvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alunoId", is(alunoId.intValue())))
                .andExpect(jsonPath("$.referencia", is("2025-11")))
                .andExpect(jsonPath("$.status", is("PENDENTE")));

        mvc.perform(get("/api/pagamentos")).andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())));
    }

    @Test
    @DisplayName("Não deve permitir pagamento duplicado por referência para o mesmo aluno")
    void naoPermiteDuplicado() throws Exception {
        Long alunoId = idFromLocation(criarAluno("MAT-PG-002"));
        String base = """
                {
                  \"alunoId\": %d,
                  \"referencia\": \"2025-12\",
                  \"valor\": 800.00,
                  \"vencimento\": \"2025-12-10\",
                  \"dataPagamento\": null,
                  \"status\": \"PENDENTE\",
                  \"observacao\": null
                }
                """.formatted(alunoId);

        mvc.perform(post("/api/pagamentos").contentType(MediaType.APPLICATION_JSON).content(base))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/pagamentos").contentType(MediaType.APPLICATION_JSON).content(base))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Deve validar campos obrigatórios de pagamento")
    void validaCampos() throws Exception {
        String invalido = """
                {
                  \"alunoId\": null,
                  \"referencia\": \"\",
                  \"valor\": -1,
                  \"vencimento\": null,
                  \"dataPagamento\": null,
                  \"status\": null,
                  \"observacao\": "" 
                }
                """;
        mvc.perform(post("/api/pagamentos").contentType(MediaType.APPLICATION_JSON).content(invalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", notNullValue()));
    }
}

