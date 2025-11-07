package com.sges.sges.financeiro;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    List<Pagamento> findByAluno_Id(Long alunoId);
    List<Pagamento> findByStatus(StatusPagamento status);
    List<Pagamento> findByVencimentoBetween(LocalDate inicio, LocalDate fim);
}

