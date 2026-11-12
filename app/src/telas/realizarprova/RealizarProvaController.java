package telas.realizarprova;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import auxiliares.Sessao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modelos.Alternativa;
import modelos.Prova;
import modelos.QuestaoComPeso;
import modelos.RespostaAluno;
import modelos.Usuario;
import servicos.ProvaServico;
import telas.componentes.CardQuestao;

public class RealizarProvaController {

    @FXML
    private Label tituloTela;
    @FXML
    private Label subtituloTela;
    @FXML
    private VBox listaQuestoes;
    @FXML
    private Button btnEntregar;

    private final Prova prova;
    private final ProvaServico provaServico;

    // Guarda mapeamentos das respostas inseridas pelo aluno
    private final Map<Integer, ToggleGroup> respostasMultiplaEscolha = new HashMap<>();
    private final Map<Integer, TextArea> respostasDissertativas = new HashMap<>();
    private final List<QuestaoComPeso> questoesExibidas = new ArrayList<>();

    public RealizarProvaController(Prova prova, ProvaServico provaServico) {
        this.prova = prova;
        this.provaServico = provaServico;
    }

    public void carregar() {
        tituloTela.setText("Realizando: " + prova.getTitulo());
        String infoData = prova.getDataHora() != null 
                ? " | Agendada para: " + prova.getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : "";
        subtituloTela.setText(prova.getMateria() + " (" + prova.getBimestre() + "º Bimestre)" + infoData);

        listaQuestoes.getChildren().clear();
        respostasMultiplaEscolha.clear();
        respostasDissertativas.clear();
        questoesExibidas.clear();

        List<QuestaoComPeso> questoes = provaServico.listarQuestoes(prova.getCodigo());
        // Embaralha as questões
        Collections.shuffle(questoes);
        questoesExibidas.addAll(questoes);

        if (questoes.isEmpty()) {
            Label vazio = new Label("Esta prova ainda não possui questões cadastradas.");
            vazio.getStyleClass().add("subtitulo");
            listaQuestoes.getChildren().add(vazio);
            btnEntregar.setDisable(true);
            return;
        }

        for (int i = 0; i < questoes.size(); i++) {
            QuestaoComPeso item = questoes.get(i);
            int idQuestao = (int) item.getQuestao().getNumero();

            VBox cardQuestao = new VBox(8);
            cardQuestao.getStyleClass().add("card");
            cardQuestao.setStyle("-fx-padding: 16; -fx-background-color: white; -fx-background-radius: 8;");

            Label lblNumeroEPeso = new Label("Questão " + (i + 1) + " (Peso: " + item.getPeso() + ")");
            lblNumeroEPeso.setStyle("-fx-font-weight: bold; -fx-text-fill: #1d4ed8; -fx-font-size: 14px;");

            Label lblTitulo = new Label(item.getQuestao().getTitulo());
            lblTitulo.getStyleClass().add("card-titulo");
            lblTitulo.setWrapText(true);

            Label lblDescricao = new Label(item.getQuestao().getDescricao());
            lblDescricao.getStyleClass().add("card-pequeno");
            lblDescricao.setWrapText(true);

            cardQuestao.getChildren().addAll(lblNumeroEPeso, lblTitulo, lblDescricao);

            List<Alternativa> alternativas = item.getQuestao().getAlternativas();
            if (alternativas != null && !alternativas.isEmpty()) {
                // Questão de alternativas (múltipla escolha)
                ToggleGroup grupo = new ToggleGroup();
                // Embaralha as alternativas
                List<Alternativa> alternativasEmbaralhadas = new ArrayList<>(alternativas);
                Collections.shuffle(alternativasEmbaralhadas);
                for (int j = 0; j < alternativasEmbaralhadas.size(); j++) {
                    Alternativa alt = alternativasEmbaralhadas.get(j);
                    RadioButton rb = new RadioButton(CardQuestao.letra(j) + " " + alt.getTexto());
                    rb.setWrapText(true);
                    rb.setToggleGroup(grupo);
                    rb.setUserData(alt.getId());
                    cardQuestao.getChildren().add(rb);
                }
                respostasMultiplaEscolha.put(idQuestao, grupo);
            } else {
                // Dissertativa / Texto livre
                TextArea txtArea = new TextArea();
                txtArea.setPromptText("Digite sua resposta aqui...");
                txtArea.setPrefRowCount(3);
                txtArea.setWrapText(true);
                cardQuestao.getChildren().add(txtArea);
                respostasDissertativas.put(idQuestao, txtArea);
            }

            listaQuestoes.getChildren().add(cardQuestao);
        }
    }

