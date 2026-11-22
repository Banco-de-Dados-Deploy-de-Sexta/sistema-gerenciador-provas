package telas.minhaprova;

import auxiliares.Embaralhamento;
import auxiliares.Sessao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import modelos.Alternativa;
import modelos.Prova;
import modelos.ProvaAluno;
import modelos.Questao;
import modelos.QuestaoComPeso;
import modelos.RespostaAluno;
import modelos.Usuario;
import servicos.ProvaServico;
import telas.aluno.Aluno;
import telas.componentes.CardQuestao;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// PB06: o aluno vê as próprias respostas e, depois da correção, a nota e o resultado de cada questão.
// PB11: o aluno salva em .txt somente as provas que ele mesmo realizou.
public class MinhaProvaController {
    private static final String TIPO_CORRESPONDENCIA = "Correspondência";
    private static final String TIPO_SEQUENCIA = "Verdadeiro ou falso em sequência";
    private static final String TIPO_DISSERTATIVA = "Dissertativa";
    private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");
    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    @FXML private Label tituloTela;
    @FXML private Label detalhesLabel;
    @FXML private Label notaFinalLabel;
    @FXML private Label instrucaoLabel;
    @FXML private VBox questoesContainer;
    @FXML private Button baixarButton;

    private final Prova prova;
    private final ProvaServico provaServico;
    private Usuario aluno;
    private ProvaAluno entrega;
    private boolean corrigida;
    private final List<Bloco> blocos = new ArrayList<>();

    public MinhaProvaController(Prova prova, ProvaServico provaServico) {
        this.prova = prova;
        this.provaServico = provaServico;
    }

    public void carregar() {
        aluno = Sessao.getUsuarioLogado();
        tituloTela.setText(prova.getTitulo());
        detalhesLabel.setText(descricaoProva());
        questoesContainer.getChildren().clear();
        blocos.clear();

        entrega = aluno == null ? null : provaServico.buscarRealizacaoAluno(prova.getCodigo(), aluno.getRegistro());
        if (entrega == null || !entrega.isRealizada()) {
            notaFinalLabel.setText("");
            instrucaoLabel.setText("");
            questoesContainer.getChildren().add(mensagem("Você ainda não realizou esta prova. "
                    + "As questões e as suas respostas aparecem aqui depois da entrega."));
            baixarButton.setDisable(true);
            return;
        }

        corrigida = "CORRIGIDA".equalsIgnoreCase(entrega.getStatus()) && entrega.getNotaFinal() != null;
        notaFinalLabel.setText(corrigida
                ? "Nota final: " + formatar(entrega.getNotaFinal()) + " / 10,00"
                : "Nota pendente: aguardando a correção do professor");
        instrucaoLabel.setText(corrigida
                ? "Veja a sua resposta, a resposta correta e a pontuação de cada questão."
                : "Estas são as respostas que você enviou. O resultado de cada questão aparece após a correção.");

        Map<Integer, RespostaAluno> respostas = new HashMap<>();
        for (RespostaAluno resposta : provaServico.listarRespostasAluno(prova.getCodigo(), aluno.getRegistro()))
            respostas.put(resposta.getIdQuestao(), resposta);

        // Mesma ordem em que as questões foram exibidas ao aluno na realização (PB15)
        List<QuestaoComPeso> questoes = new ArrayList<>(provaServico.listarQuestoes(prova.getCodigo()));
        questoes.sort(Comparator.comparingInt(item -> {
            RespostaAluno resposta = respostas.get((int) item.getQuestao().getNumero());
            return resposta == null || resposta.getOrdemQuestao() <= 0 ? Integer.MAX_VALUE : resposta.getOrdemQuestao();
        }));
        for (int i = 0; i < questoes.size(); i++) {
            QuestaoComPeso item = questoes.get(i);
            Bloco bloco = montarBloco(i + 1, item, respostas.get((int) item.getQuestao().getNumero()));
            blocos.add(bloco);
            questoesContainer.getChildren().add(cartao(bloco));
        }
        if (questoes.isEmpty()) questoesContainer.getChildren().add(mensagem("Esta prova não possui questões."));
        baixarButton.setDisable(false);
    }

    // ---------- Montagem do resultado de cada questão ----------

    private record Linha(String titulo, String respostaAluno, String gabarito, Boolean acertou) { }

    private record Bloco(int posicao, Questao questao, String tipo, double peso, List<Linha> linhas,
                         String situacao, String classeSituacao, Double pontos) { }

