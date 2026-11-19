package telas.corrigirdissertativas;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modelos.Prova;
import modelos.ProvaAluno;
import modelos.QuestaoComPeso;
import modelos.RespostaAluno;
import servicos.ProvaServico;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CorrigirDissertativasController {
    private static final String TIPO_DISSERTATIVA = "Dissertativa";

    @FXML private Label tituloTela;
    @FXML private ComboBox<ProvaAluno> entregaComboBox;
    @FXML private VBox listaQuestoes;
    @FXML private Label estadoLabel;

    private final Prova prova;
    private final ProvaServico provaServico;
    private List<QuestaoComPeso> questoesDissertativas = List.of();
    private List<ProvaAluno> entregasComRespostas = List.of();

    public CorrigirDissertativasController(Prova prova, ProvaServico provaServico) {
        this.prova = prova;
        this.provaServico = provaServico;
    }

    public void carregar() {
        tituloTela.setText("Corrigir dissertativas — " + prova.getTitulo());
        questoesDissertativas = provaServico.listarQuestoes(prova.getCodigo()).stream()
                .filter(item -> TIPO_DISSERTATIVA.equalsIgnoreCase(item.getQuestao().getDescricao()))
                .toList();
        entregaComboBox.setCellFactory(combo -> celulaEntrega());
        entregaComboBox.setButtonCell(celulaEntrega());
        entregaComboBox.valueProperty().addListener((obs, antiga, nova) -> renderizarEntrega(nova));

        if (questoesDissertativas.isEmpty()) {
            entregaComboBox.setDisable(true);
            estadoLabel.setText("Esta prova não contém questões dissertativas.");
            listaQuestoes.getChildren().setAll(mensagemVazia("Adicione uma questão dissertativa à prova para corrigir respostas abertas."));
            return;
        }
        atualizarEntregas(null);
    }

    private ListCell<ProvaAluno> celulaEntrega() {
        return new ListCell<>() {
            @Override
            protected void updateItem(ProvaAluno item, boolean vazio) {
                super.updateItem(item, vazio);
                setText(vazio || item == null ? null : item.getRegistroAluno());
            }
        };
    }

    private void atualizarEntregas(String registroSelecionado) {
        List<ProvaAluno> entregas = new ArrayList<>();
        for (ProvaAluno entrega : provaServico.listarEntregasProva(prova.getCodigo())) {
            List<RespostaAluno> respostas = provaServico.listarRespostasAluno(
                    prova.getCodigo(), entrega.getRegistroAluno());
            Map<Integer, RespostaAluno> porQuestao = new HashMap<>();
            for (RespostaAluno resposta : respostas) porQuestao.put(resposta.getIdQuestao(), resposta);
            boolean temRespostaAberta = questoesDissertativas.stream()
                    .map(item -> porQuestao.get((int) item.getQuestao().getNumero()))
                    .anyMatch(this::temRespostaAberta);
            if (temRespostaAberta) {
                entregas.add(entrega);
            }
        }

        entregasComRespostas = entregas;
        entregaComboBox.getItems().setAll(entregasComRespostas);
        ProvaAluno selecionada = entregasComRespostas.stream()
                .filter(item -> item.getRegistroAluno().equals(registroSelecionado))
                .findFirst()
                .orElse(entregasComRespostas.isEmpty() ? null : entregasComRespostas.get(0));
        entregaComboBox.setValue(selecionada);

        if (entregasComRespostas.isEmpty()) {
            estadoLabel.setText("Não há entregas com respostas dissertativas.");
            listaQuestoes.getChildren().setAll(mensagemVazia("Quando os alunos entregarem respostas abertas, elas aparecerão aqui."));
        }
    }

    private boolean temRespostaAberta(RespostaAluno resposta) {
        return resposta != null && resposta.getRespostaTexto() != null && !resposta.getRespostaTexto().isBlank();
    }

    private void renderizarEntrega(ProvaAluno entrega) {
        listaQuestoes.getChildren().clear();
        if (entrega == null) return;

        Map<Integer, RespostaAluno> respostasPorQuestao = new HashMap<>();
        for (RespostaAluno resposta : provaServico.listarRespostasAluno(prova.getCodigo(), entrega.getRegistroAluno()))
            respostasPorQuestao.put(resposta.getIdQuestao(), resposta);

        int exibidas = 0;
        int aguardandoNota = 0;
        for (QuestaoComPeso item : questoesDissertativas) {
            RespostaAluno resposta = respostasPorQuestao.get((int) item.getQuestao().getNumero());
            if (resposta == null || resposta.getRespostaTexto() == null || resposta.getRespostaTexto().isBlank())
                continue;
            listaQuestoes.getChildren().add(criarCardResposta(entrega, item, resposta));
            exibidas++;
            if (resposta.getNotaObtida() == null) aguardandoNota++;
        }
        estadoLabel.setText("Entrega do aluno " + entrega.getRegistroAluno()
                + (exibidas == 1 ? " — 1 resposta aberta" : " — " + exibidas + " respostas abertas")
                + (aguardandoNota == 0 ? " — corrigida" : " — " + aguardandoNota + " aguardando nota"));
        if (exibidas == 0)
            listaQuestoes.getChildren().add(mensagemVazia("Este aluno não enviou respostas dissertativas."));
    }

    private VBox criarCardResposta(ProvaAluno entrega, QuestaoComPeso item, RespostaAluno resposta) {
        VBox card = new VBox(10);
        card.getStyleClass().add("question-row");

        Label titulo = new Label("Questão " + item.getQuestao().getNumero()
                + "  (peso " + String.format(Locale.forLanguageTag("pt-BR"), "%.2f", item.getPeso()) + ")");
        titulo.getStyleClass().add("step-title");
        titulo.setWrapText(true);
        Label enunciado = new Label(item.getQuestao().getTitulo());
        enunciado.setWrapText(true);
        enunciado.getStyleClass().add("card-titulo");

        TextArea textoResposta = new TextArea(resposta.getRespostaTexto());
        textoResposta.setEditable(false);
        textoResposta.setWrapText(true);
        textoResposta.setPrefRowCount(Math.max(3, Math.min(8, resposta.getRespostaTexto().split("\\R", -1).length + 1)));

        TextField nota = new TextField(resposta.getNotaObtida() == null
                ? ""
                : String.format(Locale.forLanguageTag("pt-BR"), "%.2f", resposta.getNotaObtida()));
        nota.setPromptText("0,00 a " + String.format(Locale.forLanguageTag("pt-BR"), "%.2f", item.getPeso()));
        nota.setPrefColumnCount(8);

        Label intervalo = new Label("Nota do professor (0 a "
                + String.format(Locale.forLanguageTag("pt-BR"), "%.2f", item.getPeso()) + ")");
        Button salvar = new Button(resposta.getNotaObtida() == null ? "Salvar nota" : "Atualizar nota");
        salvar.getStyleClass().add("botao-primario");
        salvar.setOnAction(event -> salvarNota(entrega, item, nota));

        HBox linhaNota = new HBox(10, intervalo, nota, salvar);
        linhaNota.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        HBox.setHgrow(nota, Priority.NEVER);
        card.getChildren().addAll(titulo, enunciado, textoResposta, linhaNota);
        if (resposta.getNotaObtida() != null) {
            Label atual = new Label("Nota salva: "
                    + String.format(Locale.forLanguageTag("pt-BR"), "%.2f", resposta.getNotaObtida())
                    + " / " + String.format(Locale.forLanguageTag("pt-BR"), "%.2f", item.getPeso()));
            atual.getStyleClass().add("resultado");
            card.getChildren().add(atual);
        }
        return card;
    }

    private void salvarNota(ProvaAluno entrega, QuestaoComPeso item, TextField campoNota) {
        String valor = campoNota.getText() == null ? "" : campoNota.getText().trim().replace(',', '.');
        if (valor.isEmpty()) {
            mostrar(Alert.AlertType.WARNING, "Informe uma nota entre zero e o peso da questão.");
            campoNota.requestFocus();
            return;
        }
        final double nota;
        try {
            nota = Double.parseDouble(valor);
        } catch (NumberFormatException e) {
            mostrar(Alert.AlertType.WARNING, "Digite uma nota numérica. Você pode usar vírgula ou ponto decimal.");
            campoNota.requestFocus();
            return;
        }

        try {
            boolean provaCorrigida = provaServico.corrigirRespostaDissertativa(
                    prova.getCodigo(), entrega.getRegistroAluno(), (int) item.getQuestao().getNumero(), nota);
            mostrar(Alert.AlertType.INFORMATION, provaCorrigida
                    ? "Nota salva. Todas as respostas estão corrigidas e a nota final foi atualizada."
                    : "Nota salva. A entrega continuará aguardando a correção das outras respostas dissertativas.");
            atualizarEntregas(entrega.getRegistroAluno());
        } catch (IllegalArgumentException e) {
            mostrar(Alert.AlertType.WARNING, e.getMessage());
        } catch (RuntimeException e) {
            mostrar(Alert.AlertType.ERROR, "Não foi possível salvar a nota.\n" + e.getMessage());
        }
    }

    private Label mensagemVazia(String mensagem) {
        Label label = new Label(mensagem);
        label.getStyleClass().add("subtitulo");
        label.setWrapText(true);
        return label;
    }

    @FXML
    private void voltar(ActionEvent event) {
        try {
            telas.prova.Prova.abrir((Stage) tituloTela.getScene().getWindow());
        } catch (Exception e) {
            mostrar(Alert.AlertType.ERROR, "Não foi possível voltar à lista de provas.");
        }
    }

    private void mostrar(Alert.AlertType tipo, String mensagem) {
        Alert alerta = new Alert(tipo, mensagem);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
