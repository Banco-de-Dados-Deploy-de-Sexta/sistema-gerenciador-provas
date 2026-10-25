package telas.questao;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import modelos.Alternativa;
import modelos.Questao;
import servicos.QuestaoServico;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import telas.bancoquestoes.BancoQuestoes;
import telas.componentes.CardQuestao;
import telas.componentes.Icones;

public class QuestaoController {
    @FXML private Label numeroLabel;
    @FXML private Label tituloTela;
    @FXML private TextArea enunciadoCaixaTexto;
    @FXML private TextField perguntaCaixaTexto;
    @FXML private Label proximaLetraLabel;
    @FXML private VBox listaAlternativas;
    @FXML private VBox listaQuestoes;

    private final QuestaoServico questaoServico;
    private final List<Alternativa> alternativas = new ArrayList<>();
    private ToggleGroup grupoCorreta = new ToggleGroup();
    private long idQuestao;

    public QuestaoController(QuestaoServico questaoServico) { this.questaoServico = questaoServico; }

    public void definirQuestao(Questao questao) {
        List<Questao> questoes = questaoServico.listarQuestoes();
        if (questao != null) idQuestao = questao.getNumero();
        renderizarListaQuestoes(questoes);
        if (questao == null) {
            numeroLabel.setText((questoes.size() + 1) + ")");
            tituloTela.setText("Nova questão");
            renderizarAlternativas();
            return;
        }
        numeroLabel.setText((questoes.indexOf(questao) + 1) + ")");
        tituloTela.setText("Questão " + idQuestao);
        enunciadoCaixaTexto.setText(questao.getTitulo());
        alternativas.clear();
        alternativas.addAll(questao.getAlternativas());
        renderizarAlternativas();
    }

    // Lista lateral de questões já cadastradas; clicar abre a questão para edição
    private void renderizarListaQuestoes(List<Questao> questoes) {
        listaQuestoes.getChildren().clear();
        for (Questao questao : questoes) {
            Label numero = new Label(questao.getNumero() + ")Questão " + questao.getNumero());
            numero.getStyleClass().add("card-pequeno");
            Label pergunta = new Label(questao.getTitulo());
            pergunta.getStyleClass().add("card-texto");
            pergunta.setWrapText(true);
            pergunta.setTextOverrun(OverrunStyle.ELLIPSIS);
            pergunta.setMaxHeight(56);
            pergunta.setPadding(new Insets(10, 0, 0, 0));
            VBox card = new VBox(numero, pergunta);
            card.getStyleClass().add("card-lista");
            if (questao.getNumero() == idQuestao) card.getStyleClass().add("selecionado");
            card.setOnMouseClicked(event -> abrirQuestao(questao));
            listaQuestoes.getChildren().add(card);
        }
    }

    private void abrirQuestao(Questao questao) {
        try { telas.questao.Questao.abrir((Stage) listaQuestoes.getScene().getWindow(), questao); }
        catch (Exception e) { mostrar(Alert.AlertType.ERROR, "Não foi possível abrir a questão."); }
    }

    @FXML
    private void adicionarAlternativa() {
        String texto = perguntaCaixaTexto.getText().trim();
        if (texto.isEmpty()) {
            perguntaCaixaTexto.requestFocus();
            return;
        }
        if (texto.length() > 100) {
            mostrar(Alert.AlertType.WARNING, "Cada alternativa pode ter até 100 caracteres.");
            return;
        }
        alternativas.add(new Alternativa(alternativas.size() + 1, texto, false));
        perguntaCaixaTexto.clear();
        renderizarAlternativas();
        perguntaCaixaTexto.requestFocus();
    }

    private void renderizarAlternativas() {
        listaAlternativas.getChildren().clear();
        grupoCorreta = new ToggleGroup();
        for (int i = 0; i < alternativas.size(); i++) {
            Alternativa alternativa = alternativas.get(i);
            alternativa.setId(i + 1);
            RadioButton correta = new RadioButton();
            correta.setToggleGroup(grupoCorreta);
            correta.setUserData(alternativa);
            correta.setOnAction(event -> alternativas.forEach(item -> item.setCorreta(item == alternativa)));
            if (alternativa.isCorreta()) correta.setSelected(true);

            Label letra = new Label(CardQuestao.letra(i));
            letra.getStyleClass().add("alternativa-letra");
            Label texto = new Label(alternativa.getTexto());
            texto.setWrapText(true);
            texto.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(texto, Priority.ALWAYS);

            Button remover = new Button(null, Icones.criar(Icones.CANCELAR, 0.6));
            remover.getStyleClass().add("botao-icone");
            int indice = i;
            remover.setOnAction(event -> {
                alternativas.remove(indice);
                renderizarAlternativas();
            });

            HBox linha = new HBox(6, correta, letra, texto, remover);
            linha.setAlignment(Pos.CENTER_LEFT);
            listaAlternativas.getChildren().add(linha);
        }
        proximaLetraLabel.setText(CardQuestao.letra(alternativas.size()));
    }

    @FXML
    private void confirmar() {
        String enunciado = enunciadoCaixaTexto.getText().trim();
        Toggle marcada = grupoCorreta.getSelectedToggle();
        if (enunciado.isEmpty()) {
            mostrar(Alert.AlertType.WARNING, "Digite a pergunta da questão.");
            return;
        }
        if (marcada == null) {
            mostrar(Alert.AlertType.WARNING, "Marque a alternativa correta.");
            return;
        }

        Alternativa selecionada = (Alternativa) marcada.getUserData();
        List<Alternativa> paraSalvar = new ArrayList<>();
        for (int i = 0; i < alternativas.size(); i++) {
            Alternativa atual = alternativas.get(i);
            paraSalvar.add(new Alternativa(i + 1, atual.getTexto().trim(), atual == selecionada));
        }
        Questao questao = new Questao(idQuestao, enunciado, "Assinale a alternativa correta:", paraSalvar);
        try {
            questaoServico.salvarQuestao(questao);
            mostrar(Alert.AlertType.INFORMATION, "Questão e alternativas salvas.");
            voltarParaBanco();
        } catch (IllegalArgumentException e) {
            mostrar(Alert.AlertType.WARNING, e.getMessage());
        } catch (RuntimeException e) {
            mostrar(Alert.AlertType.ERROR, "Não foi possível salvar a questão.\n" + e.getMessage());
        }
    }

    // Questão já salva: exclui do banco. Questão nova: descarta o que foi digitado
    @FXML
    private void removerQuestao() {
        boolean salva = idQuestao > 0;
        String mensagem = salva
                ? "Excluir esta questão? Ela também será removida das provas em que estiver vinculada."
                : "Descartar esta questão? O que foi digitado será perdido.";
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION, mensagem, ButtonType.CANCEL, ButtonType.OK);
        confirmacao.setHeaderText("Remover questão");
        if (confirmacao.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        try {
            if (salva) questaoServico.excluirQuestao(idQuestao);
            voltarParaBanco();
        } catch (RuntimeException e) {
            mostrar(Alert.AlertType.ERROR, "Não foi possível excluir a questão.\n" + e.getMessage());
        }
    }

    @FXML
    private void voltar(ActionEvent event) { voltarParaBanco(); }

    private void voltarParaBanco() {
        try { BancoQuestoes.abrir((Stage) enunciadoCaixaTexto.getScene().getWindow()); }
        catch (Exception e) { mostrar(Alert.AlertType.ERROR, "Não foi possível voltar ao banco de questões."); }
    }

    private void mostrar(Alert.AlertType tipo, String mensagem) {
        Alert alerta = new Alert(tipo, mensagem);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
