package com.sges.sges.frequencias;

import com.fasterxml.jackson.databind.JsonNode;
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
class FrequenciaControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @Test
    @DisplayName("Deve lançar frequência, buscar e listar por turma/data")
    void fluxoBasico() throws Exception {
        // cria turma
        String turmaPayload = """
                {
                  "nome": "Turma F",
                  "codigo": "F-001",
                  "descricao": "Turma de frequencia",
                  "capacidade": 40
                }
                """;
        String turmaLoc = mvc.perform(post("/api/turmas").contentType(MediaType.APPLICATION_JSON).content(turmaPayload))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
        long turmaId = mapper.readTree(mvc.perform(get(turmaLoc)).andReturn().getResponse().getContentAsString()).get("id").asLong();

        // cria aluno
        String alunoPayload = """
                {
                  "nome": "Aluno F",
                  "matricula": "MAT-F-1",
                  "dataNascimento": "2006-04-10",
                  "email": "f@ex.com",
                  "telefone": ""
                }
                """;
        String alunoLoc = mvc.perform(post("/api/alunos").contentType(MediaType.APPLICATION_JSON).content(alunoPayload))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
        long alunoId = mapper.readTree(mvc.perform(get(alunoLoc)).andReturn().getResponse().getContentAsString()).get("id").asLong();

        // lança frequência
        String freqPayload = """
                {
                  "turmaId": %d,
                  "alunoId": %d,
                  "data": "2025-10-10",
                  "status": "PRESENTE",
                  "observacao": ""
                }
                """.formatted(turmaId, alunoId);

        String loc = mvc.perform(post("/api/frequencias").contentType(MediaType.APPLICATION_JSON).content(freqPayload))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/frequencias/")))
                .andReturn().getResponse().getHeader("Location");

        // busca
        mvc.perform(get(loc))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.turmaId", is((int)turmaId)))
                .andExpect(jsonPath("$.alunoId", is((int)alunoId)))
                .andExpect(jsonPath("$.status", is("PRESENTE")));

        // listar por turma/data
        mvc.perform(get("/api/frequencias").param("turmaId", String.valueOf(turmaId)).param("data", "2025-10-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())));

        // não permite duplicidade
        mvc.perform(post("/api/frequencias").contentType(MediaType.APPLICATION_JSON).content(freqPayload))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Deve validar parâmetros de listagem")
    void validarParametrosListagem() throws Exception {
        mvc.perform(get("/api/frequencias"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/frequencias").param("turmaId", "1"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/frequencias").param("alunoId", "1").param("inicio", "2025-10-01"))
                .andExpect(status().isBadRequest());
    }
}

