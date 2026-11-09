package telas.questao;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import modelos.Alternativa;
import modelos.Questao;
import servicos.QuestaoServico;
import telas.bancoquestoes.BancoQuestoes;
import telas.componentes.CardQuestao;
import telas.componentes.Icones;

public class QuestaoController {
    @FXML private Label numeroLabel;
    @FXML private Label tituloTela;
    @FXML private ComboBox<String> tipoQuestaoComboBox;
    @FXML private StackPane formularioContainer;
    @FXML private TextArea enunciadoCaixaTexto;
    @FXML private TextField perguntaCaixaTexto;
    @FXML private Label proximaLetraLabel;
    @FXML private VBox listaAlternativas;
    @FXML private VBox listaQuestoes;
    @FXML private RadioButton verdadeiroRadioButton;
    @FXML private RadioButton falsoRadioButton;
    @FXML private TextArea afirmacaoCaixaTexto;
    @FXML private ToggleGroup respostaAfirmacaoGroup;
    @FXML private VBox listaAfirmacoes;
    @FXML private Label sequenciaLabel;
    @FXML private TextField conceitoCaixaTexto;
    @FXML private TextField definicaoCaixaTexto;
    @FXML private VBox listaPares;

    private final QuestaoServico questaoServico;
    private final List<Alternativa> alternativas = new ArrayList<>();
    private ToggleGroup grupoCorreta = new ToggleGroup();
    private long idQuestao;
    private boolean carregandoQuestao;

    private static final String TIPO_MULTIPLA_ESCOLHA = "Múltipla escolha";
    private static final String TIPO_VERDADEIRO_FALSO_SEQUENCIA = "Verdadeiro ou falso em sequência";
    private static final String TIPO_CORRESPONDENCIA = "Correspondência";
    private static final Map<String, String> FORMULARIOS_POR_TIPO = Map.of(
            TIPO_MULTIPLA_ESCOLHA, "multipla_escolha.fxml",
            TIPO_VERDADEIRO_FALSO_SEQUENCIA, "verdadeiro_falso_sequencia.fxml",
            TIPO_CORRESPONDENCIA, "correspondencia.fxml"
    );

    public QuestaoController(QuestaoServico questaoServico) { this.questaoServico = questaoServico; }

    @FXML
    private void initialize() {
        if (tipoQuestaoComboBox != null && tipoQuestaoComboBox.getItems().isEmpty()) {
            tipoQuestaoComboBox.getItems().setAll(
                    TIPO_MULTIPLA_ESCOLHA,
                    TIPO_VERDADEIRO_FALSO_SEQUENCIA,
                    TIPO_CORRESPONDENCIA
            );
        }
    }

    public void definirQuestao(Questao questao) {
        List<Questao> questoes = questaoServico.listarQuestoes();
        if (questao != null) idQuestao = questao.getNumero();
        renderizarListaQuestoes(questoes);
        if (questao == null) {
            numeroLabel.setText((questoes.size() + 1) + ")");
            tituloTela.setText("Nova questão");
            tipoQuestaoComboBox.setDisable(false);
            return;
        }
        numeroLabel.setText((questoes.indexOf(questao) + 1) + ")");
        tituloTela.setText("Questão " + idQuestao);
        alternativas.clear();
        if (questao.getAlternativas() != null) alternativas.addAll(questao.getAlternativas());

        // O tipo é definido na criação e permanece fixo durante a edição.
        String tipoSalvo = questao.getDescricao();
        String tipo = tipoSalvo != null && FORMULARIOS_POR_TIPO.containsKey(tipoSalvo)
                ? tipoSalvo
                : TIPO_MULTIPLA_ESCOLHA;
        carregandoQuestao = true;
        try {
            tipoQuestaoComboBox.setValue(tipo);
            tipoQuestaoComboBox.setDisable(true);
            carregarFormulario(tipo);
        } finally {
            carregandoQuestao = false;
        }

        if (enunciadoCaixaTexto != null) enunciadoCaixaTexto.setText(questao.getTitulo());
        renderizarAlternativasDoTipo(tipo);
    }