    @FXML
    private void limparRespostas() {
        respostasMultiplaEscolha.values().forEach(g -> g.selectToggle(null));
        respostasDissertativas.values().forEach(t -> t.setText(""));
    }

    @FXML
    private void cancelar(ActionEvent event) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, "Deseja realmente sair? As respostas não salvas serão perdidas.");
        alerta.setHeaderText(null);
        Optional<ButtonType> resp = alerta.showAndWait();
        if (resp.isPresent() && resp.get() == ButtonType.OK) {
            fecharTela();
        }
    }

    @FXML
    private void entregar(ActionEvent event) {
        Usuario usuario = Sessao.getUsuarioLogado();
        if (usuario == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Sessão expirada. Faça login novamente.");
            return;
        }

        // Validação da regra da PB09: verificar questões em branco
        int totalQuestoes = questoesExibidas.size();
        int respondidas = 0;
        List<RespostaAluno> listaParaSalvar = new ArrayList<>();

        for (int i = 0; i < totalQuestoes; i++) {
            QuestaoComPeso item = questoesExibidas.get(i);
            int idQuestao = (int) item.getQuestao().getNumero();

            RespostaAluno ra = new RespostaAluno();
            ra.setIdQuestao(idQuestao);
            ra.setOrdemQuestao(i + 1);

            if (respostasMultiplaEscolha.containsKey(idQuestao)) {
                ToggleGroup grupo = respostasMultiplaEscolha.get(idQuestao);
                Toggle selecionado = grupo.getSelectedToggle();
                if (selecionado != null && selecionado.getUserData() != null) {
                    ra.setIdAlternativaEscolhida((Integer) selecionado.getUserData());
                    respondidas++;
                }
            } else if (respostasDissertativas.containsKey(idQuestao)) {
                String txt = respostasDissertativas.get(idQuestao).getText();
                if (txt != null && !txt.trim().isEmpty()) {
                    ra.setRespostaTexto(txt.trim());
                    respondidas++;
                }
            }
            listaParaSalvar.add(ra);
        }

        int emBranco = totalQuestoes - respondidas;
        if (emBranco > 0) {
            Alert confirmacao = new Alert(Alert.AlertType.WARNING);
            confirmacao.setTitle("Atenção - Questões sem resposta");
            confirmacao.setHeaderText("Você possui " + emBranco + " questão(ões) sem resposta!");
            confirmacao.setContentText("Questões não respondidas serão consideradas incorretas (nota zero).\n\nDeseja realmente entregar a prova agora?");

            ButtonType btnSim = new ButtonType("Sim, entregar agora", ButtonBar.ButtonData.OK_DONE);
            ButtonType btnVoltar = new ButtonType("Voltar e revisar", ButtonBar.ButtonData.CANCEL_CLOSE);
            confirmacao.getButtonTypes().setAll(btnSim, btnVoltar);

            Optional<ButtonType> resposta = confirmacao.showAndWait();
            if (resposta.isEmpty() || resposta.get() != btnSim) {
                return;
            }
        } else {
            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setTitle("Confirmar entrega");
            confirmacao.setHeaderText("Todas as questões foram respondidas.");
            confirmacao.setContentText("Após a entrega, suas respostas não poderão mais ser alteradas. Deseja finalizar?");
            Optional<ButtonType> resposta = confirmacao.showAndWait();
            if (resposta.isEmpty() || resposta.get() != ButtonType.OK) {
                return;
            }
        }

        // Submissão
        try {
            provaServico.entregarProva(prova.getCodigo(), usuario.getRegistro(), listaParaSalvar);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Prova entregue com sucesso!");
            fecharTela();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Não foi possível entregar a prova: " + e.getMessage());
        }
    }

    private void fecharTela() {
        Stage palco = (Stage) tituloTela.getScene().getWindow();
        try {
            telas.aluno.Aluno.abrir(palco);
        } catch (Exception e) {
            palco.close();
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensagem) {
        Alert alerta = new Alert(tipo, mensagem);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
