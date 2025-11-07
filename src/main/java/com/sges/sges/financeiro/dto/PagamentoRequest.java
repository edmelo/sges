package com.sges.sges.financeiro.dto;

import com.sges.sges.financeiro.StatusPagamento;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PagamentoRequest(
        @NotNull Long alunoId,
        @NotBlank String referencia,
        @NotNull @Positive BigDecimal valor,
        @NotNull LocalDate vencimento,
        LocalDate dataPagamento,
        @NotNull StatusPagamento status,
        @Size(max = 255) String observacao
) {}

