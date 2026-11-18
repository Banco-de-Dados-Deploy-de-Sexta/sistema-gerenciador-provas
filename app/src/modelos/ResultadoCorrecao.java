package modelos;

import java.util.Map;

public class ResultadoCorrecao {
    private final double pontosObtidos;
    private final double pontosPossiveis;
    private final Map<Long, Boolean> acertosPorQuestao;

    public ResultadoCorrecao(double pontosObtidos, double pontosPossiveis) {
        this(pontosObtidos, pontosPossiveis, Map.of());
    }

    public ResultadoCorrecao(double pontosObtidos, double pontosPossiveis,
            Map<Long, Boolean> acertosPorQuestao) {
        this.pontosObtidos = pontosObtidos;
        this.pontosPossiveis = pontosPossiveis;
        this.acertosPorQuestao = acertosPorQuestao == null ? Map.of() : Map.copyOf(acertosPorQuestao);
    }

    public double getPontosObtidos() { return pontosObtidos; }
    public double getPontosPossiveis() { return pontosPossiveis; }
    public Map<Long, Boolean> getAcertosPorQuestao() { return acertosPorQuestao; }
}
