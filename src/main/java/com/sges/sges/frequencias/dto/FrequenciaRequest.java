package com.sges.sges.frequencias.dto;

import com.sges.sges.frequencias.StatusFrequencia;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record FrequenciaRequest(
        @NotNull Long turmaId,
        @NotNull Long alunoId,
        @NotNull LocalDate data,
        @NotNull StatusFrequencia status,
        @Size(max = 255) String observacao
) {}

