package modelos;

public class QuestaoComPeso {
    private final Questao questao;
    private final double peso;

    public QuestaoComPeso(Questao questao, double peso) {
        this.questao = questao;
        this.peso = peso;
    }

    public Questao getQuestao() { return questao; }
    public double getPeso() { return peso; }
}
