package telas.resultadosprova;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import modelos.Prova;
import modelos.ProvaAluno;
import modelos.Usuario;
import servicos.ProvaServico;
import storage.UsuarioStorage;

public class ResultadosProvaController {
    @FXML private Label tituloTela;
    @FXML private Label mediaLabel;
    @FXML private Label maiorNotaLabel;
    @FXML private Label menorNotaLabel;
    @FXML private Label resumoLabel;
    @FXML private TableView<LinhaResultado> tabelaResultados;
    @FXML private TableColumn<LinhaResultado, String> registroColumn;
    @FXML private TableColumn<LinhaResultado, String> nomeColumn;
    @FXML private TableColumn<LinhaResultado, String> notaColumn;

    private final Prova prova;
    private final ProvaServico provaServico;
    private final UsuarioStorage usuarioStorage;

    public ResultadosProvaController(Prova prova, ProvaServico provaServico, UsuarioStorage usuarioStorage) {
        this.prova = prova;
        this.provaServico = provaServico;
        this.usuarioStorage = usuarioStorage;
    }

    public void carregar() {
        tituloTela.setText("Resultados — " + prova.getTitulo());
        registroColumn.setCellValueFactory(celula -> new ReadOnlyStringWrapper(celula.getValue().getRegistro()));
        nomeColumn.setCellValueFactory(celula -> new ReadOnlyStringWrapper(celula.getValue().getNome()));
        notaColumn.setCellValueFactory(celula -> new ReadOnlyStringWrapper(celula.getValue().getNota()));
        tabelaResultados.getStyleClass().add("results-table");
        tabelaResultados.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tabelaResultados.setPlaceholder(new Label("Nenhum aluno foi vinculado a esta prova."));
        tabelaResultados.setRowFactory(tabela -> {
            TableRow<LinhaResultado> linha = new TableRow<>();
            linha.setStyle("-fx-cursor: hand;");
            linha.setOnMouseClicked(event -> {
                if (!linha.isEmpty() && event.getButton() == javafx.scene.input.MouseButton.PRIMARY
                        && event.getClickCount() == 1) {
                    abrirDetalhes(linha.getItem());
                }
            });
            return linha;
        });

        List<ProvaAluno> inscritos = provaServico.listarResultadosProva(prova.getCodigo());
        Map<String, String> nomes = carregarNomesAlunos();
        List<LinhaResultado> linhas = new ArrayList<>();
        List<Double> notas = new ArrayList<>();

        for (ProvaAluno inscrito : inscritos) {
            String registro = inscrito.getRegistroAluno();
            String nome = nomes.getOrDefault(registro, "Nome não encontrado");
            Double nota = inscrito.getNotaFinal();
            String notaExibida = nota != null
                    ? formatarNota(nota)
                    : inscrito.isRealizada() ? "Aguardando correção" : "Não realizada";
            linhas.add(new LinhaResultado(registro, nome, notaExibida));
            if (nota != null) notas.add(nota);
        }

        tabelaResultados.getItems().setAll(linhas);
        atualizarIndicadores(notas, inscritos.size());
    }

    private Map<String, String> carregarNomesAlunos() {
        if (usuarioStorage == null) return Map.of();
        return usuarioStorage.listarAlunos().stream().collect(Collectors.toMap(
                Usuario::getRegistro,
                Usuario::getNome,
                (nomeExistente, ignorado) -> nomeExistente,
                HashMap::new));
    }

    private void atualizarIndicadores(List<Double> notas, int totalAlunos) {
        if (notas.isEmpty()) {
            mediaLabel.setText("—");
            maiorNotaLabel.setText("—");
            menorNotaLabel.setText("—");
            resumoLabel.setText(totalAlunos == 0
                    ? "Nenhum aluno vinculado a esta prova."
                    : "Nenhuma nota lançada até o momento.");
            return;
        }

        double media = notas.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double maior = notas.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
        double menor = notas.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
        mediaLabel.setText(formatarNota(media));
        maiorNotaLabel.setText(formatarNota(maior));
        menorNotaLabel.setText(formatarNota(menor));
        resumoLabel.setText(notas.size() + (notas.size() == 1 ? " nota lançada" : " notas lançadas")
                + " de " + totalAlunos + (totalAlunos == 1 ? " aluno." : " alunos."));
    }

    private String formatarNota(double nota) {
        return String.format(Locale.forLanguageTag("pt-BR"), "%.2f", nota);
    }

    private void abrirDetalhes(LinhaResultado aluno) {
        try {
            DetalheResultado.abrir((Stage) tituloTela.getScene().getWindow(), prova, aluno.getRegistro(),
                    aluno.getNome(), provaServico, usuarioStorage);
        } catch (Exception e) {
            javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR,
                    "Não foi possível abrir o detalhamento da prova.\n" + e.getMessage());
            alerta.setHeaderText(null);
            alerta.showAndWait();
        }
    }

    @FXML
    private void voltar(ActionEvent event) {
        try {
            telas.prova.Prova.abrir((Stage) tituloTela.getScene().getWindow());
        } catch (Exception e) {
            ((Stage) tituloTela.getScene().getWindow()).close();
        }
    }

    public static class LinhaResultado {
        private final String registro;
        private final String nome;
        private final String nota;

        public LinhaResultado(String registro, String nome, String nota) {
            this.registro = registro;
            this.nome = nome;
            this.nota = nota;
        }

        public String getRegistro() { return registro; }
        public String getNome() { return nome; }
        public String getNota() { return nota; }
    }
}
