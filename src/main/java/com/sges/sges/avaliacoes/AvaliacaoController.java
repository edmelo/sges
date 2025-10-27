package com.sges.sges.avaliacoes;

import com.sges.sges.avaliacoes.dto.AvaliacaoRequest;
import com.sges.sges.avaliacoes.dto.AvaliacaoResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/avaliacoes")
public class AvaliacaoController {

    private final AvaliacaoService service;

    public AvaliacaoController(AvaliacaoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AvaliacaoResponse> criar(@Valid @RequestBody AvaliacaoRequest request){
        AvaliacaoResponse r = service.criar(request);
        return ResponseEntity.created(URI.create("/api/avaliacoes/" + r.id())).body(r);
    }

    @GetMapping("/{id}")
    public AvaliacaoResponse buscarPorId(@PathVariable Long id){
        return service.buscarPorId(id);
    }

    @GetMapping
    public List<AvaliacaoResponse> listar(@RequestParam(required = false) Long turmaId){
        return service.listar(turmaId);
    }

    @PutMapping("/{id}")
    public AvaliacaoResponse atualizar(@PathVariable Long id, @Valid @RequestBody AvaliacaoRequest request){
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id){
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

