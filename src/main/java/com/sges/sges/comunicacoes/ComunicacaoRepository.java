package com.sges.sges.comunicacoes;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ComunicacaoRepository extends JpaRepository<Comunicacao, Long> {
    List<Comunicacao> findByTurma_Id(Long turmaId);
    List<Comunicacao> findByAluno_Id(Long alunoId);
    List<Comunicacao> findByDataBetween(LocalDate inicio, LocalDate fim);
    List<Comunicacao> findByTurma_IdAndDataBetween(Long turmaId, LocalDate inicio, LocalDate fim);
    List<Comunicacao> findByAluno_IdAndDataBetween(Long alunoId, LocalDate inicio, LocalDate fim);
}

