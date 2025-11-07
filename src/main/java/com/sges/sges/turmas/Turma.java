package com.sges.sges.turmas;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "turmas", uniqueConstraints = {
        @UniqueConstraint(name = "uk_turmas_codigo", columnNames = {"codigo"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Turma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String nome;

    @NotBlank
    @Column(nullable = false, length = 30, unique = true)
    private String codigo;

    @Size(max = 255)
    @Column(length = 255)
    private String descricao;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Integer capacidade;
}

