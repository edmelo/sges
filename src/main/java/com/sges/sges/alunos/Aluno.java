package com.sges.sges.alunos;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "alunos", uniqueConstraints = {
        @UniqueConstraint(name = "uk_alunos_matricula", columnNames = {"matricula"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String nome;

    @NotBlank
    @Column(nullable = false, length = 30, unique = true)
    private String matricula;

    @NotNull
    @Past
    @Column(nullable = false)
    private LocalDate dataNascimento;

    @Email
    @Column(length = 180)
    private String email;

    @Column(length = 30)
    private String telefone;
}

