package auxiliares;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class Embaralhamento {
    private static final long QUESTOES = 0;
    private static final long ALTERNATIVAS = 1;
    private static final long AFIRMACOES = 2;
    private static final long CONCEITOS = 3;
    private static final long DEFINICOES = 4;

    private Embaralhamento() { }

    public static <T> List<T> questoes(List<T> itens, String codigoProva, String registroAluno) {
        return embaralhar(itens, codigoProva, registroAluno, QUESTOES);
    }

    public static <T> List<T> alternativas(List<T> itens, String codigoProva, String registroAluno, long idQuestao) {
        return embaralhar(itens, codigoProva, registroAluno, idQuestao * 10 + ALTERNATIVAS);
    }

    public static <T> List<T> afirmacoes(List<T> itens, String codigoProva, String registroAluno, long idQuestao) {
        return embaralhar(itens, codigoProva, registroAluno, idQuestao * 10 + AFIRMACOES);
    }

    public static <T> List<T> conceitos(List<T> itens, String codigoProva, String registroAluno, long idQuestao) {
        return embaralhar(itens, codigoProva, registroAluno, idQuestao * 10 + CONCEITOS);
    }

    public static <T> List<T> definicoes(List<T> itens, String codigoProva, String registroAluno, long idQuestao) {
        return embaralhar(itens, codigoProva, registroAluno, idQuestao * 10 + DEFINICOES);
    }

    private static <T> List<T> embaralhar(List<T> itens, String codigoProva, String registroAluno, long complemento) {
        List<T> copia = new ArrayList<>(itens == null ? List.of() : itens);
        Collections.shuffle(copia, new Random(Objects.hash(codigoProva, registroAluno, complemento)));
        return copia;
    }
}
