package com.sges.sges.avaliacoes;

import com.sges.sges.alunos.Aluno;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "notas", uniqueConstraints = {
        @UniqueConstraint(name = "uk_notas_avaliacao_aluno", columnNames = {"avaliacao_id", "aluno_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Nota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "avaliacao_id", nullable = false, foreignKey = @ForeignKey(name = "fk_notas_avaliacao"))
    private Avaliacao avaliacao;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false, foreignKey = @ForeignKey(name = "fk_notas_aluno"))
    private Aluno aluno;

    @NotNull
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "10.0")
    @Digits(integer = 2, fraction = 2)
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal valor;

    @Size(max = 255)
    @Column(length = 255)
    private String observacao;
}

