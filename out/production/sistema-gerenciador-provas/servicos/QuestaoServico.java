package servicos;

import interfaces.QuestaoRepositorio;
import modelos.Questao;

import java.util.List;

public class QuestaoServico {

    private final QuestaoRepositorio questaoRepositorio;

    public QuestaoServico(QuestaoRepositorio questaoRepositorio) {
        this.questaoRepositorio = questaoRepositorio;
    }

    public Questao cadastrarQuestao(
            String titulo,
            String descricao,
            String alternativas,
            String alternativaCorreta
    ) {
        long proximoNumero = questaoRepositorio.findAll()
                .stream()
                .mapToLong(Questao::getNumero)
                .max()
                .orElse(0) + 1;

        Questao questao = new Questao(
                proximoNumero,
                titulo,
                descricao,
                alternativas,
                alternativaCorreta
        );

        return questaoRepositorio.save(questao);
    }

    public List<Questao> listarQuestoes() {
        return questaoRepositorio.findAll();
    }
}