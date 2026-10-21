package servicos;

import interfaces.QuestaoRepositorio;
import modelos.Alternativa;
import modelos.Questao;

import java.util.List;

public class QuestaoServico {
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
    public void excluirQuestao(long id) { questaoRepositorio.remove(id); }

    private void validar(Questao questao) {
        if (questao.getTitulo() == null || questao.getTitulo().isBlank() || questao.getTitulo().length() > 200)
            throw new IllegalArgumentException("O enunciado é obrigatório e deve ter até 200 caracteres.");
        if (questao.getDescricao() == null || questao.getDescricao().isBlank() || questao.getDescricao().length() > 300)
            throw new IllegalArgumentException("A descrição é obrigatória e deve ter até 300 caracteres.");
        List<Alternativa> alternativas = questao.getAlternativas();
        if (alternativas.size() < 3) throw new IllegalArgumentException("Adicione pelo menos 3 alternativas.");
        if (alternativas.stream().filter(Alternativa::isCorreta).count() != 1)
            throw new IllegalArgumentException("Marque exatamente uma alternativa correta.");
        if (alternativas.stream().anyMatch(a -> a.getTexto() == null || a.getTexto().isBlank() || a.getTexto().length() > 100))
            throw new IllegalArgumentException("Cada alternativa deve ter texto com até 100 caracteres.");
    }
}
