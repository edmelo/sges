package com.sges.sges.frequencias;

import com.sges.sges.alunos.Aluno;
import com.sges.sges.alunos.AlunoRepository;
import com.sges.sges.frequencias.dto.FrequenciaRequest;
import com.sges.sges.frequencias.dto.FrequenciaResponse;
import com.sges.sges.turmas.Turma;
import com.sges.sges.turmas.TurmaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class FrequenciaService {

    private final FrequenciaRepository repository;
    private final TurmaRepository turmaRepository;
    private final AlunoRepository alunoRepository;

    public FrequenciaService(FrequenciaRepository repository, TurmaRepository turmaRepository, AlunoRepository alunoRepository) {
        this.repository = repository;
        this.turmaRepository = turmaRepository;
        this.alunoRepository = alunoRepository;
    }

    @Transactional
    public FrequenciaResponse criar(FrequenciaRequest request) {
        Turma turma = turmaRepository.findById(request.turmaId())
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada: id=" + request.turmaId()));
        Aluno aluno = alunoRepository.findById(request.alunoId())
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado: id=" + request.alunoId()));

        if (repository.existsByTurma_IdAndAluno_IdAndData(request.turmaId(), request.alunoId(), request.data())) {
            throw new DataIntegrityViolationException("Frequência já lançada para o aluno nesta data");
        }

        Frequencia f = Frequencia.builder()
                .turma(turma)
                .aluno(aluno)
                .data(request.data())
                .status(request.status())
                .observacao(request.observacao())
                .build();
        Frequencia salvo = repository.save(f);
        return toResponse(salvo);
    }

    @Transactional(readOnly = true)
    public FrequenciaResponse buscarPorId(Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Frequência não encontrada: id=" + id));
    }

    @Transactional(readOnly = true)
    public List<FrequenciaResponse> listarPorTurmaData(Long turmaId, LocalDate data) {
        return repository.findByTurma_IdAndData(turmaId, data).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<FrequenciaResponse> listarPorAlunoPeriodo(Long alunoId, LocalDate inicio, LocalDate fim) {
        return repository.findByAluno_IdAndDataBetween(alunoId, inicio, fim).stream().map(this::toResponse).toList();
    }

    @Transactional
    public FrequenciaResponse atualizar(Long id, FrequenciaRequest request) {
        Frequencia existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Frequência não encontrada: id=" + id));

        Turma turma = turmaRepository.findById(request.turmaId())
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada: id=" + request.turmaId()));
        Aluno aluno = alunoRepository.findById(request.alunoId())
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado: id=" + request.alunoId()));

        // se alterou a chave natural (turma/aluno/data), validar duplicidade
        boolean mudouChave = !existente.getTurma().getId().equals(request.turmaId())
                || !existente.getAluno().getId().equals(request.alunoId())
                || !existente.getData().equals(request.data());
        if (mudouChave && repository.existsByTurma_IdAndAluno_IdAndData(request.turmaId(), request.alunoId(), request.data())) {
            throw new DataIntegrityViolationException("Frequência já lançada para o aluno nesta data");
        }

        existente.setTurma(turma);
        existente.setAluno(aluno);
        existente.setData(request.data());
        existente.setStatus(request.status());
        existente.setObservacao(request.observacao());

        Frequencia salvo = repository.save(existente);
        return toResponse(salvo);
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Frequência não encontrada: id=" + id);
        }
        repository.deleteById(id);
    }

    private FrequenciaResponse toResponse(Frequencia f) {
        return new FrequenciaResponse(
                f.getId(),
                f.getTurma() != null ? f.getTurma().getId() : null,
                f.getAluno() != null ? f.getAluno().getId() : null,
                f.getData(),
                f.getStatus(),
                f.getObservacao()
        );
    }
}

