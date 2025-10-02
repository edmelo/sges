package com.sges.sges.professores;

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
class ProfessorControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @Test
    @DisplayName("Deve criar, buscar e listar professores")
    void criarBuscarListar() throws Exception {
        String payload = """
                {
                  "nome": "Carlos Lima",
                  "registro": "REG-001",
                  "dataNascimento": "1980-05-20",
                  "email": "carlos.lima@example.com",
                  "telefone": "(11) 90000-0000"
                }
                """;

        String location = mvc.perform(post("/api/professores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/professores/")))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.nome", is("Carlos Lima")))
                .andReturn()
                .getResponse()
                .getHeader("Location");

        mvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registro", is("REG-001")));

        mvc.perform(get("/api/professores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[*].registro", hasItem("REG-001")));
    }

    @Test
    @DisplayName("Não deve permitir registro duplicado")
    void naoPermiteRegistroDuplicado() throws Exception {
        String p1 = """
                {
                  "nome": "Ana Paula",
                  "registro": "REG-XYZ",
                  "dataNascimento": "1975-01-15",
                  "email": "ana@example.com",
                  "telefone": "(11) 98888-7777"
                }
                """;
        String p2 = """
                {
                  "nome": "Outro Professor",
                  "registro": "REG-XYZ",
                  "dataNascimento": "1990-07-21",
                  "email": "outro@example.com",
                  "telefone": "(11) 97777-6666"
                }
                """;

        mvc.perform(post("/api/professores").contentType(MediaType.APPLICATION_JSON).content(p1))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/professores").contentType(MediaType.APPLICATION_JSON).content(p2))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Deve validar campos obrigatórios em professor")
    void validarCampos() throws Exception {
        String invalido = """
                {
                  "nome": "",
                  "registro": "",
                  "dataNascimento": "2030-01-01",
                  "email": "invalido",
                  "telefone": ""
                }
                """;

        mvc.perform(post("/api/professores").contentType(MediaType.APPLICATION_JSON).content(invalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", notNullValue()));
    }
}

