package telas.alunoprova;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modelos.Prova;
import modelos.Usuario;
import servicos.ProvaServico;
import storage.UsuarioStorage;

public class AlunoProvaController {
    @FXML
    private Label tituloTela;
    @FXML
    private Label subtituloTela;
    @FXML
    private VBox listaAlunos;

    private final Prova prova;
    private final ProvaServico provaServico;
    private final UsuarioStorage usuarioStorage;
    // registro do aluno -> caixa de seleção correspondente, na ordem de exibição
    private final Map<String, CheckBox> selecoes = new LinkedHashMap<>();

    public AlunoProvaController(Prova prova, ProvaServico provaServico, UsuarioStorage usuarioStorage) {
        this.prova = prova;
        this.provaServico = provaServico;
        this.usuarioStorage = usuarioStorage;
    }

    public void carregar() {
        tituloTela.setText("Alunos da prova — " + prova.getTitulo());
        Set<String> jaAplicados = new HashSet<>(provaServico.listarAlunos(prova.getCodigo()));

        listaAlunos.getChildren().clear();
        selecoes.clear();
        for (Usuario aluno : usuarioStorage.listarAlunos()) {
            CheckBox selecionado = new CheckBox(aluno.getRegistro());
            selecionado.selectedProperty().addListener((obs, antes, agora) -> atualizarSubtitulo());
            selecionado.setSelected(jaAplicados.contains(aluno.getRegistro()));
            Label nome = new Label(aluno.getNome());
            nome.setWrapText(true);
            nome.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(nome, Priority.ALWAYS);
            HBox linha = new HBox(12, selecionado, nome);
            linha.getStyleClass().add("question-row");
            selecoes.put(aluno.getRegistro(), selecionado);
            listaAlunos.getChildren().add(linha);
        }
        atualizarSubtitulo();
        if (listaAlunos.getChildren().isEmpty()) {
            Label vazio = new Label("Nenhum aluno cadastrado. Cadastre alunos antes de aplicar a prova.");
            vazio.getStyleClass().add("subtitulo");
            listaAlunos.getChildren().add(vazio);
        }
    }

    private void atualizarSubtitulo() {
        long marcados = selecoes.values().stream().filter(CheckBox::isSelected).count();
        subtituloTela.setText(marcados == 0
                ? "Marque os alunos que farão parte da prova. Nenhum aluno vinculado."
                : "Marque os alunos que farão parte da prova. " + marcados + " de " + selecoes.size()
                        + " alunos selecionados.");
    }

    @FXML
    private void marcarTodos() {
        selecoes.values().forEach(caixa -> caixa.setSelected(true));
    }

    @FXML
    private void desmarcarTodos() {
        selecoes.values().forEach(caixa -> caixa.setSelected(false));
    }

    @FXML
    private void salvar() {
        List<String> selecionados = new ArrayList<>();
        for (Map.Entry<String, CheckBox> entrada : selecoes.entrySet())
            if (entrada.getValue().isSelected())
                selecionados.add(entrada.getKey());
        try {
            provaServico.salvarAlunos(prova.getCodigo(), selecionados);
            mostrar(Alert.AlertType.INFORMATION, selecionados.isEmpty()
                    ? "Nenhum aluno vinculado à prova."
                    : "Alunos da prova salvos.");
            voltarParaProvas();
        } catch (IllegalArgumentException e) {
            mostrar(Alert.AlertType.WARNING, e.getMessage());
        } catch (RuntimeException e) {
            mostrar(Alert.AlertType.ERROR, "Não foi possível salvar os alunos da prova.\n" + e.getMessage());
        }
    }

    @FXML
    private void voltar(ActionEvent event) {
        voltarParaProvas();
    }

    private void voltarParaProvas() {
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
