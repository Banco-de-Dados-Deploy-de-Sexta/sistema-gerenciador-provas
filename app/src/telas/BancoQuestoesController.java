package telas;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import modelos.Questao;
import servicos.QuestaoServico;

public class BancoQuestoesController {
    @FXML private TableView<Questao> tabelaQuestoes;
    @FXML private TableColumn<Questao, String> colunaId;
    @FXML private TableColumn<Questao, String> colunaEnunciado;
    @FXML private TableColumn<Questao, String> colunaAlternativas;
    @FXML private Button editarButton;
    @FXML private Button excluirButton;
    @FXML private Button visualizarButton;

    private final QuestaoServico questaoServico;

    public BancoQuestoesController(QuestaoServico questaoServico) { this.questaoServico = questaoServico; }

    @FXML
    private void initialize() {
        colunaId.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.valueOf(c.getValue().getNumero())));
        colunaEnunciado.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getTitulo()));
        colunaAlternativas.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.valueOf(c.getValue().getAlternativas().size())));
        editarButton.disableProperty().bind(tabelaQuestoes.getSelectionModel().selectedItemProperty().isNull());
        excluirButton.disableProperty().bind(tabelaQuestoes.getSelectionModel().selectedItemProperty().isNull());
        visualizarButton.disableProperty().bind(tabelaQuestoes.getSelectionModel().selectedItemProperty().isNull());
        atualizar();
    }

    private void atualizar() { tabelaQuestoes.getItems().setAll(questaoServico.listarQuestoes()); }

    @FXML private void novaQuestao(ActionEvent event) { abrirFormulario(event, null); }
    @FXML private void editarQuestao(ActionEvent event) { abrirFormulario(event, tabelaQuestoes.getSelectionModel().getSelectedItem()); }

    private void abrirFormulario(ActionEvent event, Questao questao) {
        try {
            telas.Questao.abrir((Stage) ((Node) event.getSource()).getScene().getWindow(), questao);
        } catch (Exception e) {
            mostrarErro("Não foi possível abrir o formulário da questão.", e);
        }
    }

    @FXML
    private void visualizarQuestao() {
        Questao selecionada = tabelaQuestoes.getSelectionModel().getSelectedItem();
        if (selecionada == null) return;
        StringBuilder detalhes = new StringBuilder("Questão #")
                .append(selecionada.getNumero()).append("\n\n")
                .append(selecionada.getTitulo()).append("\n\nAlternativas:\n");
        for (int i = 0; i < selecionada.getAlternativas().size(); i++) {
            var alternativa = selecionada.getAlternativas().get(i);
            detalhes.append((char) ('A' + i)).append(") ").append(alternativa.getTexto());
            if (alternativa.isCorreta()) detalhes.append("  ✓ correta");
            detalhes.append('\n');
        }
        Alert alerta = new Alert(Alert.AlertType.INFORMATION, detalhes.toString());
        alerta.setHeaderText("Detalhes da questão");
        alerta.showAndWait();
    }

    @FXML
    private void excluirQuestao() {
        Questao selecionada = tabelaQuestoes.getSelectionModel().getSelectedItem();
        if (selecionada == null) return;
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                "Excluir a questão selecionada? Ela também será removida das provas em que estiver vinculada.", ButtonType.CANCEL, ButtonType.OK);
        confirmacao.setHeaderText("Excluir questão");
        if (confirmacao.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        try {
            questaoServico.excluirQuestao(selecionada.getNumero());
            atualizar();
        } catch (RuntimeException e) {
            mostrarErro("Não foi possível excluir a questão.", e);
        }
    }

    @FXML
    private void voltar(ActionEvent event) {
        try { new Home().start((Stage) ((Node) event.getSource()).getScene().getWindow()); }
        catch (Exception e) { mostrarErro("Não foi possível voltar ao início.", e); }
    }

    private void mostrarErro(String mensagem, Exception e) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensagem + "\n" + e.getMessage());
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
