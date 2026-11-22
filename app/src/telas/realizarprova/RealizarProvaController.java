package telas.realizarprova;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import auxiliares.Embaralhamento;
import auxiliares.Sessao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
    private static final String TIPO_CORRESPONDENCIA = "Correspondência";
    private static final String TIPO_VERDADEIRO_FALSO_SEQUENCIA = "Verdadeiro ou falso em sequência";
    private static final String TIPO_DISSERTATIVA = "Dissertativa";

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
    private String registroAluno;

    // Guarda mapeamentos das respostas inseridas pelo aluno
    private final Map<Integer, ToggleGroup> respostasMultiplaEscolha = new HashMap<>();
    private final Map<Integer, TextArea> respostasDissertativas = new HashMap<>();
    private final Map<Integer, Map<Integer, ComboBox<Alternativa>>> respostasCorrespondencia = new HashMap<>();
    private final Map<Integer, Map<Integer, ToggleGroup>> respostasSequencia = new HashMap<>();
    private final List<QuestaoComPeso> questoesExibidas = new ArrayList<>();

    public RealizarProvaController(Prova prova, ProvaServico provaServico) {
        this.prova = prova;
        this.provaServico = provaServico;
    }

    public void carregar() {
        tituloTela.setText("Realizando: " + prova.getTitulo());
        String infoData = prova.getDataHora() != null 
                ? " | Agendada para: " + prova.getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                        + (prova.getDataHoraFim() != null
                                ? " até " + prova.getDataHoraFim().format(DateTimeFormatter.ofPattern("HH:mm"))
                                : "")
                : "";
        subtituloTela.setText(prova.getMateria() + " (" + prova.getBimestre() + "º Bimestre)" + infoData);

        listaQuestoes.getChildren().clear();
        respostasMultiplaEscolha.clear();
        respostasDissertativas.clear();
        respostasCorrespondencia.clear();
        respostasSequencia.clear();
        questoesExibidas.clear();

        Usuario aluno = Sessao.getUsuarioLogado();
        registroAluno = aluno == null ? "" : aluno.getRegistro();
        List<QuestaoComPeso> questoes = Embaralhamento.questoes(
                provaServico.listarQuestoes(prova.getCodigo()), prova.getCodigo(), registroAluno);
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
            String tipo = item.getQuestao().getDescricao();
            if (alternativas != null && TIPO_CORRESPONDENCIA.equals(tipo)) {
                renderizarCorrespondencia(cardQuestao, idQuestao, alternativas);
            } else if (alternativas != null && TIPO_VERDADEIRO_FALSO_SEQUENCIA.equals(tipo)) {
                renderizarSequencia(cardQuestao, idQuestao, alternativas);
            } else if (TIPO_DISSERTATIVA.equals(tipo)) {
                renderizarDissertativa(cardQuestao, idQuestao);
            } else if (alternativas != null && !alternativas.isEmpty()) {
                ToggleGroup grupo = new ToggleGroup();
                List<Alternativa> alternativasEmbaralhadas = Embaralhamento.alternativas(
                        alternativas, prova.getCodigo(), registroAluno, idQuestao);
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

    private void renderizarDissertativa(VBox card, int idQuestao) {
        Label instrucao = new Label("Escreva sua resposta. O professor irá corrigi-la e atribuir uma nota de zero até o peso da questão.");
        instrucao.setWrapText(true);
        TextArea resposta = new TextArea();
        resposta.setPromptText("Escreva sua resposta aqui...");
        resposta.setPrefRowCount(5);
        resposta.setWrapText(true);
        card.getChildren().addAll(instrucao, resposta);
        respostasDissertativas.put(idQuestao, resposta);
    }

    private void renderizarCorrespondencia(VBox card, int idQuestao, List<Alternativa> pares) {
        Label instrucao = new Label("Associe cada conceito à definição correspondente. Cada associação correta vale uma parte proporcional do peso da questão.");
        instrucao.setWrapText(true);
        card.getChildren().add(instrucao);

        List<Alternativa> definicoes = Embaralhamento.definicoes(pares, prova.getCodigo(), registroAluno, idQuestao);
        List<Alternativa> conceitos = Embaralhamento.conceitos(pares, prova.getCodigo(), registroAluno, idQuestao);

        Map<Integer, ComboBox<Alternativa>> mapaCombos = new HashMap<>();
        List<ComboBox<Alternativa>> selecoes = new ArrayList<>();
        boolean[] rejeitandoDuplicada = {false};
        for (int i = 0; i < conceitos.size(); i++) {
            Alternativa par = conceitos.get(i);
            Label numero = new Label((i + 1) + ".");
            numero.setStyle("-fx-font-weight: bold; -fx-text-fill: #2563eb;");
            Label conceito = new Label(par.getTexto());
            conceito.setWrapText(true);
            conceito.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(conceito, Priority.ALWAYS);

            Label seta = new Label("→");
            ComboBox<Alternativa> definicao = new ComboBox<>();
            definicao.getItems().setAll(definicoes);
            definicao.setCellFactory(lista -> celulaDefinicao());
            definicao.setButtonCell(celulaDefinicao());
            definicao.setPromptText("Selecione a definição");
            definicao.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(definicao, Priority.ALWAYS);
            selecoes.add(definicao);
            mapaCombos.put(par.getId(), definicao);

            definicao.valueProperty().addListener((obs, antiga, nova) -> {
                if (rejeitandoDuplicada[0]) return;
                boolean duplicada = nova != null && selecoes.stream()
                        .anyMatch(outra -> outra != definicao
                                && outra.getValue() != null
                                && outra.getValue().getId() == nova.getId());
                if (duplicada) {
                    rejeitandoDuplicada[0] = true;
                    definicao.setValue(antiga);
                    rejeitandoDuplicada[0] = false;
                    mostrarAlerta(Alert.AlertType.WARNING, "Cada definição só pode ser associada a um conceito.");
                }
            });

            HBox linha = new HBox(10, numero, conceito, seta, definicao);
            linha.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            linha.setStyle("-fx-padding: 8; -fx-background-color: #f8fafc; -fx-background-radius: 8;");
            card.getChildren().add(linha);
        }
        respostasCorrespondencia.put(idQuestao, mapaCombos);
    }

    private ListCell<Alternativa> celulaDefinicao() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Alternativa item, boolean vazio) {
                super.updateItem(item, vazio);
                setText(vazio || item == null ? null : item.getTextoCorrespondencia());
                setWrapText(true);
            }
        };
    }

    private void renderizarSequencia(VBox card, int idQuestao, List<Alternativa> afirmacoes) {
        Label instrucao = new Label("Marque verdadeiro ou falso para cada afirmação. Cada acerto vale uma parte proporcional do peso da questão.");
        instrucao.setWrapText(true);
        card.getChildren().add(instrucao);

        List<Alternativa> afirmacoesEmbaralhada = Embaralhamento.afirmacoes(
                afirmacoes, prova.getCodigo(), registroAluno, idQuestao);

        // Declara um map para guardar o grupo de id das afirmações
        Map<Integer, ToggleGroup> respostasPorId = new HashMap<>();

        for (int i = 0; i < afirmacoesEmbaralhada.size(); i++) {
            Alternativa afirmacao = afirmacoesEmbaralhada.get(i);
            Label numero = new Label((i + 1) + ".");
            numero.setStyle("-fx-font-weight: bold; -fx-text-fill: #2563eb;");
            Label texto = new Label(afirmacao.getTexto());
            texto.setWrapText(true);
            texto.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(texto, Priority.ALWAYS);

            ToggleGroup grupo = new ToggleGroup();
            RadioButton verdadeiro = new RadioButton("Verdadeiro");
            verdadeiro.setUserData("V");
            verdadeiro.setToggleGroup(grupo);
            RadioButton falso = new RadioButton("Falso");
            falso.setUserData("F");
            falso.setToggleGroup(grupo);

            // Guarda o grupo por id da afirmação
            respostasPorId.put(afirmacao.getId(), grupo);

            HBox linha = new HBox(12, numero, texto, verdadeiro, falso);
            linha.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            linha.setStyle("-fx-padding: 8; -fx-background-color: #f8fafc; -fx-background-radius: 8;");
            card.getChildren().add(linha);
        }
        respostasSequencia.put(idQuestao, respostasPorId);
    }

    @FXML
    private void limparRespostas() {
        respostasMultiplaEscolha.values().forEach(g -> g.selectToggle(null));
        respostasDissertativas.values().forEach(t -> t.setText(""));
        respostasCorrespondencia.values().forEach(mapa -> mapa.values().forEach(combo -> combo.setValue(null)));
        respostasSequencia.values().forEach(mapa -> mapa.values().forEach(grupo -> grupo.selectToggle(null)));
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

            if (respostasCorrespondencia.containsKey(idQuestao)) {
                Map<Integer, ComboBox<Alternativa>> mapaCombos = respostasCorrespondencia.get(idQuestao);
                List<Alternativa> originais = item.getQuestao().getAlternativas();
                if (originais != null && !originais.isEmpty()) {
                    List<String> valoresNaOrdemOriginal = new ArrayList<>();
                    boolean algumPreenchido = false;
                    for (Alternativa alt : originais) {
                        ComboBox<Alternativa> combo = mapaCombos.get(alt.getId());
                        if (combo != null && combo.getValue() != null) {
                            valoresNaOrdemOriginal.add(String.valueOf(combo.getValue().getId()));
                            algumPreenchido = true;
                        } else {
                            valoresNaOrdemOriginal.add("");
                        }
                    }
                    ra.setRespostaTexto(String.join(",", valoresNaOrdemOriginal));
                    if (algumPreenchido) respondidas++;
                }
            }
            // Verifica as respostas com as alternativas na ordem original
            else if (respostasSequencia.containsKey(idQuestao)) {
                Map<Integer, ToggleGroup> gruposPorId = respostasSequencia.get(idQuestao);
                List<Alternativa> originais = item.getQuestao().getAlternativas();
                if (originais != null && !originais.isEmpty()) {
                    List<String> valoresNaOrdemOriginal = new ArrayList<>();
                    boolean algumaMarcada = false;
                    for (Alternativa alt : originais) {
                        ToggleGroup grupo = gruposPorId.get(alt.getId());
                        if (grupo != null && grupo.getSelectedToggle() != null) {
                            valoresNaOrdemOriginal.add(String.valueOf(grupo.getSelectedToggle().getUserData()));
                            algumaMarcada = true;
                        } else {
                            valoresNaOrdemOriginal.add("");
                        }
                    }
                    ra.setRespostaTexto(String.join(",", valoresNaOrdemOriginal));
                    if (algumaMarcada) respondidas++;
                }
            } else if (respostasMultiplaEscolha.containsKey(idQuestao)) {
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
            confirmacao.setHeaderText("Você possui " + emBranco + " questão(ões) sem nenhuma resposta!");
            confirmacao.setContentText("Questões inteiramente em branco recebem nota zero. Em verdadeiro ou falso em sequência, cada afirmação correta conta proporcionalmente para a nota.\n\nDeseja realmente entregar a prova agora?");

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
            confirmacao.setHeaderText("Todas as questões têm alguma resposta.");
            confirmacao.setContentText("Em verdadeiro ou falso em sequência, as afirmações corretas contam proporcionalmente para a nota. Após a entrega, suas respostas não poderão mais ser alteradas. Deseja finalizar?");
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
