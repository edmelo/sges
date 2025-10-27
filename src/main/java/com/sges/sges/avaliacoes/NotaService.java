package com.sges.sges.avaliacoes;

import com.sges.sges.alunos.Aluno;
import com.sges.sges.alunos.AlunoRepository;
import com.sges.sges.avaliacoes.dto.NotaRequest;
import com.sges.sges.avaliacoes.dto.NotaResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotaService {

    private final NotaRepository notaRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final AlunoRepository alunoRepository;

    public NotaService(NotaRepository notaRepository, AvaliacaoRepository avaliacaoRepository, AlunoRepository alunoRepository) {
        this.notaRepository = notaRepository;
        this.avaliacaoRepository = avaliacaoRepository;
        this.alunoRepository = alunoRepository;
    }

    @Transactional
    public NotaResponse criar(Long avaliacaoId, NotaRequest request){
        Avaliacao avaliacao = avaliacaoRepository.findById(avaliacaoId)
                .orElseThrow(() -> new EntityNotFoundException("Avaliação não encontrada: id=" + avaliacaoId));
        Aluno aluno = alunoRepository.findById(request.alunoId())
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado: id=" + request.alunoId()));

        if (notaRepository.existsByAvaliacao_IdAndAluno_Id(avaliacaoId, request.alunoId())){
            throw new DataIntegrityViolationException("Aluno já possui nota nesta avaliação");
        }

        Nota n = Nota.builder()
                .avaliacao(avaliacao)
                .aluno(aluno)
                .valor(request.valor())
                .observacao(request.observacao())
                .build();
        Nota salvo = notaRepository.save(n);
        return toResponse(salvo);
    }

    @Transactional(readOnly = true)
    public List<NotaResponse> listar(Long avaliacaoId){
        return notaRepository.findByAvaliacao_Id(avaliacaoId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public NotaResponse atualizar(Long avaliacaoId, Long notaId, NotaRequest request){
        Nota existente = notaRepository.findById(notaId)
                .orElseThrow(() -> new EntityNotFoundException("Nota não encontrada: id=" + notaId));
        if (!existente.getAvaliacao().getId().equals(avaliacaoId)){
            throw new EntityNotFoundException("Nota não pertence à avaliação informada");
        }
        // se trocar aluno, verificar duplicidade
        if (!existente.getAluno().getId().equals(request.alunoId()) &&
                notaRepository.existsByAvaliacao_IdAndAluno_Id(avaliacaoId, request.alunoId())){
            throw new DataIntegrityViolationException("Aluno já possui nota nesta avaliação");
        }
        Aluno aluno = alunoRepository.findById(request.alunoId())
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado: id=" + request.alunoId()));

        existente.setAluno(aluno);
        existente.setValor(request.valor());
        existente.setObservacao(request.observacao());

        Nota salvo = notaRepository.save(existente);
        return toResponse(salvo);
    }

    @Transactional
    public void deletar(Long avaliacaoId, Long notaId){
        Nota existente = notaRepository.findById(notaId)
                .orElseThrow(() -> new EntityNotFoundException("Nota não encontrada: id=" + notaId));
        if (!existente.getAvaliacao().getId().equals(avaliacaoId)){
            throw new EntityNotFoundException("Nota não pertence à avaliação informada");
        }
        notaRepository.delete(existente);
    }

    private NotaResponse toResponse(Nota n){
        return new NotaResponse(
                n.getId(),
                n.getAvaliacao() != null ? n.getAvaliacao().getId() : null,
                n.getAluno() != null ? n.getAluno().getId() : null,
                n.getValor(),
                n.getObservacao()
        );
    }
}

