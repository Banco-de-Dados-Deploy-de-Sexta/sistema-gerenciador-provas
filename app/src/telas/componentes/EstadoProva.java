package telas.componentes;

import javafx.scene.control.Label;

// Cores dos badges de estado da prova (professor e aluno), para identificar
// o estado rapidamente sem ler o texto:
//   cinza  - Rascunho: incompleta, sem questões
//   roxo   - Pronta (prof.) / Realizada (aluno): etapa cumprida, aguardando a próxima
//   azul   - Agendada: data marcada, ainda no futuro
//   verde  - Disponível (aluno pode fazer agora) / Aplicada / Corrigida: concluída
//   âmbar  - Encerrada: prazo expirou
// Vermelho fica reservado para estados de erro/cancelamento que ainda não existem.
public final class EstadoProva {

    private static final String CLASSE_BASE = "card-estado";
    private static final String PREFIXO_COR = "estado-";

    private EstadoProva() {
    }

    public static Label criarBadge(String estado) {
        Label badge = new Label(estado);
        aplicarEstilo(badge, estado);
        return badge;
    }

    public static void aplicarEstilo(Label badge, String estado) {
        badge.getStyleClass().removeIf(c -> c.startsWith(PREFIXO_COR));
        if (!badge.getStyleClass().contains(CLASSE_BASE)) {
            badge.getStyleClass().add(CLASSE_BASE);
        }
        badge.getStyleClass().add(PREFIXO_COR + cor(estado));
    }

    static String cor(String estado) {
        if (estado == null) return "cinza";
        switch (estado.trim().toLowerCase()) {
            case "rascunho":
                return "cinza";
            case "pronta":
            case "realizada":
                return "roxo";
            case "agendada":
                return "azul";
            case "disponível":
            case "disponivel":
            case "aplicada":
            case "corrigida":
                return "verde";
            case "encerrada":
                return "ambar";
            default:
                return "cinza";
        }
    }
}
