package com.sges.sges.frequencias;

import com.sges.sges.alunos.Aluno;
import com.sges.sges.turmas.Turma;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "frequencias", uniqueConstraints = {
        @UniqueConstraint(name = "uk_frequencias_turma_aluno_data", columnNames = {"turma_id", "aluno_id", "data"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Frequencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "turma_id", nullable = false, foreignKey = @ForeignKey(name = "fk_frequencias_turma"))
    private Turma turma;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false, foreignKey = @ForeignKey(name = "fk_frequencias_aluno"))
    private Aluno aluno;

    @NotNull
    @Column(nullable = false)
    private LocalDate data;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusFrequencia status;

    @Size(max = 255)
    @Column(length = 255)
    private String observacao;
}