    private Bloco montarBloco(int posicao, QuestaoComPeso item, RespostaAluno resposta) {
        Questao questao = item.getQuestao();
        String tipo = CardQuestao.rotuloTipo(questao.getDescricao());
        List<Alternativa> alternativas = questao.getAlternativas() == null ? List.of() : questao.getAlternativas();
        List<Linha> linhas = new ArrayList<>();
        String situacao;
        String classe;
        double pontos;

        switch (tipo) {
            case TIPO_CORRESPONDENCIA -> {
                String[] escolhidos = separar(resposta);
                int acertos = 0;
                List<Alternativa> exibidos = Embaralhamento.conceitos(
                        alternativas, prova.getCodigo(), aluno.getRegistro(), questao.getNumero());
                for (int i = 0; i < exibidos.size(); i++) {
                    Alternativa par = exibidos.get(i);
                    Integer idEscolhido = inteiroNaPosicao(escolhidos, alternativas.indexOf(par));
                    Alternativa definicao = porId(alternativas, idEscolhido);
                    boolean acertou = idEscolhido != null && idEscolhido == par.getId();
                    if (acertou) acertos++;
                    linhas.add(new Linha((i + 1) + ". " + par.getTexto(),
                            definicao == null ? "Não respondeu" : valor(definicao.getTextoCorrespondencia()),
                            valor(par.getTextoCorrespondencia()), acertou));
                }
                pontos = proporcional(item.getPeso(), acertos, alternativas.size());
                situacao = situacaoProporcional(acertos, alternativas.size());
                classe = classeProporcional(acertos, alternativas.size());
            }
            case TIPO_SEQUENCIA -> {
                String[] marcadas = separar(resposta);
                int acertos = 0;
                List<Alternativa> exibidas = Embaralhamento.afirmacoes(
                        alternativas, prova.getCodigo(), aluno.getRegistro(), questao.getNumero());
                for (int i = 0; i < exibidas.size(); i++) {
                    Alternativa afirmacao = exibidas.get(i);
                    int original = alternativas.indexOf(afirmacao);
                    String respostaAluno = original < marcadas.length ? marcadas[original].trim().toUpperCase(Locale.ROOT) : "";
                    String gabarito = afirmacao.isCorreta() ? "V" : "F";
                    boolean acertou = gabarito.equals(respostaAluno);
                    if (acertou) acertos++;
                    linhas.add(new Linha((i + 1) + ". " + afirmacao.getTexto(),
                            verdadeiroFalso(respostaAluno), verdadeiroFalso(gabarito), acertou));
                }
                pontos = proporcional(item.getPeso(), acertos, alternativas.size());
                situacao = situacaoProporcional(acertos, alternativas.size());
                classe = classeProporcional(acertos, alternativas.size());
            }
            case TIPO_DISSERTATIVA -> {
                String texto = resposta == null ? null : resposta.getRespostaTexto();
                boolean respondida = texto != null && !texto.isBlank();
                Double nota = resposta == null ? null : resposta.getNotaObtida();
                linhas.add(new Linha(null, respondida ? texto : "Não respondeu", null, null));
                pontos = nota == null ? 0.0 : nota;
                if (!respondida) { situacao = "Sem resposta"; classe = "status-incorreto"; }
                else if (nota == null) { situacao = "Aguardando correção"; classe = "status-pendente"; }
                else { situacao = "Corrigida pelo professor"; classe = "status-correto"; }
            }
            default -> {
                Alternativa marcada = porId(alternativas, resposta == null ? null : resposta.getIdAlternativaEscolhida());
                Alternativa correta = alternativas.stream().filter(Alternativa::isCorreta).findFirst().orElse(null);
                boolean acertou = marcada != null && marcada.isCorreta();
                linhas.add(new Linha(null, marcada == null ? "Não respondeu" : marcada.getTexto(),
                        correta == null ? "—" : correta.getTexto(), acertou));
                pontos = acertou ? item.getPeso() : 0.0;
                situacao = acertou ? "Acertou" : "Errou";
                classe = acertou ? "status-correto" : "status-incorreto";
            }
        }
        // A pontuação gravada na correção prevalece sobre o cálculo local
        if (resposta != null && resposta.getNotaObtida() != null) pontos = resposta.getNotaObtida();
        return new Bloco(posicao, questao, tipo, item.getPeso(), linhas, situacao, classe, pontos);
    }

