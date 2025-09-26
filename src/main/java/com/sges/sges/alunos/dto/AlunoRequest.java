package com.sges.sges.alunos.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record AlunoRequest(
        @NotBlank String nome,
        @NotBlank @Size(max = 30) String matricula,
        @NotNull @Past LocalDate dataNascimento,
        @Email @Size(max = 180) String email,
        @Size(max = 30) String telefone
) {}

