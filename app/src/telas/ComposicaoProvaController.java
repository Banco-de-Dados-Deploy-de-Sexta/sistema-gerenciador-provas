package telas;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modelos.PesoQuestao;
import modelos.Prova;
import modelos.Questao;
import modelos.QuestaoComPeso;
import servicos.ProvaServico;
import servicos.QuestaoServico;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComposicaoProvaController {
    @FXML private Label tituloTela;
    @FXML private VBox listaQuestoes;

    private final Prova prova;
    private final ProvaServico provaServico;
    private final QuestaoServico questaoServico;
    private final Map<Long, CheckBox> selecoes = new HashMap<>();
    private final Map<Long, TextField> pesos = new HashMap<>();

    public ComposicaoProvaController(Prova prova, ProvaServico provaServico, QuestaoServico questaoServico) {
        this.prova = prova;
        this.provaServico = provaServico;
        this.questaoServico = questaoServico;
    }

    public void carregar() {
        tituloTela.setText("Questões e pesos — " + prova.getTitulo());
        Map<Long, Double> pesoAtual = new HashMap<>();
        for (QuestaoComPeso item : provaServico.listarQuestoes(prova.getCodigo()))
            pesoAtual.put(item.getQuestao().getNumero(), item.getPeso());

        listaQuestoes.getChildren().clear();
        selecoes.clear();
        pesos.clear();
        for (Questao questao : questaoServico.listarQuestoes()) {
            CheckBox selecionada = new CheckBox("#" + questao.getNumero());
            selecionada.setSelected(pesoAtual.containsKey(questao.getNumero()));
            Label enunciado = new Label(questao.getTitulo());
            enunciado.setWrapText(true);
            TextField peso = new TextField(String.valueOf(pesoAtual.getOrDefault(questao.getNumero(), 1.0)));
            peso.setPromptText("Peso");
            peso.setPrefWidth(90);
            HBox linha = new HBox(12, selecionada, enunciado, peso);
            linha.getStyleClass().add("question-row");
            enunciado.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(enunciado, javafx.scene.layout.Priority.ALWAYS);
            selecoes.put(questao.getNumero(), selecionada);
            pesos.put(questao.getNumero(), peso);
            listaQuestoes.getChildren().add(linha);
        }
        if (listaQuestoes.getChildren().isEmpty()) {
            Label vazio = new Label("Nenhuma questão cadastrada. Cadastre questões antes de montar a prova.");
            vazio.getStyleClass().add("subtitulo");
            listaQuestoes.getChildren().add(vazio);
        }
    }

    @FXML
    private void salvar() {
        List<PesoQuestao> selecionadas = new ArrayList<>();
        try {
            for (Map.Entry<Long, CheckBox> entrada : selecoes.entrySet()) {
                if (!entrada.getValue().isSelected()) continue;
                double peso = Double.parseDouble(pesos.get(entrada.getKey()).getText().trim().replace(',', '.'));
                selecionadas.add(new PesoQuestao(entrada.getKey(), peso));
            }
            provaServico.salvarQuestoes(prova.getCodigo(), selecionadas);
            mostrar(Alert.AlertType.INFORMATION, "Questões e pesos salvos.");
            voltarParaProvas();
        } catch (NumberFormatException e) {
            mostrar(Alert.AlertType.WARNING, "Informe um peso numérico para cada questão selecionada.");
        } catch (IllegalArgumentException e) {
            mostrar(Alert.AlertType.WARNING, e.getMessage());
        } catch (RuntimeException e) {
            mostrar(Alert.AlertType.ERROR, "Não foi possível salvar as questões da prova.\n" + e.getMessage());
        }
    }

    @FXML private void voltar(ActionEvent event) { voltarParaProvas(); }

    private void voltarParaProvas() {
        try { telas.Prova.abrir((Stage) tituloTela.getScene().getWindow()); }
        catch (Exception e) { mostrar(Alert.AlertType.ERROR, "Não foi possível voltar à lista de provas."); }
    }

    private void mostrar(Alert.AlertType tipo, String mensagem) {
        Alert alerta = new Alert(tipo, mensagem);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