    // Lista lateral de questões já cadastradas; clicar abre a questão para edição
    private void renderizarListaQuestoes(List<Questao> questoes) {
        listaQuestoes.getChildren().clear();
        for (Questao questao : questoes) {
            Label numero = new Label(questao.getNumero() + ") Questão " + questao.getNumero());
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
        String tipoQuestao = tipoQuestaoComboBox == null ? null : tipoQuestaoComboBox.getValue();
        if (TIPO_MULTIPLA_ESCOLHA.equals(tipoQuestao)) {
            adicionarAlternativaMultiplaEscolha();
        } else if (TIPO_CORRESPONDENCIA.equals(tipoQuestao)) {
            adicionarParCorrespondencia();
        } else if (TIPO_VERDADEIRO_FALSO_SEQUENCIA.equals(tipoQuestao)) {
            adicionarAfirmacaoSequencia();
        }
    }

    private void adicionarAlternativaMultiplaEscolha() {
        if (perguntaCaixaTexto == null || listaAlternativas == null) return;
        if (alternativas.size() >= QuestaoServico.MAX_ALTERNATIVAS) {
            mostrar(Alert.AlertType.WARNING, "A questão pode ter no máximo " + QuestaoServico.MAX_ALTERNATIVAS + " alternativas (a até e).");
            return;
        }
        String texto = perguntaCaixaTexto.getText().trim();
        if (texto.isEmpty()) {
            perguntaCaixaTexto.requestFocus();
            return;
        }
        if (texto.length() > QuestaoServico.MAX_TEXTO_ALTERNATIVA) {
            mostrar(Alert.AlertType.WARNING, "Cada alternativa pode ter até " + QuestaoServico.MAX_TEXTO_ALTERNATIVA + " caracteres.");
            return;
        }
        alternativas.add(new Alternativa(alternativas.size() + 1, texto, null, false));
        perguntaCaixaTexto.clear();
        renderizarAlternativas();
        perguntaCaixaTexto.requestFocus();
    }

    private void adicionarParCorrespondencia() {
        if (conceitoCaixaTexto == null || definicaoCaixaTexto == null || listaPares == null) return;
        if (alternativas.size() >= QuestaoServico.MAX_ALTERNATIVAS) {
            mostrar(Alert.AlertType.WARNING, "A questão pode ter no máximo " + QuestaoServico.MAX_ALTERNATIVAS + " pares.");
            return;
        }

        String conceito = conceitoCaixaTexto.getText().trim();
        String definicao = definicaoCaixaTexto.getText().trim();
        if (conceito.isEmpty()) {
            conceitoCaixaTexto.requestFocus();
            return;
        }
        if (definicao.isEmpty()) {
            definicaoCaixaTexto.requestFocus();
            return;
        }
        if (conceito.length() > QuestaoServico.MAX_TEXTO_ALTERNATIVA
                || definicao.length() > QuestaoServico.MAX_TEXTO_ALTERNATIVA) {
            mostrar(Alert.AlertType.WARNING, "Cada conceito e definição pode ter até "
                    + QuestaoServico.MAX_TEXTO_ALTERNATIVA + " caracteres.");
            return;
        }

        alternativas.add(new Alternativa(alternativas.size() + 1, conceito, definicao, true));
        conceitoCaixaTexto.clear();
        definicaoCaixaTexto.clear();
        renderizarParesCorrespondencia();
        conceitoCaixaTexto.requestFocus();
    }

    private void adicionarAfirmacaoSequencia() {
        if (afirmacaoCaixaTexto == null || respostaAfirmacaoGroup == null || listaAfirmacoes == null) return;

        String texto = afirmacaoCaixaTexto.getText().trim();
        Toggle resposta = respostaAfirmacaoGroup.getSelectedToggle();
        if (texto.isEmpty()) {
            afirmacaoCaixaTexto.requestFocus();
            return;
        }
        if (texto.length() > QuestaoServico.MAX_TEXTO_ALTERNATIVA) {
            mostrar(Alert.AlertType.WARNING, "Cada afirmação pode ter até "
                    + QuestaoServico.MAX_TEXTO_ALTERNATIVA + " caracteres.");
            return;
        }
        if (resposta == null) {
            if (verdadeiroRadioButton != null) verdadeiroRadioButton.requestFocus();
            return;
        }

        boolean verdadeira = resposta == verdadeiroRadioButton;
        alternativas.add(new Alternativa(alternativas.size() + 1, texto, null, verdadeira));
        afirmacaoCaixaTexto.clear();
        respostaAfirmacaoGroup.selectToggle(null);
        renderizarAfirmacoes();
        afirmacaoCaixaTexto.requestFocus();
    }

    private void renderizarAlternativas() {
        if (listaAlternativas == null || proximaLetraLabel == null || perguntaCaixaTexto == null) return;
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
            // Texto editável da alternativa (PB24); vazio ou acima do limite é barrado ao confirmar
            TextField texto = new TextField(alternativa.getTexto());
            texto.getStyleClass().add("campo-alternativa");
            texto.setPromptText("o texto da alternativa não pode ficar vazio");
            texto.setMaxWidth(Double.MAX_VALUE);
            texto.textProperty().addListener((obs, antigo, novo) -> alternativa.setTexto(novo));
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
        boolean limiteAtingido = alternativas.size() >= QuestaoServico.MAX_ALTERNATIVAS;
        proximaLetraLabel.setText(limiteAtingido ? "" : CardQuestao.letra(alternativas.size()));
        perguntaCaixaTexto.setDisable(limiteAtingido);
        perguntaCaixaTexto.setPromptText(limiteAtingido
                ? "limite de " + QuestaoServico.MAX_ALTERNATIVAS + " alternativas atingido"
                : "digite a alternativa e clique em Adicionar alternativa");
    }

    @FXML
    private void confirmar() {
        if (enunciadoCaixaTexto == null || tipoQuestaoComboBox.getValue() == null) return;
        String tipo_questao = tipoQuestaoComboBox.getValue().trim();
        String enunciado = enunciadoCaixaTexto.getText().trim();
        if (enunciado.length() < QuestaoServico.MIN_ENUNCIADO) {
            mostrar(Alert.AlertType.WARNING, "A pergunta da questão deve ter pelo menos " + QuestaoServico.MIN_ENUNCIADO + " caracteres.");
            return;
        }
        if (enunciado.length() > QuestaoServico.MAX_ENUNCIADO) {
            mostrar(Alert.AlertType.WARNING, "O enunciado pode ter no máximo " + QuestaoServico.MAX_ENUNCIADO + " caracteres.");
            return;
        }
        if (alternativas.isEmpty()) {
            mostrar(Alert.AlertType.WARNING, "Adicione ao menos uma alternativa.");
            return;
        }
        if (alternativas.stream().anyMatch(a -> a.getTexto() == null || a.getTexto().isBlank())) {
            mostrar(Alert.AlertType.WARNING, "O texto das alternativas não pode ficar vazio.");
            return;
        }
        if (alternativas.stream().anyMatch(a -> a.getTexto().trim().length() > QuestaoServico.MAX_TEXTO_ALTERNATIVA)) {
            mostrar(Alert.AlertType.WARNING, "Cada alternativa pode ter até " + QuestaoServico.MAX_TEXTO_ALTERNATIVA + " caracteres.");
            return;
        }

        if (TIPO_CORRESPONDENCIA.equals(tipo_questao)
                && alternativas.stream().anyMatch(a -> a.getTextoCorrespondencia() == null
                || a.getTextoCorrespondencia().isBlank()
                || a.getTextoCorrespondencia().trim().length() > QuestaoServico.MAX_TEXTO_ALTERNATIVA)) {
            mostrar(Alert.AlertType.WARNING, "Preencha cada definição e use até "
                    + QuestaoServico.MAX_TEXTO_ALTERNATIVA + " caracteres.");
            return;
        }

        Toggle marcada = grupoCorreta.getSelectedToggle();
        if (TIPO_MULTIPLA_ESCOLHA.equals(tipo_questao) && marcada == null) {
            mostrar(Alert.AlertType.WARNING, "Marque a alternativa correta.");
            return;
        }

        Alternativa selecionada = marcada == null ? null : (Alternativa) marcada.getUserData();
        List<Alternativa> paraSalvar = new ArrayList<>();
        for (int i = 0; i < alternativas.size(); i++) {
            Alternativa atual = alternativas.get(i);
            paraSalvar.add(new Alternativa(
                    i + 1,
                    atual.getTexto().trim(),
                    atual.getTextoCorrespondencia() == null ? null : atual.getTextoCorrespondencia().trim(),
                    TIPO_MULTIPLA_ESCOLHA.equals(tipo_questao)
                            ? atual == selecionada
                            : atual.isCorreta()
            ));
        }
        Questao questao = new Questao(idQuestao, enunciado, tipo_questao, paraSalvar);
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
        try {
            if (salva && questaoServico.estaEmAlgumaProva(idQuestao)) {
                mostrar(Alert.AlertType.WARNING, "Esta questão está vinculada a uma ou mais provas e não pode ser excluída. "
                        + "Remova-a das provas em Questões e pesos antes de excluí-la.");
                return;
            }
        } catch (RuntimeException e) {
            mostrar(Alert.AlertType.ERROR, "Não foi possível verificar os vínculos da questão.\n" + e.getMessage());
            return;
        }
        String mensagem = salva
                ? "Excluir esta questão? Esta ação não pode ser desfeita."
                : "Descartar esta questão? O que foi digitado será perdido.";
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION, mensagem, ButtonType.CANCEL, ButtonType.OK);
        confirmacao.setHeaderText("Remover questão");
        if (confirmacao.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        try {
            if (salva) questaoServico.excluirQuestao(idQuestao);
            voltarParaBanco();
        } catch (IllegalArgumentException e) {
            mostrar(Alert.AlertType.WARNING, e.getMessage());
        } catch (RuntimeException e) {
            mostrar(Alert.AlertType.ERROR, "Não foi possível excluir a questão.\n" + e.getMessage());
        }
    }

    @FXML
    private void voltar(ActionEvent event) {
        voltarParaBanco();
    }

    @FXML
    private void selecionarTipoQuestao() {
        if (carregandoQuestao || tipoQuestaoComboBox == null || formularioContainer == null) return;

        String tipoSelecionado = tipoQuestaoComboBox.getValue();
        limparCamposFormulario();
        carregarFormulario(tipoSelecionado);
    }

    private void carregarFormulario(String tipoSelecionado) {
        if (formularioContainer == null) return;
        limparReferenciasFormulario();
        formularioContainer.getChildren().clear();
        if (tipoSelecionado == null) return;
        String arquivoFormulario = FORMULARIOS_POR_TIPO.get(tipoSelecionado);
        if (arquivoFormulario == null) return;

        var recurso = QuestaoController.class.getResource(
                "/telas/questao/tipos_questao/" + arquivoFormulario
        );
        if (recurso == null) {
            mostrar(Alert.AlertType.ERROR, "Não foi possível localizar o formulário selecionado.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(recurso);
            loader.setController(this);
            Parent formulario = loader.load();
            formularioContainer.getChildren().setAll(formulario);
        } catch (IOException | RuntimeException e) {
            mostrar(Alert.AlertType.ERROR, "Não foi possível carregar o formulário selecionado.\n" + e.getMessage());
        }
    }

    private void renderizarAlternativasDoTipo(String tipo) {
        if (TIPO_MULTIPLA_ESCOLHA.equals(tipo)) {
            renderizarAlternativas();
        } else if (TIPO_CORRESPONDENCIA.equals(tipo)) {
            renderizarParesCorrespondencia();
        } else if (TIPO_VERDADEIRO_FALSO_SEQUENCIA.equals(tipo)) {
            renderizarAfirmacoes();
        }
    }

    @FXML
    private void adicionarPar() {
        adicionarAlternativa();
    }

    private void renderizarParesCorrespondencia() {
        if (listaPares == null) return;
        listaPares.getChildren().clear();

        for (int indice = 0; indice < alternativas.size(); indice++) {
            Alternativa par = alternativas.get(indice);
            par.setId(indice + 1);

            Label numero = new Label((indice + 1) + ".");
            numero.getStyleClass().add("alternativa-letra");
            TextField conceito = new TextField(par.getTexto());
            conceito.getStyleClass().add("campo-alternativa");
            conceito.setPromptText("Conceito");
            conceito.textProperty().addListener((obs, antigo, novo) -> par.setTexto(novo));
            HBox.setHgrow(conceito, Priority.ALWAYS);

            Label separador = new Label("↔");
            separador.getStyleClass().add("alternativa-letra");

            TextField definicao = new TextField(par.getTextoCorrespondencia());
            definicao.getStyleClass().add("campo-alternativa");
            definicao.setPromptText("Definição correspondente");
            definicao.textProperty().addListener((obs, antigo, novo) -> par.setTextoCorrespondencia(novo));
            HBox.setHgrow(definicao, Priority.ALWAYS);

            Button remover = new Button("Remover");
            remover.getStyleClass().addAll("botao-claro", "perigo");
            remover.setOnAction(event -> {
                alternativas.remove(par);
                renderizarParesCorrespondencia();
            });

            HBox linha = new HBox(10, numero, conceito, separador, definicao, remover);
            linha.setAlignment(Pos.CENTER_LEFT);
            linha.getStyleClass().add("linha-par-correspondencia");
            listaPares.getChildren().add(linha);
        }
    }

    @FXML
    private void adicionarAfirmacao() {
        adicionarAlternativa();
    }

    private void renderizarAfirmacoes() {
        if (listaAfirmacoes == null) return;
        listaAfirmacoes.getChildren().clear();

        for (int indice = 0; indice < alternativas.size(); indice++) {
            Alternativa afirmacao = alternativas.get(indice);
            afirmacao.setId(indice + 1);
            Label numero = new Label((indice + 1) + ")");
            numero.getStyleClass().add("alternativa-letra");

            TextField texto = new TextField(afirmacao.getTexto());
            texto.getStyleClass().add("campo-alternativa");
            texto.setPromptText("Afirmação");
            texto.textProperty().addListener((obs, antigo, novo) -> afirmacao.setTexto(novo));
            HBox.setHgrow(texto, Priority.ALWAYS);

            ToggleGroup grupo = new ToggleGroup();
            RadioButton verdadeiro = new RadioButton("V");
            RadioButton falso = new RadioButton("F");
            verdadeiro.setAccessibleText("Verdadeiro");
            falso.setAccessibleText("Falso");
            verdadeiro.setToggleGroup(grupo);
            falso.setToggleGroup(grupo);
            verdadeiro.setSelected(afirmacao.isCorreta());
            falso.setSelected(!afirmacao.isCorreta());
            grupo.selectedToggleProperty().addListener((obs, anterior, selecionado) -> {
                if (selecionado != null) {
                    afirmacao.setCorreta(selecionado == verdadeiro);
                    atualizarSequencia();
                }
            });

            Button remover = new Button("Remover");
            remover.getStyleClass().addAll("botao-claro", "perigo");
            remover.setOnAction(event -> {
                alternativas.remove(afirmacao);
                renderizarAfirmacoes();
            });

            HBox linha = new HBox(10, numero, texto, verdadeiro, falso, remover);
            linha.setAlignment(Pos.CENTER_LEFT);
            linha.getStyleClass().add("linha-afirmacao");
            listaAfirmacoes.getChildren().add(linha);
        }

        atualizarSequencia();
    }

    private void atualizarSequencia() {
        if (sequenciaLabel == null) return;
        sequenciaLabel.setText(alternativas.isEmpty()
                ? "—"
                : alternativas.stream()
                        .map(alternativa -> alternativa.isCorreta() ? "V" : "F")
                        .collect(java.util.stream.Collectors.joining(", ")));
    }

    private void limparCamposFormulario() {
        alternativas.clear();
        limparReferenciasFormulario();
    }

    private void limparReferenciasFormulario() {
        enunciadoCaixaTexto = null;
        perguntaCaixaTexto = null;
        proximaLetraLabel = null;
        listaAlternativas = null;
        verdadeiroRadioButton = null;
        falsoRadioButton = null;
        afirmacaoCaixaTexto = null;
        respostaAfirmacaoGroup = null;
        listaAfirmacoes = null;
        sequenciaLabel = null;
        conceitoCaixaTexto = null;
        definicaoCaixaTexto = null;
        listaPares = null;
    }

    private void voltarParaBanco() {
        try { BancoQuestoes.abrir((Stage) listaQuestoes.getScene().getWindow()); }
        catch (Exception e) { mostrar(Alert.AlertType.ERROR, "Não foi possível voltar ao banco de questões."); }
    }

    private void mostrar(Alert.AlertType tipo, String mensagem) {
        Alert alerta = new Alert(tipo, mensagem);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }

}
