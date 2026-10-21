package telas;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import servicos.QuestaoServico;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class QuestaoController {

    @FXML private TextField enunciadoCaixaTexto;
    @FXML private TextField perguntaCaixaTexto;
    @FXML private GridPane gridAlternativas;

    private final QuestaoServico questaoServico;
    private final List<String> alternativas = new ArrayList<>();
    private ToggleGroup grupoCorreta = new ToggleGroup();

    public QuestaoController(QuestaoServico questaoServico) {
        this.questaoServico = questaoServico;
    }

    @FXML
    private void adicionarAlternativa() {
        String texto = perguntaCaixaTexto.getText().trim();
        if (texto.isEmpty()) {
            return;
        }

        int linha = alternativas.size();
        char letra = (char) ('a' + linha);

        // O botão de rádio marca qual alternativa é a correta
        RadioButton opcao = new RadioButton(letra + ")");
        opcao.setToggleGroup(grupoCorreta);
        opcao.setUserData(texto);

        gridAlternativas.add(opcao, 0, linha);
        gridAlternativas.add(new Label(texto), 1, linha);

        alternativas.add(texto);
        perguntaCaixaTexto.clear();
        perguntaCaixaTexto.requestFocus();
    }

    @FXML
    private void confirmar() {
        String enunciado = enunciadoCaixaTexto.getText().trim();
        Toggle marcada = grupoCorreta.getSelectedToggle();

        if (enunciado.isEmpty() || alternativas.size() < 3 || marcada == null) {
            mostrar(Alert.AlertType.WARNING, "Preencha o enunciado, adicione ao menos 3 alternativas e marque a correta.");
            return;
        }

        try {
            // var evita misturar telas.Questao com modelos.Questao
            questaoServico.cadastrarQuestao(
                    enunciado, "Assinale a alternativa correta:",
                    String.join("|", alternativas), (String) marcada.getUserData()
            );

            mostrar(Alert.AlertType.INFORMATION, "Questão e alternativas cadastradas com sucesso.");
            limpar();
        } catch (IllegalStateException e) {
            mostrar(Alert.AlertType.ERROR, "Não foi possível salvar a questão e suas alternativas no banco.");
            e.printStackTrace();
        }
    }

    private void limpar() {
        enunciadoCaixaTexto.clear();
        perguntaCaixaTexto.clear();
        gridAlternativas.getChildren().clear();
        alternativas.clear();
        grupoCorreta = new ToggleGroup();
    }

    private void mostrar(Alert.AlertType tipo, String mensagem) {
        Alert alerta = new Alert(tipo, mensagem);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }

    @FXML
    private void voltar(ActionEvent event) {
        try {
            Stage palco = (Stage) ((Node) event.getSource()).getScene().getWindow();
            new Home().start(palco);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
