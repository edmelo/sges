package com.sges.sges.turmas;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TurmaRepository extends JpaRepository<Turma, Long> {
    boolean existsByCodigoIgnoreCase(String codigo);
}

