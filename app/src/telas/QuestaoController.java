package telas;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import modelos.Alternativa;
import modelos.Questao;
import servicos.QuestaoServico;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class QuestaoController {
    @FXML private Label tituloTela;
    @FXML private TextField enunciadoCaixaTexto;
    @FXML private TextField perguntaCaixaTexto;
    @FXML private GridPane gridAlternativas;

    private final QuestaoServico questaoServico;
    private final List<Alternativa> alternativas = new ArrayList<>();
    private ToggleGroup grupoCorreta = new ToggleGroup();
    private long idQuestao;

    public QuestaoController(QuestaoServico questaoServico) { this.questaoServico = questaoServico; }

    public void definirQuestao(Questao questao) {
        if (questao == null) {
            tituloTela.setText("Nova questão");
            return;
        }
        idQuestao = questao.getNumero();
        tituloTela.setText("Editar questão #" + idQuestao);
        enunciadoCaixaTexto.setText(questao.getTitulo());
        alternativas.clear();
        alternativas.addAll(questao.getAlternativas());
        renderizarAlternativas();
    }

    @FXML
    private void adicionarAlternativa() {
        String texto = perguntaCaixaTexto.getText().trim();
        if (texto.isEmpty()) return;
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
        gridAlternativas.getChildren().clear();
        grupoCorreta = new ToggleGroup();
        for (int i = 0; i < alternativas.size(); i++) {
            Alternativa alternativa = alternativas.get(i);
            alternativa.setId(i + 1);
            RadioButton correta = new RadioButton(String.valueOf((char) ('A' + i)));
            correta.setToggleGroup(grupoCorreta);
            correta.setUserData(alternativa);
            correta.setOnAction(event -> alternativas.forEach(item -> item.setCorreta(item == alternativa)));
            if (alternativa.isCorreta()) correta.setSelected(true);
            gridAlternativas.add(correta, 0, i);

            Label texto = new Label(alternativa.getTexto());
            texto.setWrapText(true);
            gridAlternativas.add(texto, 1, i);

            Button remover = new Button("Remover");
            remover.getStyleClass().add("botao-remover");
            int indice = i;
            remover.setOnAction(event -> {
                alternativas.remove(indice);
                renderizarAlternativas();
            });
            gridAlternativas.add(remover, 2, i);
        }
    }

    @FXML
    private void confirmar() {
        String enunciado = enunciadoCaixaTexto.getText().trim();
        Toggle marcada = grupoCorreta.getSelectedToggle();
        if (enunciado.isEmpty()) {
            mostrar(Alert.AlertType.WARNING, "Digite o enunciado da questão.");
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
