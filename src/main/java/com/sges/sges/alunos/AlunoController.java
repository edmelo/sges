package com.sges.sges.alunos;

import com.sges.sges.alunos.dto.AlunoRequest;
import com.sges.sges.alunos.dto.AlunoResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/alunos")
public class AlunoController {

    private final AlunoService service;

    public AlunoController(AlunoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AlunoResponse> criar(@Valid @RequestBody AlunoRequest request) {
        AlunoResponse criado = service.criar(request);
        return ResponseEntity.created(URI.create("/api/alunos/" + criado.id())).body(criado);
    }

    @GetMapping("/{id}")
    public AlunoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @GetMapping
    public List<AlunoResponse> listar() {
        return service.listarTodos();
    }

    @PutMapping("/{id}")
    public AlunoResponse atualizar(@PathVariable Long id, @Valid @RequestBody AlunoRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

