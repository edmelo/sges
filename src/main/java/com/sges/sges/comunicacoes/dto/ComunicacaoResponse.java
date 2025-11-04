package com.sges.sges.comunicacoes.dto;

import java.time.LocalDate;

public record ComunicacaoResponse(
        Long id,
        String titulo,
        String conteudo,
        LocalDate data,
        String autor,
        Long turmaId,
        Long alunoId
) {}

