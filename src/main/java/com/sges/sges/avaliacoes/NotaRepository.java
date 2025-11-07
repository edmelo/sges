package com.sges.sges.avaliacoes;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotaRepository extends JpaRepository<Nota, Long> {
    boolean existsByAvaliacao_IdAndAluno_Id(Long avaliacaoId, Long alunoId);
    List<Nota> findByAvaliacao_Id(Long avaliacaoId);
    Optional<Nota> findByAvaliacao_IdAndAluno_Id(Long avaliacaoId, Long alunoId);
}

