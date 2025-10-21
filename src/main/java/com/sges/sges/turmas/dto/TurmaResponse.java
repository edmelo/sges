package com.sges.sges.turmas.dto;

public record TurmaResponse(
        Long id,
        String nome,
        String codigo,
        String descricao,
        Integer capacidade
) {}

