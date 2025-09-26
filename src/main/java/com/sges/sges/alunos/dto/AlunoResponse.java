package com.sges.sges.alunos.dto;

import java.time.LocalDate;

public record AlunoResponse(
        Long id,
        String nome,
        String matricula,
        LocalDate dataNascimento,
        String email,
        String telefone
) {}

