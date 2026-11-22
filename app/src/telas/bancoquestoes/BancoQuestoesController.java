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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import modelos.Questao;
import servicos.QuestaoServico;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import telas.componentes.CardQuestao;
import telas.componentes.TextoQuestao;
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
        Label numero = new Label(questao.getNumero() + ") Questão " + questao.getNumero());
        numero.getStyleClass().add("card-pequeno");
        Label pergunta = new Label(questao.getTitulo());
        pergunta.getStyleClass().add("card-texto");
        pergunta.setWrapText(true);
        pergunta.setTextOverrun(OverrunStyle.ELLIPSIS);
        pergunta.setMaxHeight(56);
        pergunta.setPadding(new Insets(10, 0, 0, 0));
        int quantidade = questao.getAlternativas().size();
        Label alternativas = new Label("Dissertativa".equals(questao.getDescricao())
                ? "Resposta aberta"
                : quantidade + (quantidade == 1 ? " alternativa" : " alternativas"));
        alternativas.getStyleClass().add("card-pequeno");
        Label tipo = new Label(tipoResumido(questao.getDescricao()));
        tipo.getStyleClass().add("card-estado");
        HBox resumo = new HBox(8, tipo, alternativas);
        resumo.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        HBox.setHgrow(tipo, Priority.NEVER);

        VBox card = new VBox(numero, pergunta, resumo);
        card.getStyleClass().add("card-lista");
        card.setUserData(questao);
        card.setOnMouseClicked(event -> selecionada.set(questao));
        return card;
    }

    private String tipoResumido(String descricao) {
        if ("Correspondência".equals(descricao)) return "Correspondência";
        if ("Verdadeiro ou falso em sequência".equals(descricao)) return "V/F em sequência";
        if ("Dissertativa".equals(descricao)) return "Dissertativa";
        return "Múltipla escolha";
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
        try {
            if (questaoServico.estaEmAlgumaProva(questao.getNumero())) {
                avisar("Esta questão está vinculada a uma ou mais provas e não pode ser excluída. "
                        + "Remova-a das provas em Questões e pesos antes de excluí-la.");
                return;
            }
        } catch (RuntimeException e) {
            mostrarErro("Não foi possível verificar os vínculos da questão.", e);
            return;
        }
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                "Excluir a questão selecionada? Esta ação não pode ser desfeita.", ButtonType.CANCEL, ButtonType.OK);
        confirmacao.setHeaderText("Excluir questão");
        if (confirmacao.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        try {
            questaoServico.excluirQuestao(questao.getNumero());
            atualizar();
        } catch (IllegalArgumentException e) {
            avisar(e.getMessage());
        } catch (RuntimeException e) {
            mostrarErro("Não foi possível excluir a questão.", e);
        }
    }

    // PB27: salva a questão selecionada ou todas as questões em um único arquivo,
    // e o professor escolhe se a resposta correta vai indicada
    @FXML
    private void baixarQuestoes(ActionEvent event) {
        if (questoes.isEmpty()) {
            avisar("Não há questões cadastradas para baixar.");
            return;
        }
        Questao marcada = selecionada.get();
        ToggleGroup escopo = new ToggleGroup();
        RadioButton somenteSelecionada = new RadioButton(marcada == null
                ? "Questão selecionada (nenhuma selecionada)"
                : "Questão selecionada (Questão " + marcada.getNumero() + ")");
        somenteSelecionada.setToggleGroup(escopo);
        somenteSelecionada.setDisable(marcada == null);
        RadioButton todas = new RadioButton("Todas as questões do banco (" + questoes.size() + ")");
        todas.setToggleGroup(escopo);
        escopo.selectToggle(marcada == null ? todas : somenteSelecionada);
        CheckBox comResposta = new CheckBox("Indicar as respostas corretas");

        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Baixar questões");
        dialogo.setHeaderText("O que deseja salvar?");
        dialogo.getDialogPane().setContent(new VBox(10, somenteSelecionada, todas, new Separator(), comResposta));
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
        if (dialogo.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;

        List<Questao> exportar = escopo.getSelectedToggle() == somenteSelecionada ? List.of(marcada) : questoes;
        FileChooser seletor = new FileChooser();
        seletor.setTitle("Baixar questões");
        seletor.setInitialFileName(exportar.size() == 1 ? "questao-" + exportar.get(0).getNumero() + ".txt" : "questoes.txt");
        seletor.getExtensionFilters().add(new FileChooser.ExtensionFilter("Texto", "*.txt"));
        File arquivo = seletor.showSaveDialog((Stage) ((Node) event.getSource()).getScene().getWindow());
        if (arquivo == null) return;

        StringBuilder texto = new StringBuilder();
        for (Questao questao : exportar) {
            texto.append("Questão ").append(questao.getNumero()).append('\n')
                    .append("Tipo: ").append(CardQuestao.rotuloTipo(questao.getDescricao())).append('\n')
                    .append(questao.getTitulo()).append('\n')
                    .append(TextoQuestao.formatar(questao, comResposta.isSelected()))
                    .append('\n');
        }
        try {
            Files.writeString(arquivo.toPath(), texto.toString(), StandardCharsets.UTF_8);
            Alert aviso = new Alert(Alert.AlertType.INFORMATION,
                    (exportar.size() == 1 ? "Questão salva em " : exportar.size() + " questões salvas em ") + arquivo.getAbsolutePath());
            aviso.setHeaderText(null);
            aviso.showAndWait();
        } catch (IOException e) {
            mostrarErro("Não foi possível salvar o arquivo das questões.", e);
        }
    }

    @FXML
    private void voltar(ActionEvent event) {
        try { Prova.abrir((Stage) ((Node) event.getSource()).getScene().getWindow()); }
        catch (Exception e) { mostrarErro("Não foi possível voltar à lista de provas.", e); }
    }

    private void avisar(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.WARNING, mensagem);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }

    private void mostrarErro(String mensagem, Exception e) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensagem + "\n" + e.getMessage());
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
