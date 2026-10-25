package telas.bancoquestoes;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modelos.Questao;
import servicos.QuestaoServico;

import java.util.List;
import telas.componentes.CardQuestao;
import telas.prova.Prova;

public class BancoQuestoesController {
    @FXML private VBox listaQuestoes;
    @FXML private VBox detalheQuestao;
    @FXML private Button editarButton;
    @FXML private Button excluirButton;

    private final QuestaoServico questaoServico;
    private final ObjectProperty<Questao> selecionada = new SimpleObjectProperty<>();
    private List<Questao> questoes = List.of();

    public BancoQuestoesController(QuestaoServico questaoServico) { this.questaoServico = questaoServico; }

    @FXML
    private void initialize() {
        var nenhuma = selecionada.isNull();
        editarButton.disableProperty().bind(nenhuma);
        excluirButton.disableProperty().bind(nenhuma);
        selecionada.addListener((obs, antiga, nova) -> {
            destacarSelecionada();
            mostrarDetalhe(nova);
        });
        atualizar();
    }

    private void atualizar() {
        questoes = questaoServico.listarQuestoes();
        selecionada.set(null);
        listaQuestoes.getChildren().clear();
        for (Questao questao : questoes) listaQuestoes.getChildren().add(cardQuestao(questao));
        if (questoes.isEmpty()) listaQuestoes.getChildren().add(vazio("Nenhuma questão cadastrada. Use Criar questão."));
        mostrarDetalhe(null);
    }

    // Card da lista de questões do Figma: número, pergunta resumida e quantidade de alternativas
    private VBox cardQuestao(Questao questao) {
        Label numero = new Label(questao.getNumero() + ")Questão " + questao.getNumero());
        numero.getStyleClass().add("card-pequeno");
        Label pergunta = new Label(questao.getTitulo());
        pergunta.getStyleClass().add("card-texto");
        pergunta.setWrapText(true);
        pergunta.setTextOverrun(OverrunStyle.ELLIPSIS);
        pergunta.setMaxHeight(56);
        pergunta.setPadding(new Insets(10, 0, 0, 0));
        int quantidade = questao.getAlternativas().size();
        Label alternativas = new Label(quantidade + (quantidade == 1 ? " alternativa" : " alternativas"));
        alternativas.getStyleClass().add("card-pequeno");

        VBox card = new VBox(numero, pergunta, alternativas);
        card.getStyleClass().add("card-lista");
        card.setUserData(questao);
        card.setOnMouseClicked(event -> selecionada.set(questao));
        return card;
    }

    private void destacarSelecionada() {
        for (Node card : listaQuestoes.getChildren()) {
            card.getStyleClass().remove("selecionado");
            if (selecionada.get() != null && card.getUserData() == selecionada.get()) card.getStyleClass().add("selecionado");
        }
    }

    private void mostrarDetalhe(Questao questao) {
        detalheQuestao.getChildren().clear();
        if (questao == null) {
            detalheQuestao.getChildren().add(vazio("Selecione uma questão para visualizá-la."));
            return;
        }
        detalheQuestao.getChildren().add(CardQuestao.visualizar(questoes.indexOf(questao) + 1, questao, null));
    }

    private Label vazio(String mensagem) {
        Label vazio = new Label(mensagem);
        vazio.getStyleClass().add("vazio");
        vazio.setWrapText(true);
        return vazio;
    }

    @FXML private void novaQuestao(ActionEvent event) { abrirFormulario(event, null); }
    @FXML private void editarQuestao(ActionEvent event) { abrirFormulario(event, selecionada.get()); }

    private void abrirFormulario(ActionEvent event, Questao questao) {
        try {
            telas.questao.Questao.abrir((Stage) ((Node) event.getSource()).getScene().getWindow(), questao);
        } catch (Exception e) {
            mostrarErro("Não foi possível abrir o formulário da questão.", e);
        }
    }

    @FXML
    private void excluirQuestao() {
        Questao questao = selecionada.get();
        if (questao == null) return;
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                "Excluir a questão selecionada? Ela também será removida das provas em que estiver vinculada.", ButtonType.CANCEL, ButtonType.OK);
        confirmacao.setHeaderText("Excluir questão");
        if (confirmacao.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        try {
            questaoServico.excluirQuestao(questao.getNumero());
            atualizar();
        } catch (RuntimeException e) {
            mostrarErro("Não foi possível excluir a questão.", e);
        }
    }

    @FXML
    private void voltar(ActionEvent event) {
        try { Prova.abrir((Stage) ((Node) event.getSource()).getScene().getWindow()); }
        catch (Exception e) { mostrarErro("Não foi possível voltar à lista de provas.", e); }
    }

    private void mostrarErro(String mensagem, Exception e) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensagem + "\n" + e.getMessage());
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
