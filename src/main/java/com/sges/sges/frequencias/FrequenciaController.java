package com.sges.sges.frequencias;

import com.sges.sges.frequencias.dto.FrequenciaRequest;
import com.sges.sges.frequencias.dto.FrequenciaResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/frequencias")
public class FrequenciaController {

    private final FrequenciaService service;

    public FrequenciaController(FrequenciaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<FrequenciaResponse> criar(@Valid @RequestBody FrequenciaRequest request) {
        FrequenciaResponse r = service.criar(request);
        return ResponseEntity.created(URI.create("/api/frequencias/" + r.id())).body(r);
    }

    @GetMapping("/{id}")
    public FrequenciaResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    // Listagem por turma e data específica
    @GetMapping
    public List<FrequenciaResponse> listarPorTurmaEData(@RequestParam(required = false) Long turmaId,
                                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
                                                        @RequestParam(required = false) Long alunoId,
                                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
                                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        // Se informado turmaId+data -> lista por turma/data, senão se informado alunoId+periodo -> lista por periodo
        if (turmaId != null && data != null) {
            return service.listarPorTurmaData(turmaId, data);
        }
        if (alunoId != null && inicio != null && fim != null) {
            return service.listarPorAlunoPeriodo(alunoId, inicio, fim);
        }
        throw new jakarta.validation.ConstraintViolationException("Parâmetros inválidos para listagem", java.util.Set.of());
    }

    @PutMapping("/{id}")
    public FrequenciaResponse atualizar(@PathVariable Long id, @Valid @RequestBody FrequenciaRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

