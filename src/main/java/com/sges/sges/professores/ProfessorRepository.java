package com.sges.sges.professores;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    Optional<Professor> findByRegistroIgnoreCase(String registro);
    boolean existsByRegistroIgnoreCase(String registro);
}