    // ---------- Exibição na tela ----------

    private VBox cartao(Bloco bloco) {
        VBox cartao = new VBox(10);
        cartao.getStyleClass().add("question-row");

        Label numero = new Label("Questão " + bloco.posicao() + "  •  " + bloco.tipo() + "  •  Peso " + formatar(bloco.peso()));
        numero.getStyleClass().add("subtitulo");
        Label enunciado = new Label(bloco.questao().getTitulo());
        enunciado.getStyleClass().add("step-title");
        enunciado.setWrapText(true);
        cartao.getChildren().addAll(numero, enunciado);

        if (corrigida) {
            Label estado = new Label(bloco.situacao());
            estado.getStyleClass().addAll("status-badge", bloco.classeSituacao());
            Region espaco = new Region();
            HBox.setHgrow(espaco, Priority.ALWAYS);
            Label pontuacao = new Label("Pontuação: " + formatar(bloco.pontos()) + " / " + formatar(bloco.peso()));
            pontuacao.getStyleClass().add("step-title");
            HBox cabecalho = new HBox(10, estado, espaco, pontuacao);
            cabecalho.setAlignment(Pos.CENTER_LEFT);
            cartao.getChildren().add(cabecalho);
        }

        VBox conteudo = new VBox(7);
        for (Linha linha : bloco.linhas()) conteudo.getChildren().add(linhaResposta(linha));
        cartao.getChildren().add(conteudo);
        return cartao;
    }

    private VBox linhaResposta(Linha linha) {
        VBox caixa = new VBox(4);
        if (linha.titulo() != null) {
            caixa.getStyleClass().add("answer-row");
            Label titulo = new Label(linha.titulo());
            titulo.getStyleClass().add("step-title");
            titulo.setWrapText(true);
            caixa.getChildren().add(titulo);
        }
        Label sua = new Label("Sua resposta: " + linha.respostaAluno());
        sua.setWrapText(true);
        caixa.getChildren().add(sua);
        if (corrigida && linha.gabarito() != null) {
            Label correta = new Label("Resposta correta: " + linha.gabarito());
            correta.getStyleClass().add("subtitulo");
            correta.setWrapText(true);
            caixa.getChildren().add(correta);
        }
        if (corrigida && linha.titulo() != null && linha.acertou() != null) {
            Label resultado = new Label(linha.acertou() ? "Acertou" : "Errou");
            resultado.getStyleClass().addAll("status-badge", linha.acertou() ? "status-correto" : "status-incorreto");
            HBox direita = new HBox(resultado);
            direita.setAlignment(Pos.CENTER_RIGHT);
            caixa.getChildren().add(direita);
        }
        return caixa;
    }

    private Label mensagem(String texto) {
        Label label = new Label(texto);
        label.getStyleClass().add("subtitulo");
        label.setWrapText(true);
        return label;
    }

    // ---------- PB11: arquivo da prova do aluno ----------

    @FXML
    private void baixarProva(ActionEvent event) {
        // Só quem realizou a prova pode salvá-la (USO22)
        if (aluno == null || entrega == null || !entrega.isRealizada()) {
            mostrar(Alert.AlertType.WARNING, "Você só pode salvar as provas que realizou.");
            return;
        }
        FileChooser seletor = new FileChooser();
        seletor.setTitle("Baixar minha prova");
        seletor.setInitialFileName(prova.getCodigo() + "-" + aluno.getRegistro() + ".txt");
        seletor.getExtensionFilters().add(new FileChooser.ExtensionFilter("Texto", "*.txt"));
        File arquivo = seletor.showSaveDialog(tituloTela.getScene().getWindow());
        if (arquivo == null) return;
        try {
            Files.writeString(arquivo.toPath(), textoArquivo(), StandardCharsets.UTF_8);
            mostrar(Alert.AlertType.INFORMATION, "Prova salva em " + arquivo.getAbsolutePath());
        } catch (IOException e) {
            mostrar(Alert.AlertType.ERROR, "Não foi possível salvar o arquivo da prova.\n" + e.getMessage());
        }
    }

