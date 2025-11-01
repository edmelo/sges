package com.sges.sges.relatorios;

import com.sges.sges.alunos.Aluno;
import com.sges.sges.alunos.AlunoRepository;
import com.sges.sges.frequencias.Frequencia;
import com.sges.sges.frequencias.FrequenciaRepository;
import com.sges.sges.turmas.Turma;
import com.sges.sges.turmas.TurmaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RelatorioService {

    private final FrequenciaRepository frequenciaRepository;
    private final TurmaRepository turmaRepository;
    private final AlunoRepository alunoRepository;

    public RelatorioService(FrequenciaRepository frequenciaRepository, TurmaRepository turmaRepository, AlunoRepository alunoRepository) {
        this.frequenciaRepository = frequenciaRepository;
        this.turmaRepository = turmaRepository;
        this.alunoRepository = alunoRepository;
    }

    @Transactional(readOnly = true)
    public String gerarTaxaPresencaPorAlunoCsv(Long turmaId, LocalDate inicio, LocalDate fim) {
        Turma turma = turmaRepository.findById(turmaId)
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada: id=" + turmaId));

        List<Frequencia> lista = frequenciaRepository.findByTurma_IdAndDataBetween(turmaId, inicio, fim);

        // agrupar por aluno e calcular totais
        Map<Aluno, List<Frequencia>> porAluno = lista.stream().collect(Collectors.groupingBy(Frequencia::getAluno));

        // CSV header
        StringBuilder sb = new StringBuilder();
        sb.append("matricula,nome,total_registros,presentes,ausentes,atrasos,justificados,taxa_presenca\n");

        for (Map.Entry<Aluno, List<Frequencia>> e : porAluno.entrySet()) {
            Aluno aluno = e.getKey();
            List<Frequencia> freqs = e.getValue();
            long total = freqs.size();
            long presentes = freqs.stream().filter(f -> f.getStatus().name().equals("PRESENTE")).count();
            long ausentes = freqs.stream().filter(f -> f.getStatus().name().equals("AUSENTE")).count();
            long atrasos = freqs.stream().filter(f -> f.getStatus().name().equals("ATRASO")).count();
            long justificados = freqs.stream().filter(f -> f.getStatus().name().equals("JUSTIFICADO")).count();
            double taxa = total == 0 ? 0.0 : (presentes * 100.0) / total;
            sb.append(String.format("%s,%s,%d,%d,%d,%d,%d,%.2f\n",
                    aluno.getMatricula(), escapeCsv(aluno.getNome()), total, presentes, ausentes, atrasos, justificados, taxa));
        }

        return sb.toString();
    }

    private String escapeCsv(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\n") || s.contains("\r") || s.contains("\"") ) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
}

