package com.sges.sges.professores;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "professores", uniqueConstraints = {
        @UniqueConstraint(name = "uk_professores_registro", columnNames = {"registro"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String nome;

    // Registro funcional do professor (único na instituição)
    @NotBlank
    @Column(nullable = false, length = 30, unique = true)
    private String registro;

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

