package com.sges.sges.avaliacoes;

import com.sges.sges.turmas.Turma;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "avaliacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String titulo;

    @Size(max = 255)
    @Column(length = 255)
    private String descricao;

    @NotNull
    @Column(nullable = false)
    private LocalDate data;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "turma_id", nullable = false, foreignKey = @ForeignKey(name = "fk_avaliacoes_turma"))
    private Turma turma;

    @Min(1)
    @Max(100)
    @Column
    private Integer peso;
}

