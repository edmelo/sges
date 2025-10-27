package com.sges.sges.avaliacoes;

import com.sges.sges.avaliacoes.dto.NotaRequest;
import com.sges.sges.avaliacoes.dto.NotaResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/avaliacoes/{avaliacaoId}/notas")
public class NotaController {

    private final NotaService service;

    public NotaController(NotaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<NotaResponse> criar(@PathVariable Long avaliacaoId, @Valid @RequestBody NotaRequest request){
        NotaResponse r = service.criar(avaliacaoId, request);
        return ResponseEntity.created(URI.create("/api/avaliacoes/" + avaliacaoId + "/notas/" + r.id())).body(r);
    }

    @GetMapping
    public List<NotaResponse> listar(@PathVariable Long avaliacaoId){
        return service.listar(avaliacaoId);
    }

    @PutMapping("/{notaId}")
    public NotaResponse atualizar(@PathVariable Long avaliacaoId, @PathVariable Long notaId, @Valid @RequestBody NotaRequest request){
        return service.atualizar(avaliacaoId, notaId, request);
    }

    @DeleteMapping("/{notaId}")
    public ResponseEntity<Void> deletar(@PathVariable Long avaliacaoId, @PathVariable Long notaId){
        service.deletar(avaliacaoId, notaId);
        return ResponseEntity.noContent().build();
    }
}

