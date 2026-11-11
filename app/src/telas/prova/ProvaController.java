package telas.prova;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import auxiliares.Sessao;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import modelos.Alternativa;
import modelos.Prova;
import modelos.QuestaoComPeso;
import modelos.Usuario;
import servicos.ProvaServico;
import telas.alunoprova.AlunoProva;
import telas.bancoquestoes.BancoQuestoes;
import telas.componentes.CardQuestao;
import telas.composicaoprova.ComposicaoProva;
import telas.corrigirprova.CorrigirProva;

public class ProvaController {
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    private VBox listaProvas;
    @FXML
    private VBox questoesProva;
    @FXML
    private Button editarButton;
    @FXML
    private Button excluirButton;
    @FXML
    private Button agendarButton;
    @FXML
    private Button baixarButton;
    @FXML
    private Button questoesButton;
    @FXML
    private Button corrigirButton;
    @FXML
    private Button aplicarButton;
    @FXML
    private Label nomeUsuario;

    private final ProvaServico provaServico;
    private final ObjectProperty<Prova> selecionada = new SimpleObjectProperty<>();
    private final BooleanProperty aplicada = new SimpleBooleanProperty();
    // registro do professor -> nome, preenchido sob demanda ao montar os cards
    private final HashMap<String, String> nomesProfessores = new HashMap<>();

    public ProvaController(ProvaServico provaServico) {
        this.provaServico = provaServico;
    }

    @FXML
    private void initialize() {
        Usuario logado = Sessao.getUsuarioLogado();
        nomeUsuario.setText(logado == null ? "" : logado.getNome());
        Label registroUsuario = (Label) ((VBox) nomeUsuario.getParent()).getChildren().get(2);
        registroUsuario.setText(Sessao.getRegistroUsuarioLogado() == null ? "" : Sessao.getRegistroUsuarioLogado());
        var nenhuma = selecionada.isNull();
        var bloqueada = nenhuma.or(aplicada);
        for (Button botao : List.of(agendarButton, baixarButton, corrigirButton, aplicarButton))
            botao.disableProperty().bind(nenhuma);
        for (Button botao : List.of(editarButton, excluirButton, questoesButton))
            botao.disableProperty().bind(bloqueada);
        selecionada.addListener((obs, antiga, nova) -> {
            aplicada.set(nova != null && provaServico.aplicada(nova.getCodigo()));
            destacarSelecionada();
            mostrarQuestoes();
        });

        // bloquear botoes caso a prova nao pertenca ao usuario logado
        BooleanProperty ehDona = new SimpleBooleanProperty();
        selecionada.addListener((obs, antiga, nova) -> {
            ehDona.set(nova != null && Sessao.getRegistroUsuarioLogado().equals(nova.getRegistroProfessor()));
        });
        editarButton.disableProperty().bind(ehDona.not());
        excluirButton.disableProperty().bind(ehDona.not());
        agendarButton.disableProperty().bind(ehDona.not());
        questoesButton.disableProperty().bind(ehDona.not());
        corrigirButton.disableProperty().bind(ehDona.not());
        aplicarButton.disableProperty().bind(ehDona.not());
        
        atualizar();
    }

    private void atualizar() {
        String codigoSelecionado = selecionada.get() == null ? null : selecionada.get().getCodigo();
        List<Prova> provas = provaServico.listarProvas();
        listaProvas.getChildren().clear();
        for (Prova prova : provas)
            listaProvas.getChildren().add(cardProva(prova));
        if (provas.isEmpty())
            listaProvas.getChildren().add(vazio("Nenhuma prova cadastrada. Use Criar prova."));
        selecionada.set(provas.stream().filter(p -> p.getCodigo().equals(codigoSelecionado)).findFirst().orElse(null));
        destacarSelecionada();
        mostrarQuestoes();
    }

    private String nomeProfessor(Prova prova) {
        String registro = prova.getRegistroProfessor();
        if (registro == null || registro.isBlank())
            return "Sem professor";
        return nomesProfessores.computeIfAbsent(registro, r -> {
            Usuario professor = telas.prova.Prova.getUsuarioStorage().buscarPorRegistro(r);
            return professor == null ? r : professor.getNome();
        });
    }

