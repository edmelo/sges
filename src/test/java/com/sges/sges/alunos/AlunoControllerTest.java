package com.sges.sges.alunos;

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
class AlunoControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @Test
    @DisplayName("Deve criar, buscar e listar alunos")
    void criarBuscarListar() throws Exception {
        String payload = """
                {
                  "nome": "Maria Souza",
                  "matricula": "MAT-001",
                  "dataNascimento": "2005-03-10",
                  "email": "maria@example.com",
                  "telefone": "(11) 99999-9999"
                }
                """;

        // Create
        String location = mvc.perform(post("/api/alunos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/alunos/")))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.nome", is("Maria Souza")))
                .andReturn()
                .getResponse()
                .getHeader("Location");

        // Get by id
        mvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricula", is("MAT-001")));

        // List all contains the created
        mvc.perform(get("/api/alunos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[*].matricula", hasItem("MAT-001")));
    }

    @Test
    @DisplayName("Não deve permitir matrícula duplicada")
    void naoPermiteMatriculaDuplicada() throws Exception {
        String payload1 = """
                {
                  "nome": "João Silva",
                  "matricula": "MAT-XYZ",
                  "dataNascimento": "2004-01-15",
                  "email": "joao@example.com",
                  "telefone": "(11) 98888-7777"
                }
                """;
        String payload2 = """
                {
                  "nome": "Outro Nome",
                  "matricula": "MAT-XYZ",
                  "dataNascimento": "2006-07-21",
                  "email": "outro@example.com",
                  "telefone": "(11) 97777-6666"
                }
                """;

        mvc.perform(post("/api/alunos").contentType(MediaType.APPLICATION_JSON).content(payload1))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/alunos").contentType(MediaType.APPLICATION_JSON).content(payload2))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Deve validar campos obrigatórios")
    void validarCampos() throws Exception {
        String invalido = """
                {
                  "nome": "",
                  "matricula": "",
                  "dataNascimento": "2030-01-01",
                  "email": "invalido",
                  "telefone": ""
                }
                """;

        mvc.perform(post("/api/alunos").contentType(MediaType.APPLICATION_JSON).content(invalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", notNullValue()));
    }
}

