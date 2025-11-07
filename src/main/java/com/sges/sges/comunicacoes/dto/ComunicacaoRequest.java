package com.sges.sges.comunicacoes.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record ComunicacaoRequest(
        @NotBlank String titulo,
        @NotBlank @Size(max = 2000) String conteudo,
        @NotNull LocalDate data,
        @Size(max = 120) String autor,
        Long turmaId,
        Long alunoId
) {}

