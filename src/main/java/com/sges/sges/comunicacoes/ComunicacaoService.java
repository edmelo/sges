package com.sges.sges.comunicacoes;

import com.sges.sges.alunos.AlunoRepository;
import com.sges.sges.comunicacoes.dto.ComunicacaoRequest;
import com.sges.sges.comunicacoes.dto.ComunicacaoResponse;
import com.sges.sges.turmas.TurmaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ComunicacaoService {

    private final ComunicacaoRepository repository;
    private final TurmaRepository turmaRepository;
    private final AlunoRepository alunoRepository;

    public ComunicacaoService(ComunicacaoRepository repository, TurmaRepository turmaRepository, AlunoRepository alunoRepository) {
        this.repository = repository;
        this.turmaRepository = turmaRepository;
        this.alunoRepository = alunoRepository;
    }

    @Transactional
    public ComunicacaoResponse criar(ComunicacaoRequest r){
        Comunicacao entity = toEntity(r);
        validarDestinatario(entity);
        return toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public ComunicacaoResponse buscarPorId(Long id){
        return repository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Comunicação não encontrada: id="+id));
    }

    @Transactional(readOnly = true)
    public List<ComunicacaoResponse> listar(Long turmaId, Long alunoId){
        if(turmaId!=null) return repository.findByTurma_Id(turmaId).stream().map(this::toResponse).toList();
        if(alunoId!=null) return repository.findByAluno_Id(alunoId).stream().map(this::toResponse).toList();
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public ComunicacaoResponse atualizar(Long id, ComunicacaoRequest r){
        Comunicacao existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comunicação não encontrada: id="+id));
        aplicar(existente, r);
        validarDestinatario(existente);
        return toResponse(repository.save(existente));
    }

    @Transactional
    public void deletar(Long id){
        if(!repository.existsById(id)){
            throw new EntityNotFoundException("Comunicação não encontrada: id="+id);
        }
        repository.deleteById(id);
    }

    private void aplicar(Comunicacao c, ComunicacaoRequest r){
        c.setTitulo(r.titulo());
        c.setConteudo(r.conteudo());
        c.setData(r.data());
        c.setAutor(r.autor());
        c.setTurma(r.turmaId()!=null ? turmaRepository.findById(r.turmaId())
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada: id="+r.turmaId())) : null);
        c.setAluno(r.alunoId()!=null ? alunoRepository.findById(r.alunoId())
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado: id="+r.alunoId())) : null);
    }

    private Comunicacao toEntity(ComunicacaoRequest r){
        Comunicacao c = new Comunicacao();
        aplicar(c, r);
        return c;
    }

    private void validarDestinatario(Comunicacao c){
        if(c.getTurma()==null && c.getAluno()==null){
            throw new jakarta.validation.ConstraintViolationException("Informe um alunoId ou turmaId como destinatário.", java.util.Set.of());
        }
    }

    private ComunicacaoResponse toResponse(Comunicacao c){
        return new ComunicacaoResponse(
                c.getId(), c.getTitulo(), c.getConteudo(), c.getData(),
                c.getAutor(), c.getTurma()!=null? c.getTurma().getId(): null,
                c.getAluno()!=null? c.getAluno().getId(): null
        );
    }
}