    private String textoArquivo() {
        StringBuilder texto = new StringBuilder()
                .append("Prova: ").append(prova.getCodigo()).append(" — ").append(prova.getTitulo()).append('\n')
                .append(descricaoProva()).append('\n')
                .append("Aluno(a): ").append(aluno.getNome()).append(" (RA ").append(aluno.getRegistro()).append(")\n");
        if (entrega.getDataRealizacao() != null)
            texto.append("Entregue em: ").append(entrega.getDataRealizacao().format(FORMATO_DATA_HORA)).append('\n');
        texto.append(corrigida
                ? "Nota final: " + formatar(entrega.getNotaFinal()) + " / 10,00"
                : "Nota: pendente (aguardando correção)").append("\n\n");

        for (Bloco bloco : blocos) {
            texto.append(bloco.posicao()).append(") ").append(bloco.questao().getTitulo())
                    .append("  (").append(bloco.tipo()).append(", peso ").append(formatar(bloco.peso())).append(")\n");
            if (!TIPO_CORRESPONDENCIA.equals(bloco.tipo()) && !TIPO_SEQUENCIA.equals(bloco.tipo())
                    && !TIPO_DISSERTATIVA.equals(bloco.tipo())) {
                List<Alternativa> exibidas = Embaralhamento.alternativas(bloco.questao().getAlternativas(),
                        prova.getCodigo(), aluno.getRegistro(), bloco.questao().getNumero());
                for (int i = 0; i < exibidas.size(); i++)
                    texto.append("   ").append(CardQuestao.letra(i)).append(' ').append(exibidas.get(i).getTexto()).append('\n');
            }
            for (Linha linha : bloco.linhas()) {
                if (linha.titulo() != null) texto.append("   ").append(linha.titulo()).append('\n');
                String recuo = linha.titulo() != null ? "      " : "   ";
                texto.append(recuo).append("Sua resposta: ").append(linha.respostaAluno()).append('\n');
                if (corrigida && linha.gabarito() != null)
                    texto.append(recuo).append("Resposta correta: ").append(linha.gabarito()).append('\n');
                if (corrigida && linha.titulo() != null && linha.acertou() != null)
                    texto.append(recuo).append(linha.acertou() ? "Acertou" : "Errou").append('\n');
            }
            if (corrigida)
                texto.append("   Resultado: ").append(bloco.situacao())
                        .append(" — ").append(formatar(bloco.pontos())).append(" de ").append(formatar(bloco.peso()))
                        .append(" pontos\n");
            texto.append('\n');
        }
        return texto.toString();
    }

    // ---------- Auxiliares ----------

    private String descricaoProva() {
        String curso = prova.getCurso() == null || prova.getCurso().isBlank() ? "" : prova.getCurso() + " - ";
        return curso + prova.getMateria() + " / " + prova.getBimestre() + "º bimestre";
    }

    private String[] separar(RespostaAluno resposta) {
        String texto = resposta == null ? null : resposta.getRespostaTexto();
        return texto == null ? new String[0] : texto.split(",", -1);
    }

    private Integer inteiroNaPosicao(String[] valores, int posicao) {
        if (posicao >= valores.length || valores[posicao].isBlank()) return null;
        try { return Integer.parseInt(valores[posicao].trim()); }
        catch (NumberFormatException e) { return null; }
    }

    private Alternativa porId(List<Alternativa> alternativas, Integer id) {
        if (id == null) return null;
        return alternativas.stream().filter(a -> a.getId() == id).findFirst().orElse(null);
    }

    private double proporcional(double peso, int acertos, int total) { return total == 0 ? 0.0 : peso * acertos / total; }

    private String situacaoProporcional(int acertos, int total) {
        if (total == 0 || acertos == 0) return "Errou";
        return acertos == total ? "Acertou" : "Parcial: " + acertos + " de " + total;
    }

    private String classeProporcional(int acertos, int total) {
        if (total == 0 || acertos == 0) return "status-incorreto";
        return acertos == total ? "status-correto" : "status-parcial";
    }

    private String verdadeiroFalso(String valor) {
        return switch (valor) {
            case "V" -> "Verdadeiro";
            case "F" -> "Falso";
            default -> "Não respondeu";
        };
    }

    private String valor(String texto) { return texto == null || texto.isBlank() ? "—" : texto; }

    private String formatar(double numero) { return String.format(PT_BR, "%.2f", numero); }

    private void mostrar(Alert.AlertType tipo, String mensagem) {
        Alert alerta = new Alert(tipo, mensagem);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }

    @FXML
    private void voltar(ActionEvent event) {
        try { Aluno.abrir((Stage) tituloTela.getScene().getWindow()); }
        catch (Exception e) { mostrar(Alert.AlertType.ERROR, "Não foi possível voltar às suas provas.\n" + e.getMessage()); }
    }
}
