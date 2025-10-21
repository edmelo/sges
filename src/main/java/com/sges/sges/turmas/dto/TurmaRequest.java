package com.sges.sges.turmas.dto;

import jakarta.validation.constraints.*;

public record TurmaRequest(
        @NotBlank String nome,
        @NotBlank @Size(max = 30) String codigo,
        @Size(max = 255) String descricao,
        @NotNull @Positive Integer capacidade
) {}

