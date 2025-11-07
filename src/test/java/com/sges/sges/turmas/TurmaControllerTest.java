package com.sges.sges.turmas;

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
class TurmaControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @Test
    @DisplayName("Deve criar, buscar e listar turmas")
    void criarBuscarListar() throws Exception {
        String payload = """
                {
                  "nome": "Turma 1A",
                  "codigo": "T-1A-2025",
                  "descricao": "Turma do primeiro ano - A",
                  "capacidade": 35
                }
                """;

        String location = mvc.perform(post("/api/turmas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/turmas/")))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.nome", is("Turma 1A")))
                .andReturn()
                .getResponse()
                .getHeader("Location");

        mvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo", is("T-1A-2025")));

        mvc.perform(get("/api/turmas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[*].codigo", hasItem("T-1A-2025")));
    }

    @Test
    @DisplayName("Não deve permitir código de turma duplicado")
    void naoPermiteCodigoDuplicado() throws Exception {
        String p1 = """
                {
                  "nome": "Turma X",
                  "codigo": "COD-XYZ",
                  "descricao": "Desc X",
                  "capacidade": 25
                }
                """;
        String p2 = """
                {
                  "nome": "Turma Y",
                  "codigo": "COD-XYZ",
                  "descricao": "Desc Y",
                  "capacidade": 30
                }
                """;

        mvc.perform(post("/api/turmas").contentType(MediaType.APPLICATION_JSON).content(p1))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/turmas").contentType(MediaType.APPLICATION_JSON).content(p2))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Deve validar campos obrigatórios e regras em turma")
    void validarCampos() throws Exception {
        String invalido = """
                {
                  "nome": "",
                  "codigo": "",
                  "descricao": "",
                  "capacidade": 0
                }
                """;

        mvc.perform(post("/api/turmas").contentType(MediaType.APPLICATION_JSON).content(invalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", notNullValue()));
    }
}

