package com.sges.sges.frequencias.dto;

import com.sges.sges.frequencias.StatusFrequencia;

import java.time.LocalDate;

public record FrequenciaResponse(
        Long id,
        Long turmaId,
        Long alunoId,
        LocalDate data,
        StatusFrequencia status,
        String observacao
) {}

