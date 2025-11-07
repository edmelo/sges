package com.sges.sges.avaliacoes.dto;

import java.time.LocalDate;

public record AvaliacaoResponse(
        Long id,
        String titulo,
        String descricao,
        LocalDate data,
        Long turmaId,
        Integer peso
) {}

