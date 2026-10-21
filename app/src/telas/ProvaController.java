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
import modelos.Prova;
import servicos.ProvaServico;

public class ProvaController {
    @FXML private TableView<Prova> tabelaProvas;
    @FXML private TableColumn<Prova, String> colunaCodigo;
    @FXML private TableColumn<Prova, String> colunaTitulo;
    @FXML private TableColumn<Prova, String> colunaMateria;
    @FXML private TableColumn<Prova, String> colunaCurso;
    @FXML private TableColumn<Prova, String> colunaBimestre;
    @FXML private TableColumn<Prova, String> colunaData;
    @FXML private Button editarButton;
    @FXML private Button excluirButton;
    @FXML private Button questoesButton;
    @FXML private Button corrigirButton;

    private final ProvaServico provaServico;

    public ProvaController(ProvaServico provaServico) { this.provaServico = provaServico; }

    @FXML
    private void initialize() {
        colunaCodigo.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getCodigo()));
        colunaTitulo.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getTitulo()));
        colunaMateria.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getMateria()));
        colunaCurso.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getCurso()));
        colunaBimestre.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.valueOf(c.getValue().getBimestre())));
        colunaData.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getData() == null ? "—" : c.getValue().getData().toString()));
        var selecionada = tabelaProvas.getSelectionModel().selectedItemProperty();
        editarButton.disableProperty().bind(selecionada.isNull());
        excluirButton.disableProperty().bind(selecionada.isNull());
        questoesButton.disableProperty().bind(selecionada.isNull());
        corrigirButton.disableProperty().bind(selecionada.isNull());
        atualizar();
    }

    private void atualizar() { tabelaProvas.getItems().setAll(provaServico.listarProvas()); }

    @FXML private void novaProva(ActionEvent event) { abrirFormulario(event, null); }
    @FXML private void editarProva(ActionEvent event) { abrirFormulario(event, tabelaProvas.getSelectionModel().getSelectedItem()); }

    private void abrirFormulario(ActionEvent event, Prova prova) {
        try { telas.Prova.abrirFormulario((Stage) ((Node) event.getSource()).getScene().getWindow(), prova); }
        catch (Exception e) { erro("Não foi possível abrir o formulário da prova.", e); }
    }

    @FXML
    private void excluirProva() {
        Prova prova = tabelaProvas.getSelectionModel().getSelectedItem();
        if (prova == null) return;
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                "Excluir a prova e seus vínculos com questões?", ButtonType.CANCEL, ButtonType.OK);
        confirmacao.setHeaderText("Excluir prova");
        if (confirmacao.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        try { provaServico.excluirProva(prova.getCodigo()); atualizar(); }
        catch (RuntimeException e) { erro("Não foi possível excluir a prova.", e); }
    }

    @FXML
    private void gerenciarQuestoes(ActionEvent event) {
        Prova prova = tabelaProvas.getSelectionModel().getSelectedItem();
        if (prova == null) return;
        try { ComposicaoProva.abrir((Stage) ((Node) event.getSource()).getScene().getWindow(), prova, provaServico, telas.Prova.getQuestaoServico()); }
        catch (Exception e) { erro("Não foi possível abrir a composição da prova.", e); }
    }

    @FXML
    private void corrigir(ActionEvent event) {
        Prova prova = tabelaProvas.getSelectionModel().getSelectedItem();
        if (prova == null) return;
        try { CorrigirProva.abrir((Stage) ((Node) event.getSource()).getScene().getWindow(), prova, provaServico); }
        catch (Exception e) { erro("Não foi possível abrir a correção.", e); }
    }

    @FXML
    private void voltar(ActionEvent event) {
        try { new Home().start((Stage) ((Node) event.getSource()).getScene().getWindow()); }
        catch (Exception e) { erro("Não foi possível voltar ao início.", e); }
    }

    private void erro(String mensagem, Exception e) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensagem + "\n" + e.getMessage());
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
