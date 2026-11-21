package servicos;

import interfaces.QuestaoRepositorio;
import modelos.Questao;

import java.util.List;

public class QuestaoServico {
    public static final int MIN_ENUNCIADO = 5;
    public static final int MAX_ENUNCIADO = 500;
    public static final int MIN_ALTERNATIVAS = 4;
    public static final int MAX_ALTERNATIVAS = 5;
    public static final int MAX_TEXTO_ALTERNATIVA = 300;

    private final QuestaoRepositorio questaoRepositorio;

    public QuestaoServico(QuestaoRepositorio questaoRepositorio) { this.questaoRepositorio = questaoRepositorio; }

    public Questao salvarQuestao(Questao questao) {
        if (questao.getNumero() <= 0) {
            long proximo = questaoRepositorio.findAll().stream().mapToLong(Questao::getNumero).max().orElse(0) + 1;
            questao.setNumero(proximo);
        }
        return questaoRepositorio.save(questao);
    }

    public Questao buscarQuestao(long id) { return questaoRepositorio.findByKey(id); }
    public List<Questao> listarQuestoes() { return questaoRepositorio.findAll(); }
    // Só é possível excluir questões que não estão em nenhuma prova (PB19)
    public void excluirQuestao(long id) {
        if (questaoRepositorio.estaEmAlgumaProva(id))
            throw new IllegalArgumentException("Esta questão está vinculada a uma ou mais provas e não pode ser excluída. "
                    + "Remova-a das provas em Questões e pesos antes de excluí-la.");
        questaoRepositorio.remove(id);
    }

    public boolean estaEmAlgumaProva(long id) { return questaoRepositorio.estaEmAlgumaProva(id); }
}
