package com.sges.sges.financeiro;

import com.sges.sges.alunos.Aluno;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pagamentos", uniqueConstraints = {
        @UniqueConstraint(name = "uk_pagamentos_aluno_referencia", columnNames = {"aluno_id", "referencia"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false, foreignKey = @ForeignKey(name = "fk_pagamentos_aluno"))
    private Aluno aluno;

    @NotBlank
    @Column(nullable = false, length = 20)
    private String referencia; // ex: MES/ANO ou COD FATURA

    @NotNull
    @Positive
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @NotNull
    @Column(nullable = false)
    private LocalDate vencimento;

    @Column
    private LocalDate dataPagamento;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private StatusPagamento status;

    @Size(max = 255)
    @Column(length = 255)
    private String observacao;
}

