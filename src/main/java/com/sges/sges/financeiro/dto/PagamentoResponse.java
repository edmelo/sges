package com.sges.sges.financeiro.dto;

import com.sges.sges.financeiro.StatusPagamento;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PagamentoResponse(
        Long id,
        Long alunoId,
        String referencia,
        BigDecimal valor,
        LocalDate vencimento,
        LocalDate dataPagamento,
        StatusPagamento status,
        String observacao
) {}

