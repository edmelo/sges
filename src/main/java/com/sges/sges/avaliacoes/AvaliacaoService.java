package com.sges.sges.avaliacoes;

import com.sges.sges.avaliacoes.dto.AvaliacaoRequest;
import com.sges.sges.avaliacoes.dto.AvaliacaoResponse;
import com.sges.sges.turmas.Turma;
import com.sges.sges.turmas.TurmaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final TurmaRepository turmaRepository;
    private final NotaRepository notaRepository;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository, TurmaRepository turmaRepository, NotaRepository notaRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.turmaRepository = turmaRepository;
        this.notaRepository = notaRepository;
    }

    @Transactional
    public AvaliacaoResponse criar(AvaliacaoRequest request) {
        Turma turma = turmaRepository.findById(request.turmaId())
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada: id=" + request.turmaId()));

        Avaliacao a = Avaliacao.builder()
                .titulo(request.titulo())
                .descricao(request.descricao())
                .data(request.data())
                .turma(turma)
                .peso(request.peso())
                .build();
        Avaliacao salvo = avaliacaoRepository.save(a);
        return toResponse(salvo);
    }

    @Transactional(readOnly = true)
    public AvaliacaoResponse buscarPorId(Long id) {
        Avaliacao a = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Avaliação não encontrada: id=" + id));
        return toResponse(a);
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoResponse> listar(Long turmaId) {
        List<Avaliacao> list = turmaId == null ? avaliacaoRepository.findAll() : avaliacaoRepository.findByTurma_Id(turmaId);
        return list.stream().map(this::toResponse).toList();
    }

    @Transactional
    public AvaliacaoResponse atualizar(Long id, AvaliacaoRequest request) {
        Avaliacao existente = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Avaliação não encontrada: id=" + id));
        Turma turma = turmaRepository.findById(request.turmaId())
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada: id=" + request.turmaId()));

        existente.setTitulo(request.titulo());
        existente.setDescricao(request.descricao());
        existente.setData(request.data());
        existente.setTurma(turma);
        existente.setPeso(request.peso());

        Avaliacao salvo = avaliacaoRepository.save(existente);
        return toResponse(salvo);
    }

    @Transactional
    public void deletar(Long id) {
        Avaliacao existente = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Avaliação não encontrada: id=" + id));
        if (!notaRepository.findByAvaliacao_Id(id).isEmpty()) {
            throw new DataIntegrityViolationException("Avaliação possui notas e não pode ser excluída");
        }
        avaliacaoRepository.delete(existente);
    }

    private AvaliacaoResponse toResponse(Avaliacao a){
        return new AvaliacaoResponse(
                a.getId(),
                a.getTitulo(),
                a.getDescricao(),
                a.getData(),
                a.getTurma() != null ? a.getTurma().getId() : null,
                a.getPeso()
        );
    }
}

