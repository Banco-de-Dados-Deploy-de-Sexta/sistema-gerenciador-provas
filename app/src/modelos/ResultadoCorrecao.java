package modelos;

public class ResultadoCorrecao {
    private final double pontosObtidos;
    private final double pontosPossiveis;

    public ResultadoCorrecao(double pontosObtidos, double pontosPossiveis) {
        this.pontosObtidos = pontosObtidos;
        this.pontosPossiveis = pontosPossiveis;
    }

    public double getPontosObtidos() { return pontosObtidos; }
    public double getPontosPossiveis() { return pontosPossiveis; }
}
