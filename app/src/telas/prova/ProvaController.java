package telas.prova;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import modelos.Alternativa;
import modelos.Prova;
import modelos.QuestaoComPeso;
import servicos.ProvaServico;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import telas.bancoquestoes.BancoQuestoes;
import telas.componentes.CardQuestao;
import telas.composicaoprova.ComposicaoProva;
import telas.corrigirprova.CorrigirProva;

public class ProvaController {
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private VBox listaProvas;
    @FXML private VBox questoesProva;
    @FXML private Button editarButton;
    @FXML private Button excluirButton;
    @FXML private Button agendarButton;
    @FXML private Button baixarButton;
    @FXML private Button questoesButton;
    @FXML private Button corrigirButton;

    private final ProvaServico provaServico;
    private final ObjectProperty<Prova> selecionada = new SimpleObjectProperty<>();

    public ProvaController(ProvaServico provaServico) { this.provaServico = provaServico; }

    @FXML
    private void initialize() {
        var nenhuma = selecionada.isNull();
        for (Button botao : List.of(editarButton, excluirButton, agendarButton, baixarButton, questoesButton, corrigirButton))
            botao.disableProperty().bind(nenhuma);
        selecionada.addListener((obs, antiga, nova) -> {
            destacarSelecionada();
            mostrarQuestoes();
        });
        atualizar();
    }

    private void atualizar() {
        String codigoSelecionado = selecionada.get() == null ? null : selecionada.get().getCodigo();
        List<Prova> provas = provaServico.listarProvas();
        listaProvas.getChildren().clear();
        for (Prova prova : provas) listaProvas.getChildren().add(cardProva(prova));
        if (provas.isEmpty()) listaProvas.getChildren().add(vazio("Nenhuma prova cadastrada. Use Criar prova."));
        selecionada.set(provas.stream().filter(p -> p.getCodigo().equals(codigoSelecionado)).findFirst().orElse(null));
        destacarSelecionada();
        mostrarQuestoes();
    }

    // Card de prova do Figma: código e data no topo, título, matéria/curso/bimestre e total de questões
    private VBox cardProva(Prova prova) {
        Label codigo = new Label(prova.getCodigo());
        codigo.getStyleClass().add("card-pequeno");
        Label data = new Label(prova.getData() == null ? "Sem data" : prova.getData().format(FORMATO_DATA));
        data.getStyleClass().add("card-pequeno");
        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);
        HBox topo = new HBox(codigo, espaco, data);

        Label titulo = new Label(prova.getTitulo());
        titulo.getStyleClass().add("card-titulo");
        Label detalhes = new Label(descricao(prova));
        detalhes.getStyleClass().add("card-pequeno");
        detalhes.setWrapText(true);
        int quantidade = provaServico.contarQuestoes(prova.getCodigo());
        Label questoes = new Label(quantidade + (quantidade == 1 ? " questão" : " questões"));
        questoes.getStyleClass().add("card-destaque");
        questoes.setPadding(new Insets(16, 0, 0, 0));

