package com.sges.sges.financeiro;

import com.sges.sges.financeiro.dto.PagamentoRequest;
import com.sges.sges.financeiro.dto.PagamentoResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {

    private final PagamentoService service;

    public PagamentoController(PagamentoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PagamentoResponse> criar(@Valid @RequestBody PagamentoRequest request) {
        PagamentoResponse r = service.criar(request);
        return ResponseEntity.created(URI.create("/api/pagamentos/" + r.id())).body(r);
    }

    @GetMapping("/{id}")
    public PagamentoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @GetMapping
    public List<PagamentoResponse> listar(@RequestParam(required = false) Long alunoId) {
        return service.listar(alunoId);
    }

    @PutMapping("/{id}")
    public PagamentoResponse atualizar(@PathVariable Long id, @Valid @RequestBody PagamentoRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

