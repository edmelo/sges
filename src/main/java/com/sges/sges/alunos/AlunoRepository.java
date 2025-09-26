package com.sges.sges.alunos;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    Optional<Aluno> findByMatriculaIgnoreCase(String matricula);
    boolean existsByMatriculaIgnoreCase(String matricula);
}

