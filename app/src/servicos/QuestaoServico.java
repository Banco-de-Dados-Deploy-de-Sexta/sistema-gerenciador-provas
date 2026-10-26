package servicos;

import interfaces.QuestaoRepositorio;
import modelos.Alternativa;
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
        validar(questao);
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

    private void validar(Questao questao) {
        String enunciado = questao.getTitulo() == null ? "" : questao.getTitulo().trim();
        if (enunciado.length() < MIN_ENUNCIADO || enunciado.length() > MAX_ENUNCIADO)
            throw new IllegalArgumentException("O enunciado deve ter entre %d e %d caracteres.".formatted(MIN_ENUNCIADO, MAX_ENUNCIADO));
        if (questao.getDescricao() == null || questao.getDescricao().isBlank() || questao.getDescricao().length() > 300)
            throw new IllegalArgumentException("A descrição é obrigatória e deve ter até 300 caracteres.");
        List<Alternativa> alternativas = questao.getAlternativas();
        if (alternativas.size() < MIN_ALTERNATIVAS)
            throw new IllegalArgumentException("Adicione pelo menos %d alternativas.".formatted(MIN_ALTERNATIVAS));
        if (alternativas.size() > MAX_ALTERNATIVAS)
            throw new IllegalArgumentException("A questão pode ter no máximo %d alternativas (a até e).".formatted(MAX_ALTERNATIVAS));
        if (alternativas.stream().anyMatch(a -> a.getTexto() == null || a.getTexto().isBlank()))
            throw new IllegalArgumentException("O texto das alternativas não pode ficar vazio.");
        if (alternativas.stream().anyMatch(a -> a.getTexto().trim().length() > MAX_TEXTO_ALTERNATIVA))
            throw new IllegalArgumentException("Cada alternativa pode ter até %d caracteres.".formatted(MAX_TEXTO_ALTERNATIVA));
        if (alternativas.stream().filter(Alternativa::isCorreta).count() != 1)
            throw new IllegalArgumentException("Marque exatamente uma alternativa correta.");
    }
}