        VBox card = new VBox(topo, titulo, detalhes, questoes);
        card.getStyleClass().add("card-lista");
        card.setUserData(prova);
        card.setOnMouseClicked(event -> selecionada.set(prova));
        return card;
    }

    private String descricao(Prova prova) {
        String curso = prova.getCurso() == null || prova.getCurso().isBlank() ? "" : prova.getCurso() + " - ";
        return curso + prova.getMateria() + "/" + prova.getBimestre() + "º bimestre";
    }

    private void destacarSelecionada() {
        for (Node card : listaProvas.getChildren()) {
            boolean marcada = selecionada.get() != null && card.getUserData() == selecionada.get();
            card.getStyleClass().remove("selecionado");
            if (marcada) card.getStyleClass().add("selecionado");
        }
    }

    private void mostrarQuestoes() {
        questoesProva.getChildren().clear();
        Prova prova = selecionada.get();
        if (prova == null) {
            questoesProva.getChildren().add(vazio("Selecione uma prova para ver as questões."));
            return;
        }
        List<QuestaoComPeso> questoes = provaServico.listarQuestoes(prova.getCodigo());
        for (int i = 0; i < questoes.size(); i++)
            questoesProva.getChildren().add(CardQuestao.visualizar(i + 1, questoes.get(i).getQuestao(), questoes.get(i).getPeso()));
        if (questoes.isEmpty())
            questoesProva.getChildren().add(vazio("Esta prova ainda não tem questões. Use Questões e pesos para vinculá-las."));
    }

    private Label vazio(String mensagem) {
        Label vazio = new Label(mensagem);
        vazio.getStyleClass().add("vazio");
        vazio.setWrapText(true);
        return vazio;
    }

    @FXML private void novaProva(ActionEvent event) { abrirFormulario(event, null); }
    @FXML private void editarProva(ActionEvent event) { abrirFormulario(event, selecionada.get()); }

    private void abrirFormulario(ActionEvent event, Prova prova) {
        try { telas.prova.Prova.abrirFormulario(palcoDe(event), prova); }
        catch (Exception e) { erro("Não foi possível abrir o formulário da prova.", e); }
    }

    @FXML
    private void excluirProva() {
        Prova prova = selecionada.get();
        if (prova == null) return;
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                "Excluir a prova e seus vínculos com questões?", ButtonType.CANCEL, ButtonType.OK);
        confirmacao.setHeaderText("Excluir prova");
        if (confirmacao.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        try { provaServico.excluirProva(prova.getCodigo()); selecionada.set(null); atualizar(); }
        catch (RuntimeException e) { erro("Não foi possível excluir a prova.", e); }
    }

    @FXML
    private void agendarProva() {
        Prova prova = selecionada.get();
        if (prova == null) return;
        DatePicker data = new DatePicker(prova.getData() == null ? LocalDate.now() : prova.getData());
        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Agendar prova");
        dialogo.setHeaderText("Escolha a data da prova \"" + prova.getTitulo() + "\"");
        dialogo.getDialogPane().setContent(new VBox(8, new Label("Data da prova"), data));
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
        if (dialogo.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK || data.getValue() == null) return;
        LocalDate anterior = prova.getData();
        try {
            prova.setData(data.getValue());
            provaServico.salvarProva(prova);
            atualizar();
        } catch (RuntimeException e) {
            prova.setData(anterior);
            erro("Não foi possível agendar a prova.", e);
        }
    }

    // Exporta a prova em texto, sem indicar as alternativas corretas
    @FXML
    private void baixarProva(ActionEvent event) {
        Prova prova = selecionada.get();
        if (prova == null) return;
        FileChooser seletor = new FileChooser();
        seletor.setTitle("Baixar prova");
        seletor.setInitialFileName(prova.getCodigo() + ".txt");
        seletor.getExtensionFilters().add(new FileChooser.ExtensionFilter("Texto", "*.txt"));
        File arquivo = seletor.showSaveDialog(palcoDe(event));
        if (arquivo == null) return;

        StringBuilder texto = new StringBuilder()
                .append(prova.getCodigo()).append(" — ").append(prova.getTitulo()).append('\n')
                .append(descricao(prova))
                .append(prova.getData() == null ? "" : " — " + prova.getData().format(FORMATO_DATA))
                .append("\n\n");
        List<QuestaoComPeso> questoes = provaServico.listarQuestoes(prova.getCodigo());
        for (int i = 0; i < questoes.size(); i++) {
            QuestaoComPeso item = questoes.get(i);
            texto.append(i + 1).append(") ").append(item.getQuestao().getTitulo())
                    .append(String.format(Locale.forLanguageTag("pt-BR"), "  (peso %.1f)", item.getPeso())).append('\n');
            List<Alternativa> alternativas = item.getQuestao().getAlternativas();
            for (int j = 0; j < alternativas.size(); j++)
                texto.append("   ").append(CardQuestao.letra(j)).append(' ').append(alternativas.get(j).getTexto()).append('\n');
            texto.append('\n');
        }
        try {
            Files.writeString(arquivo.toPath(), texto.toString(), StandardCharsets.UTF_8);
            Alert aviso = new Alert(Alert.AlertType.INFORMATION, "Prova salva em " + arquivo.getAbsolutePath());
            aviso.setHeaderText(null);
            aviso.showAndWait();
        } catch (IOException e) {
            erro("Não foi possível salvar o arquivo da prova.", e);
        }
    }

    @FXML
    private void abrirBancoQuestoes(ActionEvent event) {
        try { BancoQuestoes.abrir(palcoDe(event)); }
        catch (Exception e) { erro("Não foi possível abrir o banco de questões.", e); }
    }

    @FXML
    private void gerenciarQuestoes(ActionEvent event) {
        Prova prova = selecionada.get();
        if (prova == null) return;
        try { ComposicaoProva.abrir(palcoDe(event), prova, provaServico, telas.prova.Prova.getQuestaoServico()); }
        catch (Exception e) { erro("Não foi possível abrir a composição da prova.", e); }
    }

    @FXML
    private void corrigir(ActionEvent event) {
        Prova prova = selecionada.get();
        if (prova == null) return;
        try { CorrigirProva.abrir(palcoDe(event), prova, provaServico); }
        catch (Exception e) { erro("Não foi possível abrir a correção.", e); }
    }

    private Stage palcoDe(ActionEvent event) { return (Stage) ((Node) event.getSource()).getScene().getWindow(); }

    private void erro(String mensagem, Exception e) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensagem + "\n" + e.getMessage());
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
