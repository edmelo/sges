package com.sges.sges.frequencias;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FrequenciaRepository extends JpaRepository<Frequencia, Long> {
    boolean existsByTurma_IdAndAluno_IdAndData(Long turmaId, Long alunoId, LocalDate data);
    Optional<Frequencia> findByTurma_IdAndAluno_IdAndData(Long turmaId, Long alunoId, LocalDate data);
    List<Frequencia> findByTurma_IdAndData(Long turmaId, LocalDate data);
    List<Frequencia> findByAluno_IdAndDataBetween(Long alunoId, LocalDate inicio, LocalDate fim);
}

