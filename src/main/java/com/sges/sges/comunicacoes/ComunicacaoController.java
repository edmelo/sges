package com.sges.sges.comunicacoes;

import com.sges.sges.comunicacoes.dto.ComunicacaoRequest;
import com.sges.sges.comunicacoes.dto.ComunicacaoResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/comunicacoes")
public class ComunicacaoController {

    private final ComunicacaoService service;

    public ComunicacaoController(ComunicacaoService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<ComunicacaoResponse> criar(@Valid @RequestBody ComunicacaoRequest req){
        ComunicacaoResponse r = service.criar(req);
        return ResponseEntity.created(URI.create("/api/comunicacoes/"+r.id())).body(r);
    }

    @GetMapping("/{id}")
    public ComunicacaoResponse buscar(@PathVariable Long id){ return service.buscarPorId(id); }

    @GetMapping
    public List<ComunicacaoResponse> listar(@RequestParam(required = false) Long turmaId,
                                            @RequestParam(required = false) Long alunoId){
        return service.listar(turmaId, alunoId);
    }

    @PutMapping("/{id}")
    public ComunicacaoResponse atualizar(@PathVariable Long id, @Valid @RequestBody ComunicacaoRequest req){
        return service.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id){
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

