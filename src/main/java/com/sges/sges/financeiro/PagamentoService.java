package com.sges.sges.financeiro;

import com.sges.sges.alunos.Aluno;
import com.sges.sges.alunos.AlunoRepository;
import com.sges.sges.financeiro.dto.PagamentoRequest;
import com.sges.sges.financeiro.dto.PagamentoResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final AlunoRepository alunoRepository;

    public PagamentoService(PagamentoRepository pagamentoRepository, AlunoRepository alunoRepository) {
        this.pagamentoRepository = pagamentoRepository;
        this.alunoRepository = alunoRepository;
    }

    @Transactional
    public PagamentoResponse criar(PagamentoRequest request) {
        Aluno aluno = alunoRepository.findById(request.alunoId())
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado: id=" + request.alunoId()));

        boolean duplicado = pagamentoRepository.findByAluno_Id(aluno.getId()).stream()
                .anyMatch(p -> p.getReferencia().equalsIgnoreCase(request.referencia()));
        if (duplicado) {
            throw new DataIntegrityViolationException("Já existe pagamento para a referência informada");
        }

        Pagamento p = Pagamento.builder()
                .aluno(aluno)
                .referencia(request.referencia())
                .valor(request.valor())
                .vencimento(request.vencimento())
                .dataPagamento(request.dataPagamento())
                .status(request.status())
                .observacao(request.observacao())
                .build();
        Pagamento salvo = pagamentoRepository.save(p);
        return toResponse(salvo);
    }

    @Transactional(readOnly = true)
    public PagamentoResponse buscarPorId(Long id) {
        Pagamento p = pagamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pagamento não encontrado: id=" + id));
        return toResponse(p);
    }

    @Transactional(readOnly = true)
    public List<PagamentoResponse> listar(Long alunoId) {
        List<Pagamento> list = alunoId == null ? pagamentoRepository.findAll() : pagamentoRepository.findByAluno_Id(alunoId);
        return list.stream().map(this::toResponse).toList();
    }

    @Transactional
    public PagamentoResponse atualizar(Long id, PagamentoRequest request) {
        Pagamento existente = pagamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pagamento não encontrado: id=" + id));
        Aluno aluno = alunoRepository.findById(request.alunoId())
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado: id=" + request.alunoId()));

        // Checa duplicidade de referencia por aluno excluindo o próprio registro
        boolean duplicado = pagamentoRepository.findByAluno_Id(aluno.getId()).stream()
                .anyMatch(p -> !p.getId().equals(id) && p.getReferencia().equalsIgnoreCase(request.referencia()));
        if (duplicado) {
            throw new DataIntegrityViolationException("Já existe pagamento para a referência informada");
        }

        existente.setAluno(aluno);
        existente.setReferencia(request.referencia());
        existente.setValor(request.valor());
        existente.setVencimento(request.vencimento());
        existente.setDataPagamento(request.dataPagamento());
        existente.setStatus(request.status());
        existente.setObservacao(request.observacao());

        Pagamento salvo = pagamentoRepository.save(existente);
        return toResponse(salvo);
    }

    @Transactional
    public void deletar(Long id) {
        Pagamento existente = pagamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pagamento não encontrado: id=" + id));
        pagamentoRepository.delete(existente);
    }

    private PagamentoResponse toResponse(Pagamento p) {
        return new PagamentoResponse(
                p.getId(),
                p.getAluno() != null ? p.getAluno().getId() : null,
                p.getReferencia(),
                p.getValor(),
                p.getVencimento(),
                p.getDataPagamento(),
                p.getStatus(),
                p.getObservacao()
        );
    }
}

