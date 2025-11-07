package com.sges.sges.relatorios;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RelatorioControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @Test
    @DisplayName("Deve gerar CSV de taxa de presença por aluno para uma turma")
    void gerarCsv() throws Exception {
        // cria turma
        String turmaPayload = """
                {
                  "nome": "Turma R",
                  "codigo": "R-001",
                  "descricao": "Turma Relatorio",
                  "capacidade": 30
                }
                """;
        String turmaLoc = mvc.perform(post("/api/turmas").contentType(MediaType.APPLICATION_JSON).content(turmaPayload))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
        long turmaId = mapper.readTree(mvc.perform(get(turmaLoc)).andReturn().getResponse().getContentAsString()).get("id").asLong();

        // cria alunos
        String aluno1 = """
                {"nome":"Aluno A","matricula":"MAT-R-1","dataNascimento":"2006-01-10","email":"a@ex.com","telefone":""}
                """;
        String aluno2 = """
                {"nome":"Aluno B","matricula":"MAT-R-2","dataNascimento":"2006-02-10","email":"b@ex.com","telefone":""}
                """;
        String loc1 = mvc.perform(post("/api/alunos").contentType(MediaType.APPLICATION_JSON).content(aluno1))
                .andExpect(status().isCreated()).andReturn().getResponse().getHeader("Location");
        String loc2 = mvc.perform(post("/api/alunos").contentType(MediaType.APPLICATION_JSON).content(aluno2))
                .andExpect(status().isCreated()).andReturn().getResponse().getHeader("Location");
        long alunoId1 = mapper.readTree(mvc.perform(get(loc1)).andReturn().getResponse().getContentAsString()).get("id").asLong();
        long alunoId2 = mapper.readTree(mvc.perform(get(loc2)).andReturn().getResponse().getContentAsString()).get("id").asLong();

        // lança algumas frequências
        String f1 = """
                {"turmaId": %d, "alunoId": %d, "data":"2025-10-01", "status":"PRESENTE", "observacao":""}
                """.formatted(turmaId, alunoId1);
        String f2 = """
                {"turmaId": %d, "alunoId": %d, "data":"2025-10-02", "status":"AUSENTE", "observacao":""}
                """.formatted(turmaId, alunoId1);
        String f3 = """
                {"turmaId": %d, "alunoId": %d, "data":"2025-10-01", "status":"PRESENTE", "observacao":""}
                """.formatted(turmaId, alunoId2);

        mvc.perform(post("/api/frequencias").contentType(MediaType.APPLICATION_JSON).content(f1)).andExpect(status().isCreated());
        mvc.perform(post("/api/frequencias").contentType(MediaType.APPLICATION_JSON).content(f2)).andExpect(status().isCreated());
        mvc.perform(post("/api/frequencias").contentType(MediaType.APPLICATION_JSON).content(f3)).andExpect(status().isCreated());

        // chama o relatório
        String csv = mvc.perform(get("/api/relatorios/taxa-presenca/turma/" + turmaId).param("inicio", "2025-10-01").param("fim", "2025-10-05"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", containsString("text/csv")))
                .andReturn().getResponse().getContentAsString();

        // valida algumas partes do CSV
        assertTrue(csv.contains("MAT-R-1"));
        assertTrue(csv.contains("MAT-R-2"));
        assertTrue(csv.contains("presentes") || csv.contains("presentes"));
    }
}

