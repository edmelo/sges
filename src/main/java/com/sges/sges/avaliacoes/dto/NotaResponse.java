package com.sges.sges.avaliacoes.dto;

import java.math.BigDecimal;

public record NotaResponse(
        Long id,
        Long avaliacaoId,
        Long alunoId,
        BigDecimal valor,
        String observacao
) {}

