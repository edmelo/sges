package com.sges.sges.avaliacoes;

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
class AvaliacaoControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @Test
    @DisplayName("Deve criar avaliação, lançar nota e listar")
    void fluxoCompleto() throws Exception {
        // cria turma
        String turmaPayload = """
                {
                  "nome": "Turma Teste",
                  "codigo": "T-TEST",
                  "descricao": "Turma de teste",
                  "capacidade": 30
                }
                """;
        String turmaLoc = mvc.perform(post("/api/turmas").contentType(MediaType.APPLICATION_JSON).content(turmaPayload))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
        String turmaBody = mvc.perform(get(turmaLoc)).andReturn().getResponse().getContentAsString();
        long turmaId = mapper.readTree(turmaBody).get("id").asLong();

        // cria avaliação
        String avPayload = """
                {
                  "titulo": "Prova 1",
                  "descricao": "Conteúdo A",
                  "data": "2025-10-01",
                  "turmaId": %d,
                  "peso": 30
                }
                """.formatted(turmaId);

        String avLoc = mvc.perform(post("/api/avaliacoes").contentType(MediaType.APPLICATION_JSON).content(avPayload))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/avaliacoes/")))
                .andExpect(jsonPath("$.titulo", is("Prova 1")))
                .andReturn().getResponse().getHeader("Location");

        String avBody = mvc.perform(get(avLoc)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        JsonNode avJson = mapper.readTree(avBody);
        long avaliacaoId = avJson.get("id").asLong();

        // lista avaliações
        mvc.perform(get("/api/avaliacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", hasItem((int)avaliacaoId)));

        // cria aluno
        String alunoPayload = """
                {
                  "nome": "Aluno X",
                  "matricula": "MAT-1",
                  "dataNascimento": "2005-01-01",
                  "email": "x@exemplo.com",
                  "telefone": ""
                }
                """;
        String alunoLoc = mvc.perform(post("/api/alunos").contentType(MediaType.APPLICATION_JSON).content(alunoPayload))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
        String alunoBody = mvc.perform(get(alunoLoc)).andReturn().getResponse().getContentAsString();
        long alunoId = mapper.readTree(alunoBody).get("id").asLong();

        // lança nota
        String notaPayload = """
                {
                  "alunoId": %d,
                  "valor": 8.5,
                  "observacao": "Boa prova"
                }
                """.formatted(alunoId);
        mvc.perform(post("/api/avaliacoes/"+avaliacaoId+"/notas").contentType(MediaType.APPLICATION_JSON).content(notaPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.alunoId", is((int)alunoId)))
                .andExpect(jsonPath("$.valor", is(8.5)));

        // lista notas da avaliação
        mvc.perform(get("/api/avaliacoes/"+avaliacaoId+"/notas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())));

        // não permite duplicidade de nota
        mvc.perform(post("/api/avaliacoes/"+avaliacaoId+"/notas").contentType(MediaType.APPLICATION_JSON).content(notaPayload))
                .andExpect(status().isConflict());

        // não permite excluir avaliação com notas
        mvc.perform(delete("/api/avaliacoes/"+avaliacaoId))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Deve validar campos de avaliação e nota")
    void validarCampos() throws Exception {
        // avaliação inválida
        String invalida = """
                {
                  "titulo": "",
                  "descricao": "",
                  "data": null,
                  "turmaId": null,
                  "peso": 0
                }
                """;
        mvc.perform(post("/api/avaliacoes").contentType(MediaType.APPLICATION_JSON).content(invalida))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", notNullValue()));

        // cria turma válida para avaliação
        String turmaPayload = """
                {
                  "nome": "Turma YY",
                  "codigo": "YY-1",
                  "descricao": "",
                  "capacidade": 20
                }
                """;
        String turmaLoc = mvc.perform(post("/api/turmas").contentType(MediaType.APPLICATION_JSON).content(turmaPayload))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
        long turmaId = new ObjectMapper().readTree(mvc.perform(get(turmaLoc)).andReturn().getResponse().getContentAsString()).get("id").asLong();

        // cria avaliação válida
        String avPayload = """
                {
                  "titulo": "Trabalho 1",
                  "descricao": "",
                  "data": "2025-10-02",
                  "turmaId": %d,
                  "peso": 10
                }
                """.formatted(turmaId);
        String avLoc = mvc.perform(post("/api/avaliacoes").contentType(MediaType.APPLICATION_JSON).content(avPayload))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
        long avaliacaoId = new ObjectMapper().readTree(mvc.perform(get(avLoc)).andReturn().getResponse().getContentAsString()).get("id").asLong();

        // nota inválida (fora do range)
        String notaInvalida = """
                {
                  "alunoId": 1,
                  "valor": 12.0,
                  "observacao": ""
                }
                """;
        mvc.perform(post("/api/avaliacoes/"+avaliacaoId+"/notas").contentType(MediaType.APPLICATION_JSON).content(notaInvalida))
                .andExpect(status().isBadRequest());
    }
}
