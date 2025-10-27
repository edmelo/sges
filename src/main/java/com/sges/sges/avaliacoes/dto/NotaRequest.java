package com.sges.sges.avaliacoes.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record NotaRequest(
        @NotNull Long alunoId,
        @NotNull @DecimalMin("0.0") @DecimalMax("10.0") @Digits(integer = 2, fraction = 2) BigDecimal valor,
        @Size(max = 255) String observacao
) {}

