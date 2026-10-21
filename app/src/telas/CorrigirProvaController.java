package telas;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modelos.Alternativa;
import modelos.Prova;
import modelos.QuestaoComPeso;
import modelos.ResultadoCorrecao;
import servicos.ProvaServico;

import java.util.HashMap;
import java.util.Map;

public class CorrigirProvaController {
    @FXML private Label tituloTela;
    @FXML private Label resultadoLabel;
    @FXML private VBox listaQuestoes;

    private final Prova prova;
    private final ProvaServico provaServico;
    private final Map<Long, ToggleGroup> respostas = new HashMap<>();

    public CorrigirProvaController(Prova prova, ProvaServico provaServico) {
        this.prova = prova;
        this.provaServico = provaServico;
    }

    public void carregar() {
        tituloTela.setText("Corrigir — " + prova.getTitulo());
        listaQuestoes.getChildren().clear();
        respostas.clear();
        for (QuestaoComPeso item : provaServico.listarQuestoes(prova.getCodigo())) {
            VBox bloco = new VBox(7);
            bloco.getStyleClass().add("question-row");
            Label pergunta = new Label(item.getQuestao().getTitulo() + "  (peso " + item.getPeso() + ")");
            pergunta.getStyleClass().add("step-title");
            pergunta.setWrapText(true);
            bloco.getChildren().add(pergunta);
            ToggleGroup grupo = new ToggleGroup();
            for (Alternativa alternativa : item.getQuestao().getAlternativas()) {
                RadioButton opcao = new RadioButton(alternativa.getTexto());
                opcao.setWrapText(true);
                opcao.setToggleGroup(grupo);
                opcao.setUserData(alternativa.getId());
                bloco.getChildren().add(opcao);
            }
            if (item.getQuestao().getAlternativas().isEmpty()) {
                Label semOpcoes = new Label("Esta questão não tem alternativas cadastradas.");
                semOpcoes.getStyleClass().add("subtitulo");
                bloco.getChildren().add(semOpcoes);
            }
            respostas.put(item.getQuestao().getNumero(), grupo);
            listaQuestoes.getChildren().add(bloco);
        }
        if (listaQuestoes.getChildren().isEmpty()) {
            Label vazio = new Label("Esta prova ainda não tem questões. Vincule questões e pesos primeiro.");
            vazio.getStyleClass().add("subtitulo");
            listaQuestoes.getChildren().add(vazio);
        }
    }

    @FXML
    private void corrigir() {
        if (respostas.isEmpty()) {
            mostrar(Alert.AlertType.WARNING, "A prova ainda não possui questões para corrigir.");
            return;
        }
        Map<Long, Integer> respostasMarcadas = new HashMap<>();
        for (Map.Entry<Long, ToggleGroup> entrada : respostas.entrySet()) {
            Toggle marcada = entrada.getValue().getSelectedToggle();
            if (marcada != null) respostasMarcadas.put(entrada.getKey(), (Integer) marcada.getUserData());
        }
        ResultadoCorrecao resultado = provaServico.corrigir(prova.getCodigo(), respostasMarcadas);
        resultadoLabel.setText(String.format(java.util.Locale.forLanguageTag("pt-BR"),
                "Resultado: %.2f de %.2f pontos", resultado.getPontosObtidos(), resultado.getPontosPossiveis()));
    }

    @FXML
    private void voltar(ActionEvent event) {
        try { telas.Prova.abrir((Stage) tituloTela.getScene().getWindow()); }
        catch (Exception e) { mostrar(Alert.AlertType.ERROR, "Não foi possível voltar à lista de provas."); }
    }

    private void mostrar(Alert.AlertType tipo, String mensagem) {
        Alert alerta = new Alert(tipo, mensagem);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
