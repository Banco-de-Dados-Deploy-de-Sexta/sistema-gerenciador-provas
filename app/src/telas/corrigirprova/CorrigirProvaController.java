package telas.corrigirprova;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.RadioButton;
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
import modelos.ResultadoCorrecao;
import servicos.ProvaServico;
import telas.componentes.CardQuestao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class CorrigirProvaController {
    private static final String TIPO_CORRESPONDENCIA = "Correspondência";
    private static final String TIPO_VERDADEIRO_FALSO_SEQUENCIA = "Verdadeiro ou falso em sequência";
    private static final String TIPO_DISSERTATIVA = "Dissertativa";
    private static final String CLASSE_CORRETA = "feedback-correto";
    private static final String CLASSE_INCORRETA = "feedback-incorreto";

    @FXML private Label tituloTela;
    @FXML private Label resultadoLabel;
    @FXML private VBox listaQuestoes;

    private final Prova prova;
    private final ProvaServico provaServico;
    private final Map<Long, QuestaoComPeso> questoesPorId = new HashMap<>();
    private final Map<Long, ToggleGroup> respostasMultiplaEscolha = new HashMap<>();
    private final Map<Long, List<RadioButton>> opcoesMultiplaEscolha = new HashMap<>();
    private final Map<Long, Label> feedbackMultiplaEscolha = new HashMap<>();
    private final Map<Long, List<CorrespondenciaLinha>> respostasCorrespondencia = new HashMap<>();
    private final Map<Long, List<SequenciaLinha>> respostasSequencia = new HashMap<>();

    public CorrigirProvaController(Prova prova, ProvaServico provaServico) {
        this.prova = prova;
        this.provaServico = provaServico;
    }

    public void carregar() {
        tituloTela.setText("Corrigir — " + prova.getTitulo());
        listaQuestoes.getChildren().clear();
        limparMapeamentos();
        resultadoLabel.setText("");

        List<QuestaoComPeso> questoes = provaServico.listarQuestoes(prova.getCodigo());
        for (QuestaoComPeso item : questoes) {
            long idQuestao = item.getQuestao().getNumero();
            questoesPorId.put(idQuestao, item);
            VBox bloco = new VBox(9);
            bloco.getStyleClass().add("question-row");

            Label pergunta = new Label(item.getQuestao().getTitulo()
                    + "  (peso " + String.format(Locale.forLanguageTag("pt-BR"), "%.1f", item.getPeso()) + ")");
            pergunta.getStyleClass().add("step-title");
            pergunta.setWrapText(true);
            Label tipo = new Label(CardQuestao.rotuloTipo(item.getQuestao().getDescricao()));
            tipo.setStyle("-fx-background-color: #eff6ff; -fx-background-radius: 999; "
                    + "-fx-padding: 4 9; -fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #1d4ed8;");
            bloco.getChildren().addAll(pergunta, tipo);

            List<Alternativa> alternativas = item.getQuestao().getAlternativas();
            String tipoQuestao = item.getQuestao().getDescricao();
            if (TIPO_CORRESPONDENCIA.equals(tipoQuestao)) {
                renderizarCorrespondencia(bloco, idQuestao, alternativas);
            } else if (TIPO_VERDADEIRO_FALSO_SEQUENCIA.equals(tipoQuestao)) {
                renderizarSequencia(bloco, idQuestao, alternativas);
            } else if (TIPO_DISSERTATIVA.equals(tipoQuestao)) {
                Label informacao = new Label("A nota é atribuída à resposta enviada pelo aluno. Use “Corrigir dissertativas” no menu da prova.");
                informacao.setWrapText(true);
                bloco.getChildren().add(informacao);
            } else {
                renderizarMultiplaEscolha(bloco, idQuestao, alternativas);
            }
            listaQuestoes.getChildren().add(bloco);
        }

        if (listaQuestoes.getChildren().isEmpty()) {
            Label vazio = new Label("Esta prova ainda não tem questões. Vincule questões e pesos primeiro.");
            vazio.getStyleClass().add("subtitulo");
            listaQuestoes.getChildren().add(vazio);
        }
    }

    private void renderizarMultiplaEscolha(VBox bloco, long idQuestao, List<Alternativa> alternativas) {
        ToggleGroup grupo = novoGrupoComAtualizacao();
        List<RadioButton> opcoes = new ArrayList<>();
        for (int i = 0; i < alternativas.size(); i++) {
            Alternativa alternativa = alternativas.get(i);
            RadioButton opcao = new RadioButton(CardQuestao.letra(i) + " " + alternativa.getTexto());
            opcao.setWrapText(true);
            opcao.setToggleGroup(grupo);
            opcao.setUserData(alternativa.getId());
            opcoes.add(opcao);
            bloco.getChildren().add(opcao);
        }
        respostasMultiplaEscolha.put(idQuestao, grupo);
        opcoesMultiplaEscolha.put(idQuestao, opcoes);

        Label feedback = novaLabelFeedback();
        bloco.getChildren().add(feedback);
        feedbackMultiplaEscolha.put(idQuestao, feedback);
        if (alternativas.isEmpty()) adicionarSemOpcoes(bloco);
    }

    private void renderizarCorrespondencia(VBox bloco, long idQuestao, List<Alternativa> pares) {
        Label instrucao = new Label("Associe cada conceito à definição que o aluno respondeu. Cada definição pode ser usada uma única vez, e cada associação correta vale uma parte proporcional do peso.");
        instrucao.setWrapText(true);
        bloco.getChildren().add(instrucao);

        List<Alternativa> definicoes = new ArrayList<>(pares);
        Collections.shuffle(definicoes);
        if (definicoes.size() > 1 && mesmaOrdem(definicoes, pares)) Collections.rotate(definicoes, 1);

        List<CorrespondenciaLinha> linhas = new ArrayList<>();
        boolean[] rejeitandoSelecao = {false};
        for (int i = 0; i < pares.size(); i++) {
            Alternativa par = pares.get(i);
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
            definicao.setPromptText("Selecione a definição respondida");
            definicao.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(definicao, Priority.ALWAYS);

            HBox linhaVisual = new HBox(10, numero, conceito, seta, definicao);
            linhaVisual.getStyleClass().add("answer-row");
            linhaVisual.setAlignment(Pos.CENTER_LEFT);
            Label feedback = novaLabelFeedback();
            VBox linhaCompleta = new VBox(4, linhaVisual, feedback);
            linhas.add(new CorrespondenciaLinha(par, definicao, linhaVisual, feedback));
            definicao.valueProperty().addListener((obs, antiga, nova) -> {
                if (rejeitandoSelecao[0]) return;
                boolean duplicada = nova != null && linhas.stream()
                        .anyMatch(outra -> outra.selecao != definicao
                                && outra.selecao.getValue() != null
                                && outra.selecao.getValue().getId() == nova.getId());
                if (duplicada) {
                    rejeitandoSelecao[0] = true;
                    definicao.setValue(antiga);
                    rejeitandoSelecao[0] = false;
                    resultadoLabel.setText("Cada definição só pode ser associada a um conceito.");
                    return;
                }
                invalidarResultado();
            });
            bloco.getChildren().add(linhaCompleta);
        }
        respostasCorrespondencia.put(idQuestao, linhas);
        if (pares.isEmpty()) adicionarSemOpcoes(bloco);
    }

    private void renderizarSequencia(VBox bloco, long idQuestao, List<Alternativa> afirmacoes) {
        Label instrucao = new Label("Indique a resposta do aluno para cada afirmação. Cada acerto vale uma parte proporcional do peso da questão.");
        instrucao.setWrapText(true);
        bloco.getChildren().add(instrucao);

        List<SequenciaLinha> linhas = new ArrayList<>();
        for (int i = 0; i < afirmacoes.size(); i++) {
            Alternativa afirmacao = afirmacoes.get(i);
            Label numero = new Label((i + 1) + ".");
            numero.setStyle("-fx-font-weight: bold; -fx-text-fill: #2563eb;");
            Label texto = new Label(afirmacao.getTexto());
            texto.setWrapText(true);
            texto.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(texto, Priority.ALWAYS);

            ToggleGroup grupo = novoGrupoComAtualizacao();
            RadioButton verdadeiro = new RadioButton("Verdadeiro");
            verdadeiro.setUserData("V");
            verdadeiro.setToggleGroup(grupo);
            RadioButton falso = new RadioButton("Falso");
            falso.setUserData("F");
            falso.setToggleGroup(grupo);

            HBox linhaVisual = new HBox(12, numero, texto, verdadeiro, falso);
            linhaVisual.getStyleClass().add("answer-row");
            linhaVisual.setAlignment(Pos.CENTER_LEFT);
            Label feedback = novaLabelFeedback();
            VBox linhaCompleta = new VBox(4, linhaVisual, feedback);
            linhas.add(new SequenciaLinha(afirmacao, grupo, verdadeiro, falso, linhaVisual, feedback));
            bloco.getChildren().add(linhaCompleta);
        }
        respostasSequencia.put(idQuestao, linhas);
        if (afirmacoes.isEmpty()) adicionarSemOpcoes(bloco);
    }

    private ToggleGroup novoGrupoComAtualizacao() {
        ToggleGroup grupo = new ToggleGroup();
        grupo.selectedToggleProperty().addListener((obs, antiga, nova) -> invalidarResultado());
        return grupo;
    }

    private Label novaLabelFeedback() {
        Label feedback = new Label("");
        feedback.getStyleClass().add("feedback-texto");
        feedback.setWrapText(true);
        return feedback;
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

    private boolean mesmaOrdem(List<Alternativa> primeira, List<Alternativa> segunda) {
        for (int i = 0; i < primeira.size(); i++) {
            if (primeira.get(i) != segunda.get(i)) return false;
        }
        return true;
    }

    private void adicionarSemOpcoes(VBox bloco) {
        Label semOpcoes = new Label("Esta questão não tem alternativas cadastradas.");
        semOpcoes.getStyleClass().add("subtitulo");
        bloco.getChildren().add(semOpcoes);
    }

    @FXML
    private void limparRespostas() {
        respostasMultiplaEscolha.values().forEach(grupo -> grupo.selectToggle(null));
        respostasCorrespondencia.values().forEach(linhas -> linhas.forEach(linha -> linha.selecao.setValue(null)));
        respostasSequencia.values().forEach(linhas -> linhas.forEach(linha -> linha.grupo.selectToggle(null)));
        invalidarResultado();
    }

    @FXML
    private void corrigir() {
        if (questoesPorId.isEmpty()) {
            mostrar(Alert.AlertType.WARNING, "A prova ainda não possui questões para corrigir.");
            return;
        }

        Map<Long, RespostaAluno> respostasInformadas = new HashMap<>();
        respostasMultiplaEscolha.forEach((idQuestao, grupo) -> {
            Toggle marcada = grupo.getSelectedToggle();
            if (marcada != null && marcada.getUserData() instanceof Integer idAlternativa) {
                RespostaAluno resposta = new RespostaAluno();
                resposta.setIdAlternativaEscolhida(idAlternativa);
                respostasInformadas.put(idQuestao, resposta);
            }
        });
        respostasCorrespondencia.forEach((idQuestao, linhas) -> {
            if (!linhas.isEmpty() && linhas.stream().anyMatch(linha -> linha.selecao.getValue() != null)) {
                List<String> idsEscolhidos = linhas.stream()
                        .map(linha -> linha.selecao.getValue() == null
                                ? ""
                                : String.valueOf(linha.selecao.getValue().getId()))
                        .toList();
                List<Integer> idsSelecionados = linhas.stream()
                        .map(linha -> linha.selecao.getValue() == null ? null : linha.selecao.getValue().getId())
                        .filter(java.util.Objects::nonNull)
                        .toList();
                if (new HashSet<>(idsSelecionados).size() != idsSelecionados.size()) {
                    mostrar(Alert.AlertType.WARNING, "Cada definição só pode ser associada a um conceito.");
                    return;
                }
                RespostaAluno resposta = new RespostaAluno();
                resposta.setRespostaTexto(String.join(",", idsEscolhidos));
                respostasInformadas.put(idQuestao, resposta);
            }
        });
        respostasSequencia.forEach((idQuestao, linhas) -> {
            if (!linhas.isEmpty() && linhas.stream().anyMatch(linha -> linha.grupo.getSelectedToggle() != null)) {
                RespostaAluno resposta = new RespostaAluno();
                resposta.setRespostaTexto(linhas.stream()
                        .map(linha -> linha.grupo.getSelectedToggle() == null
                                ? ""
                                : String.valueOf(linha.grupo.getSelectedToggle().getUserData()))
                        .collect(Collectors.joining(",")));
                respostasInformadas.put(idQuestao, resposta);
            }
        });

        ResultadoCorrecao resultado = provaServico.corrigirRespostas(prova.getCodigo(), respostasInformadas);
        aplicarFeedback(resultado);
        resultadoLabel.setText(resultado.getPontosPossiveis() == 0
                ? "Esta prova não tem questões objetivas para calcular."
                : String.format(Locale.forLanguageTag("pt-BR"),
                        "Resultado objetivo: %.2f de %.2f pontos",
                        resultado.getPontosObtidos(), resultado.getPontosPossiveis()));
    }

    private void aplicarFeedback(ResultadoCorrecao resultado) {
        questoesPorId.forEach((idQuestao, item) -> {
            boolean questaoCorreta = Boolean.TRUE.equals(resultado.getAcertosPorQuestao().get(idQuestao));
            String tipo = item.getQuestao().getDescricao();
            if (TIPO_CORRESPONDENCIA.equals(tipo)) {
                aplicarFeedbackCorrespondencia(idQuestao);
            } else if (TIPO_VERDADEIRO_FALSO_SEQUENCIA.equals(tipo)) {
                aplicarFeedbackSequencia(idQuestao);
            } else if (TIPO_DISSERTATIVA.equals(tipo)) {
                return;
            } else {
                aplicarFeedbackMultiplaEscolha(idQuestao, item, questaoCorreta);
            }
        });
    }

    private void aplicarFeedbackMultiplaEscolha(long idQuestao, QuestaoComPeso item, boolean correta) {
        List<Alternativa> alternativas = item.getQuestao().getAlternativas();
        List<RadioButton> opcoes = opcoesMultiplaEscolha.getOrDefault(idQuestao, List.of());
        List<String> gabarito = new ArrayList<>();
        for (int i = 0; i < alternativas.size(); i++) {
            Alternativa alternativa = alternativas.get(i);
            RadioButton opcao = opcoes.get(i);
            if (alternativa.isCorreta()) {
                adicionarClasse(opcao, CLASSE_CORRETA);
                gabarito.add(CardQuestao.letra(i) + " " + alternativa.getTexto());
            } else if (opcao.isSelected()) {
                adicionarClasse(opcao, CLASSE_INCORRETA);
            }
        }
        Label feedback = feedbackMultiplaEscolha.get(idQuestao);
        if (correta) {
            definirFeedback(feedback, "✓ Acertou.", true);
        } else {
            String respostaCorreta = gabarito.isEmpty() ? "não cadastrada" : String.join("; ", gabarito);
            definirFeedback(feedback, "✕ Errou. Resposta correta: " + respostaCorreta, false);
        }
    }

    private void aplicarFeedbackCorrespondencia(long idQuestao) {
        for (CorrespondenciaLinha linha : respostasCorrespondencia.getOrDefault(idQuestao, List.of())) {
            Alternativa selecionada = linha.selecao.getValue();
            boolean correta = selecionada != null && selecionada.getId() == linha.conceito.getId();
            adicionarClasse(linha.visual, correta ? CLASSE_CORRETA : CLASSE_INCORRETA);
            adicionarClasse(linha.selecao, correta ? CLASSE_CORRETA : CLASSE_INCORRETA);
            if (correta) {
                definirFeedback(linha.feedback, "✓ Associação correta.", true);
            } else {
                String respostaCorreta = linha.conceito.getTextoCorrespondencia();
                definirFeedback(linha.feedback, "✕ Associação incorreta. Resposta correta: "
                        + (respostaCorreta == null || respostaCorreta.isBlank() ? "não cadastrada" : respostaCorreta), false);
            }
        }
    }

    private void aplicarFeedbackSequencia(long idQuestao) {
        for (SequenciaLinha linha : respostasSequencia.getOrDefault(idQuestao, List.of())) {
            Toggle marcada = linha.grupo.getSelectedToggle();
            String respostaAluno = marcada == null ? null : String.valueOf(marcada.getUserData());
            String respostaCorreta = linha.afirmacao.isCorreta() ? "V" : "F";
            boolean correta = respostaCorreta.equals(respostaAluno);
            adicionarClasse(linha.visual, correta ? CLASSE_CORRETA : CLASSE_INCORRETA);
            RadioButton opcaoCorreta = "V".equals(respostaCorreta) ? linha.verdadeiro : linha.falso;
            adicionarClasse(opcaoCorreta, CLASSE_CORRETA);
            if (!correta && marcada instanceof RadioButton opcaoMarcada) {
                adicionarClasse(opcaoMarcada, CLASSE_INCORRETA);
            }
            if (correta) {
                definirFeedback(linha.feedback, "✓ Acertou: " + rotuloVerdadeiroFalso(respostaCorreta) + ".", true);
            } else {
                definirFeedback(linha.feedback, "✕ Errou. Resposta correta: "
                        + rotuloVerdadeiroFalso(respostaCorreta) + ".", false);
            }
        }
    }

    private String rotuloVerdadeiroFalso(String resposta) {
        return "V".equals(resposta) ? "Verdadeiro" : "Falso";
    }

    private void definirFeedback(Label label, String texto, boolean correta) {
        if (label == null) return;
        label.setText(texto);
        removerClassesStatus(label);
        adicionarClasse(label, correta ? CLASSE_CORRETA : CLASSE_INCORRETA);
    }

    private void adicionarClasse(javafx.scene.Node node, String classe) {
        if (!node.getStyleClass().contains(classe)) node.getStyleClass().add(classe);
    }

    private void removerClassesStatus(javafx.scene.Node node) {
        node.getStyleClass().removeAll(CLASSE_CORRETA, CLASSE_INCORRETA);
    }

    private void invalidarResultado() {
        removerFeedbackVisual();
        resultadoLabel.setText("");
    }

    private void removerFeedbackVisual() {
        opcoesMultiplaEscolha.values().forEach(opcoes -> opcoes.forEach(this::removerClassesStatus));
        feedbackMultiplaEscolha.values().forEach(label -> {
            label.setText("");
            removerClassesStatus(label);
        });
        respostasCorrespondencia.values().forEach(linhas -> linhas.forEach(linha -> {
            removerClassesStatus(linha.visual);
            linha.feedback.setText("");
            removerClassesStatus(linha.feedback);
        }));
        respostasSequencia.values().forEach(linhas -> linhas.forEach(linha -> {
            removerClassesStatus(linha.visual);
            linha.feedback.setText("");
            removerClassesStatus(linha.feedback);
        }));
    }

    private void limparMapeamentos() {
        questoesPorId.clear();
        respostasMultiplaEscolha.clear();
        opcoesMultiplaEscolha.clear();
        feedbackMultiplaEscolha.clear();
        respostasCorrespondencia.clear();
        respostasSequencia.clear();
    }

    @FXML
    private void voltar(ActionEvent event) {
        try { telas.prova.Prova.abrir((Stage) tituloTela.getScene().getWindow()); }
        catch (Exception e) { mostrar(Alert.AlertType.ERROR, "Não foi possível voltar à lista de provas."); }
    }

    private void mostrar(Alert.AlertType tipo, String mensagem) {
        Alert alerta = new Alert(tipo, mensagem);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }

    private static class CorrespondenciaLinha {
        private final Alternativa conceito;
        private final ComboBox<Alternativa> selecao;
        private final HBox visual;
        private final Label feedback;

        private CorrespondenciaLinha(Alternativa conceito, ComboBox<Alternativa> selecao, HBox visual, Label feedback) {
            this.conceito = conceito;
            this.selecao = selecao;
            this.visual = visual;
            this.feedback = feedback;
        }
    }

    private static class SequenciaLinha {
        private final Alternativa afirmacao;
        private final ToggleGroup grupo;
        private final RadioButton verdadeiro;
        private final RadioButton falso;
        private final HBox visual;
        private final Label feedback;

        private SequenciaLinha(Alternativa afirmacao, ToggleGroup grupo, RadioButton verdadeiro,
                RadioButton falso, HBox visual, Label feedback) {
            this.afirmacao = afirmacao;
            this.grupo = grupo;
            this.verdadeiro = verdadeiro;
            this.falso = falso;
            this.visual = visual;
            this.feedback = feedback;
        }
    }
}
