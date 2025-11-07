package com.sges.sges.comunicacoes;

import com.sges.sges.alunos.Aluno;
import com.sges.sges.turmas.Turma;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "comunicacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comunicacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String titulo;

    @NotBlank
    @Size(max = 2000)
    @Column(nullable = false, length = 2000)
    private String conteudo;

    @NotNull
    @Column(nullable = false)
    private LocalDate data;

    @Size(max = 120)
    @Column(length = 120)
    private String autor;

    // Destinatários: pelo menos um dos dois deve ser informado na camada de serviço
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turma_id", foreignKey = @ForeignKey(name = "fk_comunicacoes_turma"))
    private Turma turma; // opcional

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", foreignKey = @ForeignKey(name = "fk_comunicacoes_aluno"))
    private Aluno aluno; // opcional
}

