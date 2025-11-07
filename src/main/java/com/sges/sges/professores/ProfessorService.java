package com.sges.sges.professores;

import com.sges.sges.professores.dto.ProfessorRequest;
import com.sges.sges.professores.dto.ProfessorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProfessorService {

    private final ProfessorRepository repository;

    public ProfessorService(ProfessorRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ProfessorResponse criar(ProfessorRequest request) {
        if (repository.existsByRegistroIgnoreCase(request.registro())) {
            throw new DataIntegrityViolationException("Registro já cadastrado: " + request.registro());
        }
        Professor entity = toEntity(request);
        Professor salvo = repository.save(entity);
        return toResponse(salvo);
    }

    @Transactional(readOnly = true)
    public ProfessorResponse buscarPorId(Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Professor não encontrado: id=" + id));
    }

    @Transactional(readOnly = true)
    public List<ProfessorResponse> listarTodos() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public ProfessorResponse atualizar(Long id, ProfessorRequest request) {
        Professor existente = repository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Professor não encontrado: id=" + id));

        if (!existente.getRegistro().equalsIgnoreCase(request.registro())
                && repository.existsByRegistroIgnoreCase(request.registro())) {
            throw new DataIntegrityViolationException("Registro já cadastrado: " + request.registro());
        }

        existente.setNome(request.nome());
        existente.setRegistro(request.registro());
        existente.setDataNascimento(request.dataNascimento());
        existente.setEmail(request.email());
        existente.setTelefone(request.telefone());

        Professor salvo = repository.save(existente);
        return toResponse(salvo);
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new jakarta.persistence.EntityNotFoundException("Professor não encontrado: id=" + id);
        }
        repository.deleteById(id);
    }

    private Professor toEntity(ProfessorRequest r) {
        return Professor.builder()
                .nome(r.nome())
                .registro(r.registro())
                .dataNascimento(r.dataNascimento())
                .email(r.email())
                .telefone(r.telefone())
                .build();
    }

    private ProfessorResponse toResponse(Professor p) {
        return new ProfessorResponse(
                p.getId(),
                p.getNome(),
                p.getRegistro(),
                p.getDataNascimento(),
                p.getEmail(),
                p.getTelefone()
        );
    }
}

