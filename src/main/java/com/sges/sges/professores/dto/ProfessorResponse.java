package com.sges.sges.professores.dto;

import java.time.LocalDate;

public record ProfessorResponse(
        Long id,
        String nome,
        String registro,
        LocalDate dataNascimento,
        String email,
        String telefone
) {}