    // Card de prova do Figma: código e data no topo, título,
    // matéria/curso/bimestre, total de questões e estado
    private VBox cardProva(Prova prova) {
        Label professor = new Label(nomeProfessor(prova));
        professor.getStyleClass().add("card-pequeno");
        Label codigo = new Label(prova.getCodigo());
        codigo.getStyleClass().add("card-pequeno");
        String textoData = "Sem data";
        if (prova.getDataHora() != null) {
            textoData = prova.getDataHora().format(FORMATO_DATA_HORA);
        } else if (prova.getData() != null) {
            textoData = prova.getData().format(FORMATO_DATA);
        }
        Label data = new Label(textoData);
        data.getStyleClass().add("card-pequeno");
        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);
        VBox direita = new VBox(2, data, professor);
        direita.setAlignment(Pos.TOP_RIGHT);
        HBox topo = new HBox(codigo, espaco, direita);


        Label titulo = new Label(prova.getTitulo());
        titulo.getStyleClass().add("card-titulo");
        Label detalhes = new Label(descricao(prova));
        detalhes.getStyleClass().add("card-pequeno");
        detalhes.setWrapText(true);
        int quantidade = provaServico.contarQuestoes(prova.getCodigo());
        Label questoes = new Label(quantidade + (quantidade == 1 ? " questão" : " questões"));
        questoes.getStyleClass().add("card-destaque");
        Region espacoRodape = new Region();
        HBox.setHgrow(espacoRodape, Priority.ALWAYS);
        Label estado = new Label(provaServico.estado(prova));
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
            if (marcada)
                card.getStyleClass().add("selecionado");
        }
    }

    private void mostrarQuestoes() {
        questoesProva.getChildren().clear();
        Prova prova = selecionada.get();
        if (prova == null) {
            questoesProva.getChildren().add(vazio("Selecione uma prova para ver as questões."));
            return;
        }
        List<QuestaoComPeso> questoes = provaServico.listarQuestoes(prova.getCodigo());
        for (int i = 0; i < questoes.size(); i++)
            questoesProva.getChildren()
                    .add(CardQuestao.visualizar(i + 1, questoes.get(i).getQuestao(), questoes.get(i).getPeso()));
        if (questoes.isEmpty())
            questoesProva.getChildren()
                    .add(vazio("Esta prova ainda não tem questões. Use Questões e pesos para vinculá-las."));
    }

    private Label vazio(String mensagem) {
        Label vazio = new Label(mensagem);
        vazio.getStyleClass().add("vazio");
        vazio.setWrapText(true);
        return vazio;
    }

    @FXML
    private void novaProva(ActionEvent event) {
        abrirFormulario(event, null);
    }

    @FXML
    private void editarProva(ActionEvent event) {
        Prova prova = selecionada.get();
        if (ehDona(prova) == false) {
            Alert aviso = new Alert(Alert.AlertType.WARNING, "Você não tem permissão para editar esta prova.");
            aviso.setHeaderText(null);
            aviso.showAndWait();
            return;
        }
        abrirFormulario(event, prova);
    }

    private void abrirFormulario(ActionEvent event, Prova prova) {
        try {
            telas.prova.Prova.abrirFormulario(palcoDe(event), prova);
        } catch (Exception e) {
            erro("Não foi possível abrir o formulário da prova.", e);
        }
    }

    @FXML
    private void excluirProva() {
        Prova prova = selecionada.get();
        if (ehDona(prova) == false) {
            Alert aviso = new Alert(Alert.AlertType.WARNING, "Você não tem permissão para editar esta prova.");
            aviso.setHeaderText(null);
            aviso.showAndWait();
            return;
        }
        if (prova == null)
            return;
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                "Excluir a prova e seus vínculos com questões?", ButtonType.CANCEL, ButtonType.OK);
        confirmacao.setHeaderText("Excluir prova");
        if (confirmacao.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK)
            return;
        try {
            provaServico.excluirProva(prova.getCodigo());
            selecionada.set(null);
            atualizar();
        } catch (RuntimeException e) {
            erro("Não foi possível excluir a prova.", e);
        }
    }

    @FXML
    private void agendarProva() {
        Prova prova = selecionada.get();
        if (ehDona(prova) == false) {
            Alert aviso = new Alert(Alert.AlertType.WARNING, "Você não tem permissão para editar esta prova.");
            aviso.setHeaderText(null);
            aviso.showAndWait();
            return;
        }
        if (prova == null)
            return;

        LocalDate hoje = auxiliares.Relogio.hoje();
        int semestreAtual = hoje.getMonthValue() <= 6 ? 1 : 2;
        int anoAtual = hoje.getYear();

        LocalDate dataInicial = prova.getDataHora() != null ? prova.getDataHora().toLocalDate()
                : (prova.getData() != null ? prova.getData() : hoje);
        DatePicker campoData = new DatePicker(dataInicial);
        campoData.setMaxWidth(Double.MAX_VALUE);
        campoData.setPromptText("Selecione a data");
        campoData.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    return;
                }
                boolean ehPassado = item.isBefore(hoje);
                boolean ehFimDeSemana = item.getDayOfWeek() == java.time.DayOfWeek.SATURDAY
                        || item.getDayOfWeek() == java.time.DayOfWeek.SUNDAY;
                int semItem = item.getMonthValue() <= 6 ? 1 : 2;
                boolean outroSemestre = item.getYear() != anoAtual || semItem != semestreAtual;

                if (ehPassado || ehFimDeSemana || outroSemestre) {
                    setDisable(true);
                    setStyle("-fx-opacity: 0.4;");
                }
            }
        });

        List<String> todosHorarios = List.of(
            // Manhã
            "07:10", "07:55", "08:40", "09:40", "10:25", "11:10",
            // Tarde
            "13:10", "13:55", "14:40", "15:40", "16:25", "17:10",
            // Noite
            "18:45", "19:30", "19:35", "20:15", "20:25", "21:10", "21:15", "21:25", "21:55", "22:15", "23:05"
        );

        String horaInicioInicial = prova.getDataHora() != null ? prova.getDataHora().format(FORMATO_HORA) : "07:10";
        ComboBox<String> campoHoraInicio = new ComboBox<>(FXCollections.observableArrayList(todosHorarios));
        campoHoraInicio.setEditable(true);
        campoHoraInicio.setValue(horaInicioInicial);
        campoHoraInicio.setMaxWidth(Double.MAX_VALUE);
        campoHoraInicio.setPromptText("Selecione ou digite (HH:mm)");

        String horaFimInicial = prova.getDataHoraFim() != null ? prova.getDataHoraFim().format(FORMATO_HORA) : "";
        ComboBox<String> campoHoraFim = new ComboBox<>(FXCollections.observableArrayList(todosHorarios));
        campoHoraFim.setEditable(true);
        if (!horaFimInicial.isEmpty()) {
            campoHoraFim.setValue(horaFimInicial);
        }
        campoHoraFim.setMaxWidth(Double.MAX_VALUE);
        campoHoraFim.setPromptText("Opcional (HH:mm)");

        // Atualiza dinamicamente as opções de horários de início e término
        Runnable atualizarOpcoesHorarios = () -> {
            LocalDate dataSel = campoData.getValue();
            boolean dataEhHoje = dataSel != null && dataSel.equals(auxiliares.Relogio.hoje());
            java.time.LocalTime agora = auxiliares.Relogio.horaAtual();

            List<String> horariosInicioValidos = todosHorarios.stream().filter(h -> {
                if (!dataEhHoje) return true;
                try {
                    return java.time.LocalTime.parse(h, DateTimeFormatter.ofPattern("HH:mm")).isAfter(agora);
                } catch (Exception e) {
                    return true;
                }
            }).toList();

            String valorInicioAtual = campoHoraInicio.getEditor().getText() != null
                    ? campoHoraInicio.getEditor().getText().trim()
                    : campoHoraInicio.getValue();

            campoHoraInicio.setItems(FXCollections.observableArrayList(horariosInicioValidos));
            if (valorInicioAtual != null && !valorInicioAtual.isEmpty()) {
                campoHoraInicio.getEditor().setText(valorInicioAtual);
                campoHoraInicio.setValue(valorInicioAtual);
            }

            java.time.LocalTime inicioParseado = null;
            if (valorInicioAtual != null && valorInicioAtual.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {
                String normalizado = valorInicioAtual.length() == 4 ? "0" + valorInicioAtual : valorInicioAtual;
                inicioParseado = java.time.LocalTime.parse(normalizado, DateTimeFormatter.ofPattern("HH:mm"));
            }

            final java.time.LocalTime refInicio = inicioParseado;
            List<String> horariosFimValidos = todosHorarios.stream().filter(h -> {
                try {
                    java.time.LocalTime ht = java.time.LocalTime.parse(h, DateTimeFormatter.ofPattern("HH:mm"));
                    if (refInicio != null && !ht.isAfter(refInicio)) {
                        return false;
                    }
                    if (dataEhHoje && !ht.isAfter(agora)) {
                        return false;
                    }
                    return true;
                } catch (Exception e) {
                    return true;
                }
            }).toList();

            String valorFimAtual = campoHoraFim.getEditor().getText() != null
                    ? campoHoraFim.getEditor().getText().trim()
                    : campoHoraFim.getValue();

            campoHoraFim.setItems(FXCollections.observableArrayList(horariosFimValidos));
            if (valorFimAtual != null && !valorFimAtual.isEmpty()) {
                campoHoraFim.getEditor().setText(valorFimAtual);
                campoHoraFim.setValue(valorFimAtual);
            }
        };

        campoData.valueProperty().addListener((obs, antigo, novo) -> atualizarOpcoesHorarios.run());
        campoHoraInicio.valueProperty().addListener((obs, antigo, novo) -> atualizarOpcoesHorarios.run());
        campoHoraInicio.getEditor().textProperty().addListener((obs, antigo, novo) -> atualizarOpcoesHorarios.run());

        atualizarOpcoesHorarios.run();

        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Agendar prova");

        DialogPane dialogPane = dialogo.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/telas/componentes/prova.css").toExternalForm());
        dialogPane.getStyleClass().add("card");
        dialogPane.setStyle("-fx-background-color: #f5f7fb; -fx-padding: 10;");

        Label labelCabecalho = new Label("Agendar: " + prova.getTitulo());
        labelCabecalho.getStyleClass().add("titulo");
        labelCabecalho.setStyle("-fx-font-size: 18px;");

        Label labelSubtitulo = new Label("Semestre letivo: " + semestreAtual + "º semestre de " + anoAtual + " (dias úteis)");
        labelSubtitulo.getStyleClass().add("subtitulo");

        Label lblData = new Label("DATA DA PROVA");
        lblData.getStyleClass().add("secao");
        VBox boxData = new VBox(5, lblData, campoData);

        Label lblHoraInicio = new Label("HORÁRIO DE INÍCIO");
        lblHoraInicio.getStyleClass().add("secao");
        VBox boxHoraInicio = new VBox(5, lblHoraInicio, campoHoraInicio);
        HBox.setHgrow(boxHoraInicio, Priority.ALWAYS);

        Label lblHoraFim = new Label("HORÁRIO DE TÉRMINO");
        lblHoraFim.getStyleClass().add("secao");
        VBox boxHoraFim = new VBox(5, lblHoraFim, campoHoraFim);
        HBox.setHgrow(boxHoraFim, Priority.ALWAYS);

        HBox boxHoras = new HBox(12, boxHoraInicio, boxHoraFim);

        VBox conteudo = new VBox(14,
                labelCabecalho,
                labelSubtitulo,
                boxData,
                boxHoras
        );
        conteudo.setPrefWidth(460);
        conteudo.getStyleClass().add("card");
        dialogPane.setContent(conteudo);

        ButtonType btnSalvar = new ButtonType("Agendar prova", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogPane.getButtonTypes().setAll(btnCancelar, btnSalvar);

        Button okButtonNode = (Button) dialogPane.lookupButton(btnSalvar);
        if (okButtonNode != null) {
            okButtonNode.getStyleClass().addAll("botao-primario");
        }
        Button cancelButtonNode = (Button) dialogPane.lookupButton(btnCancelar);
        if (cancelButtonNode != null) {
            cancelButtonNode.getStyleClass().addAll("botao-secundario");
        }

        if (dialogo.showAndWait().orElse(btnCancelar) != btnSalvar)
            return;

        LocalDate dataEscolhida = campoData.getValue();
        if (dataEscolhida == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecione uma data para a prova.");
            return;
        }

        String textoHoraInicio = campoHoraInicio.getEditor().getText() != null ? campoHoraInicio.getEditor().getText().trim() : "";
        if (!textoHoraInicio.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Horário de início inválido. Informe no formato HH:mm (exemplo: 07:10 ou 18:45).");
            return;
        }

        java.time.LocalTime horarioInicio = java.time.LocalTime.parse(
                textoHoraInicio.length() == 4 ? "0" + textoHoraInicio : textoHoraInicio,
                DateTimeFormatter.ofPattern("HH:mm")
        );
        java.time.LocalDateTime dataHoraEscolhida = java.time.LocalDateTime.of(dataEscolhida, horarioInicio);

        try {
            provaServico.validarAgendamento(dataHoraEscolhida);
        } catch (IllegalArgumentException e) {
            mostrarAlerta(Alert.AlertType.WARNING, e.getMessage());
            return;
        }

        java.time.LocalDateTime dataHoraFimEscolhida = null;
        String textoHoraFim = campoHoraFim.getEditor().getText() != null ? campoHoraFim.getEditor().getText().trim() : "";
        if (!textoHoraFim.isEmpty()) {
            if (!textoHoraFim.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {
                mostrarAlerta(Alert.AlertType.WARNING, "Horário de término inválido. Informe no formato HH:mm (exemplo: 08:40 ou 20:25).");
                return;
            }
            java.time.LocalTime horarioFim = java.time.LocalTime.parse(
                    textoHoraFim.length() == 4 ? "0" + textoHoraFim : textoHoraFim,
                    DateTimeFormatter.ofPattern("HH:mm")
            );
            dataHoraFimEscolhida = java.time.LocalDateTime.of(dataEscolhida, horarioFim);
            if (!dataHoraFimEscolhida.isAfter(dataHoraEscolhida)) {
                mostrarAlerta(Alert.AlertType.WARNING, "O horário de término deve ser posterior ao horário de início.");
                return;
            }
        }

        java.time.LocalDateTime anteriorInicio = prova.getDataHora();
        java.time.LocalDateTime anteriorFim = prova.getDataHoraFim();
        try {
            prova.setDataHora(dataHoraEscolhida);
            prova.setDataHoraFim(dataHoraFimEscolhida);
            provaServico.salvarProva(prova);
            atualizar();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Prova salva com sucesso. Código: " + prova.getCodigo());
        } catch (RuntimeException e) {
            prova.setDataHora(anteriorInicio);
            prova.setDataHoraFim(anteriorFim);
            erro("Não foi possível agendar a prova.", e);
        }
    }

    // Exporta a prova em texto, sem indicar as alternativas corretas
    @FXML
    private void baixarProva(ActionEvent event) {
        Prova prova = selecionada.get();
        if (prova == null)
            return;
        FileChooser seletor = new FileChooser();
        seletor.setTitle("Baixar prova");
        seletor.setInitialFileName(prova.getCodigo() + ".txt");
        seletor.getExtensionFilters().add(new FileChooser.ExtensionFilter("Texto", "*.txt"));
        File arquivo = seletor.showSaveDialog(palcoDe(event));
        if (arquivo == null)
            return;

        String textoData = "";
        if (prova.getDataHora() != null) {
            textoData = " — " + prova.getDataHora().format(FORMATO_DATA_HORA);
        } else if (prova.getData() != null) {
            textoData = " — " + prova.getData().format(FORMATO_DATA);
        }
        StringBuilder texto = new StringBuilder()
                .append(prova.getCodigo()).append(" — ").append(prova.getTitulo()).append('\n')
                .append(descricao(prova))
                .append(textoData)
                .append("\n\n");
        List<QuestaoComPeso> questoes = provaServico.listarQuestoes(prova.getCodigo());
        for (int i = 0; i < questoes.size(); i++) {
            QuestaoComPeso item = questoes.get(i);
            texto.append(i + 1).append(") ").append(item.getQuestao().getTitulo())
                    .append(String.format(Locale.forLanguageTag("pt-BR"), "  (peso %.1f)", item.getPeso()))
                    .append('\n');
            List<Alternativa> alternativas = item.getQuestao().getAlternativas();
            for (int j = 0; j < alternativas.size(); j++)
                texto.append("   ").append(CardQuestao.letra(j)).append(' ').append(alternativas.get(j).getTexto())
                        .append('\n');
            texto.append('\n');
        }
        try {
            Files.writeString(arquivo.toPath(), texto.toString(), StandardCharsets.UTF_8);
            Alert aviso = new Alert(Alert.AlertType.INFORMATION, "Prova salva em " + arquivo.getAbsolutePath());
            aviso.setHeaderText(null);
            aviso.showAndWait();
        } catch (IOException e) {
            erro("Não foi possível salvar o arquivo da prova.", e);
        }
    }

    @FXML
    private void abrirBancoQuestoes(ActionEvent event) {
        try {
            BancoQuestoes.abrir(palcoDe(event));
        } catch (Exception e) {
            erro("Não foi possível abrir o banco de questões.", e);
        }
    }

    @FXML
    private void gerenciarQuestoes(ActionEvent event) {
        Prova prova = selecionada.get();
        String registroLogado = Sessao.getUsuarioLogado().getRegistro();
        if (ehDona(prova) == false) {
            Alert aviso = new Alert(Alert.AlertType.WARNING, "Você não tem permissão para editar esta prova.");
            aviso.setHeaderText(null);
            aviso.showAndWait();
            return;
        }
        if (prova == null)
            return;
        try {
            ComposicaoProva.abrir(palcoDe(event), prova, provaServico, telas.prova.Prova.getQuestaoServico());
        } catch (Exception e) {
            erro("Não foi possível abrir a composição da prova.", e);
        }
    }

    @FXML
    private void corrigir(ActionEvent event) {
        Prova prova = selecionada.get();
        if (prova == null)
            return;
        try {
            CorrigirProva.abrir(palcoDe(event), prova, provaServico);
        } catch (Exception e) {
            erro("Não foi possível abrir a correção.", e);
        }
    }

    private Stage palcoDe(ActionEvent event) {
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }

    private void erro(String mensagem, Exception e) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, mensagem + "\n" + e.getMessage());
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensagem) {
        Alert alerta = new Alert(tipo, mensagem);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }

    @FXML
    private void voltar(ActionEvent event) {
        try {
            Sessao.encerrar();
            telas.login.Login.abrir(palcoDe(event));
        } catch (Exception e) {
            erro("Não foi possível voltar para a tela de login.", e);
        }
    }

    @FXML
    private void aplicar(ActionEvent event) {
        Prova prova = selecionada.get();
        if (prova == null)
            return;
        if (!ehDona(prova)) {
            Alert aviso = new Alert(Alert.AlertType.WARNING, "Você não tem permissão para aplicar esta prova.");
            aviso.setHeaderText(null);
            aviso.showAndWait();
            return;
        }
        try {
            AlunoProva.abrir(palcoDe(event), prova, provaServico, telas.prova.Prova.getUsuarioStorage());
        } catch (Exception e) {
            erro("Não foi possível abrir a tela de aplicação da prova.", e);
        }
    }

    private boolean ehDona(Prova prova) {
        Usuario logado = Sessao.getUsuarioLogado();
        return logado != null && Objects.equals(prova.getRegistroProfessor(), logado.getRegistro());
    }

}
