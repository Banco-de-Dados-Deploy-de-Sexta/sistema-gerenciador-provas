package telas.aluno;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import auxiliares.Sessao;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modelos.Prova;
import modelos.Usuario;
import servicos.ProvaServico;
import servicos.QuestaoServico;

public class AlunoController {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());

    @FXML
    private Label nomeUsuario;
    @FXML
    private Label registroUsuario;
    @FXML
    private Button realizarButton;
    @FXML
    private VBox listaProvas;
    @FXML
    private VBox questoesProva;

    private final ProvaServico provaServico;
    private final QuestaoServico questaoServico;
    private final ObjectProperty<Prova> selecionada = new SimpleObjectProperty<>();

    public AlunoController(ProvaServico provaServico, QuestaoServico questaoServico) {
        this.provaServico = provaServico;
        this.questaoServico = questaoServico;
    }

    public void initialize() {
        Usuario usuario = Sessao.getUsuarioLogado();
        if (usuario != null) {
            nomeUsuario.setText(usuario.getNome());
            registroUsuario.setText("RA: " + usuario.getRegistro());
        }

        atualizarBotaoRealizar(selecionada.get());
        selecionada.addListener((obs, antes, agora) -> {
            destacarSelecionada();
            mostrarDetalhesProva();
            atualizarBotaoRealizar(agora);
        });

        atualizarLista();
    }

    public void atualizarLista() {
        listaProvas.getChildren().clear();
        Usuario usuario = Sessao.getUsuarioLogado();
        if (usuario == null || provaServico == null) {
            return;
        }

        List<Prova> provas = provaServico.listarProvasPorAluno(usuario.getRegistro());
        for (Prova prova : provas) {
            listaProvas.getChildren().add(criarCardProva(prova));
        }

        if (provas.isEmpty()) {
            Label vazio = new Label("Nenhuma prova associada a você no momento.");
            vazio.getStyleClass().add("subtitulo");
            vazio.setWrapText(true);
            vazio.setPadding(new Insets(15));
            listaProvas.getChildren().add(vazio);
            selecionada.set(null);
        } else if (selecionada.get() == null || !provas.contains(selecionada.get())) {
            selecionada.set(provas.get(0));
        } else {
            destacarSelecionada();
            mostrarDetalhesProva();
        }
    }

    private Node criarCardProva(Prova prova) {
        Label codigo = new Label(prova.getCodigo());
        codigo.getStyleClass().add("card-pequeno");

        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);

        HBox topo = new HBox(codigo, espaco);

        Label titulo = new Label(prova.getTitulo());
        titulo.getStyleClass().add("card-titulo");

        Label detalhes = new Label(descricao(prova));
        detalhes.getStyleClass().add("card-pequeno");
        detalhes.setWrapText(true);

        int totalQuestoes = provaServico.contarQuestoes(prova.getCodigo());
        Label questoes = new Label(totalQuestoes + (totalQuestoes == 1 ? " questão" : " questões"));
        questoes.getStyleClass().add("card-destaque");

        Region espacoRodape = new Region();
        HBox.setHgrow(espacoRodape, Priority.ALWAYS);

        Usuario usuario = Sessao.getUsuarioLogado();
        String estadoTexto = usuario != null
                ? provaServico.estadoParaAluno(prova, usuario.getRegistro())
                : provaServico.estadoParaAluno(prova);
        Label estado = new Label(estadoTexto);
        estado.getStyleClass().add("card-estado");

        HBox rodape = new HBox(questoes, espacoRodape, estado);
        rodape.setAlignment(Pos.CENTER_LEFT);
        rodape.setPadding(new Insets(16, 0, 0, 0));

        VBox card = new VBox(topo, titulo, detalhes, rodape);
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
            if (marcada) {
                card.getStyleClass().add("selecionado");
            }
        }
    }

    private void mostrarDetalhesProva() {
        questoesProva.getChildren().clear();
        Prova prova = selecionada.get();
        if (prova == null) {
            Label vazio = new Label("Selecione uma prova para ver os detalhes.");
            vazio.getStyleClass().add("vazio");
            questoesProva.getChildren().add(vazio);
            return;
        }

        VBox painel = new VBox(14);
        painel.getStyleClass().add("card");
        painel.setStyle("-fx-padding: 20; -fx-background-color: white; -fx-background-radius: 8;");

        Label titulo = new Label(prova.getTitulo());
        titulo.getStyleClass().add("titulo");
        titulo.setStyle("-fx-font-size: 20px;");

        Label materiaCurso = new Label("Disciplina: " + prova.getMateria()
                + (prova.getCurso() != null && !prova.getCurso().isBlank() ? " | Curso: " + prova.getCurso() : "")
                + " (" + prova.getBimestre() + "º Bimestre)");
        materiaCurso.getStyleClass().add("subtitulo");

        int totalQuestoes = provaServico.contarQuestoes(prova.getCodigo());
        Label qtd = new Label("Total de questões: " + totalQuestoes);
        qtd.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151;");

        String dataTexto = "Não agendada";
        if (prova.getDataHora() != null) {
            dataTexto = prova.getDataHora().format(FORMATO_DATA) + " das " + prova.getDataHora().format(FORMATO_HORA);
            if (prova.getDataHoraFim() != null) {
                dataTexto += " às " + prova.getDataHoraFim().format(FORMATO_HORA);
            }
        } else if (prova.getData() != null) {
            dataTexto = prova.getData().format(FORMATO_DATA);
        }
        Label data = new Label("Data e Horário: " + dataTexto);
        data.setStyle("-fx-text-fill: #4b5563;");

        Usuario usuario = Sessao.getUsuarioLogado();
        String estadoTexto = usuario != null
                ? provaServico.estadoParaAluno(prova, usuario.getRegistro())
                : provaServico.estadoParaAluno(prova);
        Label status = new Label("Status: " + estadoTexto);
        status.setStyle("-fx-font-weight: bold; -fx-text-fill: #1d4ed8;");

        painel.getChildren().addAll(titulo, materiaCurso, qtd, data, status);

        if (usuario != null && provaServico.alunoJaRealizou(prova.getCodigo(), usuario.getRegistro())) {
            modelos.ProvaAluno realizacao = provaServico.buscarRealizacaoAluno(prova.getCodigo(), usuario.getRegistro());
            if (realizacao != null && realizacao.getNotaFinal() != null) {
                Label nota = new Label("Sua Nota: " + String.format(Locale.US, "%.2f", realizacao.getNotaFinal()) + " / 10.00");
                nota.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #059669;");
                painel.getChildren().add(nota);
            }
            Label msgRealizada = new Label("Você já realizou e enviou esta prova.");
            msgRealizada.setStyle("-fx-text-fill: #059669; -fx-font-weight: bold;");
            painel.getChildren().add(msgRealizada);
        } else {
            Label aviso = new Label("As questões estarão disponíveis para realização no dia e horário agendados.");
            aviso.setStyle("-fx-text-fill: #6b7280; -fx-font-style: italic;");
            aviso.setWrapText(true);
            painel.getChildren().add(aviso);
        }
        questoesProva.getChildren().add(painel);
    }

    private void atualizarBotaoRealizar(Prova prova) {
        if (prova == null) {
            realizarButton.setDisable(true);
            return;
        }
        Usuario usuario = Sessao.getUsuarioLogado();
        if (usuario != null && provaServico.alunoJaRealizou(prova.getCodigo(), usuario.getRegistro())) {
            realizarButton.setDisable(true);
            return;
        }
        String estado = usuario != null
                ? provaServico.estadoParaAluno(prova, usuario.getRegistro())
                : provaServico.estadoParaAluno(prova);
        boolean disponivel = "Disponível".equalsIgnoreCase(estado);
        realizarButton.setDisable(!disponivel);
    }

    @FXML
    private void realizarProva(ActionEvent event) {
        Prova prova = selecionada.get();
        if (prova == null) {
            return;
        }
        Usuario usuario = Sessao.getUsuarioLogado();
        if (usuario != null && provaServico.alunoJaRealizou(prova.getCodigo(), usuario.getRegistro())) {
            mostrarAlerta(Alert.AlertType.WARNING, "Você já realizou esta prova anteriormente.");
            return;
        }
        String estado = usuario != null
                ? provaServico.estadoParaAluno(prova, usuario.getRegistro())
                : provaServico.estadoParaAluno(prova);
        if (!"Disponível".equalsIgnoreCase(estado)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Esta prova só pode ser realizada quando estiver no estado 'Disponível'.");
            return;
        }
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            telas.realizarprova.RealizarProva.abrir(stage, prova, provaServico);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Não foi possível abrir a tela de realização da prova: " + e.getMessage());
        }
    }

    @FXML
    private void voltar(ActionEvent event) {
        try {
            Sessao.encerrar();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            telas.login.Login.abrir(stage);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro ao retornar para a tela de login: " + e.getMessage());
        }
    }

    // implementar corretamente novo botao do fxml para dar refresh
    @FXML
    private void atualizarProvas() {
        atualizarLista();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensagem) {
        Alert alerta = new Alert(tipo, mensagem);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
