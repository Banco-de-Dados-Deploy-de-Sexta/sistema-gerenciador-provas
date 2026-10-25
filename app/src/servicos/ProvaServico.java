package servicos;

import interfaces.ProvaRepositorio;
import modelos.Alternativa;
import modelos.PesoQuestao;
import modelos.Prova;
import modelos.Questao;
import modelos.QuestaoComPeso;
import modelos.ResultadoCorrecao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProvaServico {
    private final ProvaRepositorio provaRepositorio;
    private final QuestaoServico questaoServico;

    public ProvaServico(ProvaRepositorio provaRepositorio, QuestaoServico questaoServico) {
        this.provaRepositorio = provaRepositorio;
        this.questaoServico = questaoServico;
    }

    public Prova criarProva(String titulo, String materia, LocalDate data, String curso, int bimestre) {
        String codigo = gerarCodigo();
        return salvarProva(new Prova(codigo, titulo, materia, data, curso, bimestre));
    }

    public Prova salvarProva(Prova prova) {
        validar(prova);
        if (prova.getCodigo() == null || prova.getCodigo().isBlank()) prova.setCodigo(gerarCodigo());
        return provaRepositorio.save(prova);
    }

    public Prova buscarProva(String codigo) { return provaRepositorio.findByKey(codigo); }
    public List<Prova> listarProvas() { return provaRepositorio.findAll(); }
    public void excluirProva(String codigo) { provaRepositorio.remove(codigo); }

    public List<QuestaoComPeso> listarQuestoes(String codigoProva) {
        List<QuestaoComPeso> resultado = new ArrayList<>();
        for (PesoQuestao vinculo : provaRepositorio.listarQuestoes(codigoProva)) {
            Questao questao = questaoServico.buscarQuestao(vinculo.getIdQuestao());
            if (questao != null) resultado.add(new QuestaoComPeso(questao, vinculo.getPeso()));
        }
        return resultado;
    }

    public int contarQuestoes(String codigoProva) { return provaRepositorio.listarQuestoes(codigoProva).size(); }

    public void salvarQuestoes(String codigoProva, List<PesoQuestao> questoes) {
        if (buscarProva(codigoProva) == null) throw new IllegalArgumentException("A prova não existe.");
        for (PesoQuestao item : questoes) {
            if (questaoServico.buscarQuestao(item.getIdQuestao()) == null)
                throw new IllegalArgumentException("Uma das questões selecionadas não existe.");
            if (!Double.isFinite(item.getPeso()) || item.getPeso() <= 0)
                throw new IllegalArgumentException("O peso deve ser um número maior que zero.");
        }
        provaRepositorio.salvarQuestoes(codigoProva, questoes);
    }

    public ResultadoCorrecao corrigir(String codigoProva, Map<Long, Integer> respostas) {
        List<QuestaoComPeso> questoes = listarQuestoes(codigoProva);
        double possiveis = 0;
        double obtidos = 0;
        for (QuestaoComPeso item : questoes) {
            possiveis += item.getPeso();
            Integer resposta = respostas.get(item.getQuestao().getNumero());
            if (resposta == null) continue;
            boolean correta = item.getQuestao().getAlternativas().stream()
                    .anyMatch(a -> a.getId() == resposta && a.isCorreta());
            if (correta) obtidos += item.getPeso();
        }
        return new ResultadoCorrecao(obtidos, possiveis);
    }

    private String gerarCodigo() {
        int proximo = provaRepositorio.findAll().stream()
                .map(Prova::getCodigo)
                .filter(codigo -> codigo != null && codigo.matches("PRV[0-9]{4}"))
                .mapToInt(codigo -> Integer.parseInt(codigo.substring(3)))
                .max().orElse(0) + 1;
        if (proximo > 9999) throw new IllegalStateException("Limite de provas cadastradas atingido.");
        return "PRV%04d".formatted(proximo);
    }

    private void validar(Prova prova) {
        if (prova.getTitulo() == null || prova.getTitulo().isBlank()) throw new IllegalArgumentException("Informe o título.");
        if (prova.getMateria() == null || prova.getMateria().isBlank() || prova.getMateria().length() > 50)
            throw new IllegalArgumentException("Informe a matéria (até 50 caracteres).");
        if (prova.getCurso() != null && prova.getCurso().length() > 50)
            throw new IllegalArgumentException("O curso deve ter até 50 caracteres.");
        if (prova.getBimestre() < 1 || prova.getBimestre() > 4)
            throw new IllegalArgumentException("O bimestre deve estar entre 1 e 4.");
    }
}
