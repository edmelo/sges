package com.sges.sges.turmas;

import com.sges.sges.turmas.dto.TurmaRequest;
import com.sges.sges.turmas.dto.TurmaResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TurmaService {

    private final TurmaRepository repository;

    public TurmaService(TurmaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TurmaResponse criar(TurmaRequest request) {
        if (repository.existsByCodigoIgnoreCase(request.codigo())) {
            throw new DataIntegrityViolationException("Código de turma já cadastrado: " + request.codigo());
        }
        Turma entity = toEntity(request);
        Turma salvo = repository.save(entity);
        return toResponse(salvo);
    }

    @Transactional(readOnly = true)
    public TurmaResponse buscarPorId(Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Turma não encontrada: id=" + id));
    }

    @Transactional(readOnly = true)
    public List<TurmaResponse> listarTodos() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public TurmaResponse atualizar(Long id, TurmaRequest request) {
        Turma existente = repository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Turma não encontrada: id=" + id));

        if (!existente.getCodigo().equalsIgnoreCase(request.codigo())
                && repository.existsByCodigoIgnoreCase(request.codigo())) {
            throw new DataIntegrityViolationException("Código de turma já cadastrado: " + request.codigo());
        }

        existente.setNome(request.nome());
        existente.setCodigo(request.codigo());
        existente.setDescricao(request.descricao());
        existente.setCapacidade(request.capacidade());

        Turma salvo = repository.save(existente);
        return toResponse(salvo);
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new jakarta.persistence.EntityNotFoundException("Turma não encontrada: id=" + id);
        }
        repository.deleteById(id);
    }

    private Turma toEntity(TurmaRequest r) {
        return Turma.builder()
                .nome(r.nome())
                .codigo(r.codigo())
                .descricao(r.descricao())
                .capacidade(r.capacidade())
                .build();
    }

    private TurmaResponse toResponse(Turma t) {
        return new TurmaResponse(
                t.getId(),
                t.getNome(),
                t.getCodigo(),
                t.getDescricao(),
                t.getCapacidade()
        );
    }
}

