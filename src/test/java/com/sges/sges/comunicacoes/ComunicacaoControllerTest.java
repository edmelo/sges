package com.sges.sges.comunicacoes;

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
class ComunicacaoControllerTest {

    @Autowired MockMvc mvc;

    @Test
    @DisplayName("Deve criar, listar por turma e atualizar comunicação")
    void fluxoBasico() throws Exception {
        // cria turma base
        String turmaPayload = """
                {"nome":"Turma C1","codigo":"C1-2025","descricao":"","capacidade":25}
                """;
        String turmaLoc = mvc.perform(post("/api/turmas").contentType(MediaType.APPLICATION_JSON).content(turmaPayload))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
        long turmaId = Long.parseLong(turmaLoc.substring(turmaLoc.lastIndexOf('/')+1));

        // cria comunicação para a turma
        String comPayload = ("""
                {
                  "titulo": "Aviso",
                  "conteudo": "Reunião amanhã",
                  "data": "2025-10-05",
                  "autor": "Coord",
                  "turmaId": %d
                }
                """.formatted(turmaId));
        String comLoc = mvc.perform(post("/api/comunicacoes").contentType(MediaType.APPLICATION_JSON).content(comPayload))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/comunicacoes/")))
                .andExpect(jsonPath("$.titulo", is("Aviso")))
                .andReturn().getResponse().getHeader("Location");

        // lista por turma
        mvc.perform(get("/api/comunicacoes").param("turmaId", String.valueOf(turmaId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())));

        // atualiza
        String upd = ("""
                {
                  "titulo": "Aviso Alterado",
                  "conteudo": "Reunião às 10h",
                  "data": "2025-10-06",
                  "autor": "Coord",
                  "turmaId": %d
                }
                """.formatted(turmaId));
        mvc.perform(put(comLoc).contentType(MediaType.APPLICATION_JSON).content(upd))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo", is("Aviso Alterado")));

        // valida erro quando sem destinatário
        String invalida = """
                {"titulo":"X","conteudo":"Y","data":"2025-10-06"}
                """;
        mvc.perform(post("/api/comunicacoes").contentType(MediaType.APPLICATION_JSON).content(invalida))
                .andExpect(status().isBadRequest());
    }
}

