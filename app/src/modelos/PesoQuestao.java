package modelos;

public class PesoQuestao {
    private final long idQuestao;
    private final double peso;

    public PesoQuestao(long idQuestao, double peso) {
        this.idQuestao = idQuestao;
        this.peso = peso;
    }

    public long getIdQuestao() { return idQuestao; }
    public double getPeso() { return peso; }
}
