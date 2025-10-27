package com.sges.sges.avaliacoes.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record AvaliacaoRequest(
        @NotBlank String titulo,
        @Size(max = 255) String descricao,
        @NotNull LocalDate data,
        @NotNull Long turmaId,
        @Min(1) @Max(100) Integer peso
) {}

