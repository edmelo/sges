package com.sges.sges.alunos;

import com.sges.sges.alunos.dto.AlunoRequest;
import com.sges.sges.alunos.dto.AlunoResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlunoService {

    private final AlunoRepository repository;

    public AlunoService(AlunoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public AlunoResponse criar(AlunoRequest request) {
        // Verifica duplicidade de matrícula de forma pró-ativa
        if (repository.existsByMatriculaIgnoreCase(request.matricula())) {
            throw new DataIntegrityViolationException("Matrícula já cadastrada: " + request.matricula());
        }
        Aluno aluno = toEntity(request);
        Aluno salvo = repository.save(aluno);
        return toResponse(salvo);
    }

    @Transactional(readOnly = true)
    public AlunoResponse buscarPorId(Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Aluno não encontrado: id=" + id));
    }

    @Transactional(readOnly = true)
    public List<AlunoResponse> listarTodos() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public AlunoResponse atualizar(Long id, AlunoRequest request) {
        Aluno existente = repository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Aluno não encontrado: id=" + id));

        // Se matrícula mudou, valida duplicidade
        if (!existente.getMatricula().equalsIgnoreCase(request.matricula())
                && repository.existsByMatriculaIgnoreCase(request.matricula())) {
            throw new DataIntegrityViolationException("Matrícula já cadastrada: " + request.matricula());
        }

        existente.setNome(request.nome());
        existente.setMatricula(request.matricula());
        existente.setDataNascimento(request.dataNascimento());
        existente.setEmail(request.email());
        existente.setTelefone(request.telefone());

        Aluno salvo = repository.save(existente);
        return toResponse(salvo);
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new jakarta.persistence.EntityNotFoundException("Aluno não encontrado: id=" + id);
        }
        repository.deleteById(id);
    }

    private Aluno toEntity(AlunoRequest r) {
        return Aluno.builder()
                .nome(r.nome())
                .matricula(r.matricula())
                .dataNascimento(r.dataNascimento())
                .email(r.email())
                .telefone(r.telefone())
                .build();
    }

    private AlunoResponse toResponse(Aluno a) {
        return new AlunoResponse(
                a.getId(),
                a.getNome(),
                a.getMatricula(),
                a.getDataNascimento(),
                a.getEmail(),
                a.getTelefone()
        );
    }
}

