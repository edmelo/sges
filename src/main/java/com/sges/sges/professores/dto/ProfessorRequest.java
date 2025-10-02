package com.sges.sges.professores.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record ProfessorRequest(
        @NotBlank String nome,
        @NotBlank @Size(max = 30) String registro,
        @NotNull @Past LocalDate dataNascimento,
        @Email @Size(max = 180) String email,
        @Size(max = 30) String telefone
) {}

