package com.sges.sges.professores;

import com.sges.sges.professores.dto.ProfessorRequest;
import com.sges.sges.professores.dto.ProfessorResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/professores")
public class ProfessorController {

    private final ProfessorService service;

    public ProfessorController(ProfessorService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProfessorResponse> criar(@Valid @RequestBody ProfessorRequest request) {
        ProfessorResponse criado = service.criar(request);
        return ResponseEntity.created(URI.create("/api/professores/" + criado.id())).body(criado);
    }

    @GetMapping("/{id}")
    public ProfessorResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @GetMapping
    public List<ProfessorResponse> listar() {
        return service.listarTodos();
    }

    @PutMapping("/{id}")
    public ProfessorResponse atualizar(@PathVariable Long id, @Valid @RequestBody ProfessorRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

