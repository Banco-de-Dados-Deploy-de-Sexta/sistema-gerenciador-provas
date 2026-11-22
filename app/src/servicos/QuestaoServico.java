package servicos;

import interfaces.QuestaoRepositorio;
import modelos.Alternativa;
import modelos.Questao;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class QuestaoServico {
    public static final int MIN_ENUNCIADO = 5;
    public static final int MAX_ENUNCIADO = 500;
    public static final int MIN_ALTERNATIVAS = 4;
    public static final int MAX_ALTERNATIVAS = 5;
    public static final int MAX_TEXTO_ALTERNATIVA = 300;
    public static final int MAX_TIPO = 300;

    public static final String TIPO_MULTIPLA_ESCOLHA = "Múltipla escolha";
    public static final String TIPO_VERDADEIRO_FALSO_SEQUENCIA = "Verdadeiro ou falso em sequência";
    public static final String TIPO_CORRESPONDENCIA = "Correspondência";
    public static final String TIPO_DISSERTATIVA = "Dissertativa";

    private final QuestaoRepositorio questaoRepositorio;

    public QuestaoServico(QuestaoRepositorio questaoRepositorio) { this.questaoRepositorio = questaoRepositorio; }

    public Questao salvarQuestao(Questao questao) {
        if (questao == null)
            throw new IllegalArgumentException("Informe os dados da questão.");
        if (questao.getNumero() > 0 && questaoRepositorio.findByKey(questao.getNumero()) != null
                && questaoRepositorio.estaEmProvaAplicada(questao.getNumero()))
            throw new IllegalArgumentException("Esta questão está em uma prova já aplicada a alunos e não pode mais ser alterada.");
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

    public boolean estaEmProvaAplicada(long id) { return questaoRepositorio.estaEmProvaAplicada(id); }

    private void validar(Questao questao) {
        String enunciado = questao.getTitulo() == null ? "" : questao.getTitulo().trim();
        if (enunciado.length() < MIN_ENUNCIADO || enunciado.length() > MAX_ENUNCIADO)
            throw new IllegalArgumentException("O enunciado deve ter entre %d e %d caracteres.".formatted(MIN_ENUNCIADO, MAX_ENUNCIADO));
        String tipo = questao.getDescricao() == null ? "" : questao.getDescricao().trim();
        if (tipo.isEmpty() || tipo.length() > MAX_TIPO)
            throw new IllegalArgumentException("Selecione o tipo da questão.");
        List<Alternativa> alternativas = questao.getAlternativas() == null ? List.of() : questao.getAlternativas();

        if (TIPO_DISSERTATIVA.equalsIgnoreCase(tipo)) {
            if (!alternativas.isEmpty())
                throw new IllegalArgumentException("Questões dissertativas não possuem alternativas.");
            return;
        }

        if (alternativas.size() < MIN_ALTERNATIVAS)
            throw new IllegalArgumentException("Adicione pelo menos %d alternativas ou itens.".formatted(MIN_ALTERNATIVAS));
        if (alternativas.size() > MAX_ALTERNATIVAS)
            throw new IllegalArgumentException("A questão pode ter no máximo %d alternativas ou itens.".formatted(MAX_ALTERNATIVAS));
        for (Alternativa alternativa : alternativas) {
            if (alternativa.getTexto() == null || alternativa.getTexto().isBlank())
                throw new IllegalArgumentException("O texto das alternativas ou itens não pode ficar vazio.");
            if (alternativa.getTexto().trim().length() > MAX_TEXTO_ALTERNATIVA)
                throw new IllegalArgumentException("Cada alternativa ou item pode ter até %d caracteres.".formatted(MAX_TEXTO_ALTERNATIVA));
        }

        if (TIPO_CORRESPONDENCIA.equalsIgnoreCase(tipo)) {
            for (Alternativa par : alternativas) {
                String definicao = par.getTextoCorrespondencia();
                if (definicao == null || definicao.isBlank() || definicao.trim().length() > MAX_TEXTO_ALTERNATIVA)
                    throw new IllegalArgumentException("Preencha cada definição com até %d caracteres.".formatted(MAX_TEXTO_ALTERNATIVA));
            }
            if (temRepetidos(alternativas.stream().map(Alternativa::getTexto).toList())
                    || temRepetidos(alternativas.stream().map(Alternativa::getTextoCorrespondencia).toList()))
                throw new IllegalArgumentException("Os conceitos e as definições da correspondência não podem se repetir.");
            return;
        }

        if (TIPO_VERDADEIRO_FALSO_SEQUENCIA.equalsIgnoreCase(tipo))
            return;

        if (alternativas.stream().filter(Alternativa::isCorreta).count() != 1)
            throw new IllegalArgumentException("Marque exatamente uma alternativa correta.");
    }

    private boolean temRepetidos(List<String> textos) {
        Set<String> vistos = new HashSet<>();
        for (String texto : textos)
            if (!vistos.add(texto.trim().toLowerCase(Locale.ROOT)))
                return true;
        return false;
    }
}
