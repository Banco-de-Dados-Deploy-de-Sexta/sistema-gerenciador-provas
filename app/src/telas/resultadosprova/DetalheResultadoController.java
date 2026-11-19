package telas.resultadosprova;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Comparator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import modelos.Alternativa;
import modelos.Prova;
import modelos.ProvaAluno;
import modelos.QuestaoComPeso;
import modelos.RespostaAluno;
import servicos.ProvaServico;
import telas.componentes.CardQuestao;
import storage.UsuarioStorage;

public class DetalheResultadoController {
    private static final String TIPO_MULTIPLA_ESCOLHA = "Múltipla escolha";
    private static final String TIPO_CORRESPONDENCIA = "Correspondência";
    private static final String TIPO_SEQUENCIA = "Verdadeiro ou falso em sequência";
    private static final String TIPO_DISSERTATIVA = "Dissertativa";

    @FXML private Label tituloTela;
    @FXML private Label alunoLabel;
    @FXML private Label notaFinalLabel;
    @FXML private VBox questoesContainer;

    private final Prova prova;
    private final String registroAluno;
    private final String nomeAluno;
    private final ProvaServico provaServico;
    private final UsuarioStorage usuarioStorage;

    public DetalheResultadoController(Prova prova, String registroAluno, String nomeAluno,
            ProvaServico provaServico, UsuarioStorage usuarioStorage) {
        this.prova = prova;
        this.registroAluno = registroAluno;
        this.nomeAluno = nomeAluno;
        this.provaServico = provaServico;
        this.usuarioStorage = usuarioStorage;
    }

    public void carregar() {
        // A consulta protegida valida novamente se quem abriu é o professor dono da prova.
        List<ProvaAluno> resultados = provaServico.listarResultadosProva(prova.getCodigo());
        ProvaAluno entrega = resultados.stream()
                .filter(item -> registroAluno.equals(item.getRegistroAluno()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("O aluno não está vinculado a esta prova."));

        tituloTela.setText("Desempenho do aluno");
        alunoLabel.setText(nomeAluno + "  •  ID " + registroAluno);
        notaFinalLabel.setText(entrega.getNotaFinal() == null
                ? entrega.isRealizada() ? "Entrega aguardando correção" : "Prova ainda não realizada"
                : "Nota final: " + formatar(entrega.getNotaFinal()) + " / 10,00");

        questoesContainer.getChildren().clear();
        if (!entrega.isRealizada()) {
            questoesContainer.getChildren().add(mensagem("Este aluno ainda não entregou a prova."));
            return;
        }

        Map<Integer, RespostaAluno> respostas = new HashMap<>();
        for (RespostaAluno resposta : provaServico.listarRespostasResultadoProva(prova.getCodigo(), registroAluno))
            respostas.put(resposta.getIdQuestao(), resposta);

        List<QuestaoComPeso> questoes = new java.util.ArrayList<>(provaServico.listarQuestoes(prova.getCodigo()));
        questoes.sort(Comparator.comparingInt(item -> {
            RespostaAluno resposta = respostas.get((int) item.getQuestao().getNumero());
            return resposta == null || resposta.getOrdemQuestao() <= 0
                    ? Integer.MAX_VALUE
                    : resposta.getOrdemQuestao();
        }));
        for (int i = 0; i < questoes.size(); i++) {
            QuestaoComPeso item = questoes.get(i);
            int idQuestao = (int) item.getQuestao().getNumero();
            questoesContainer.getChildren().add(criarCartaoQuestao(
                    i + 1, item, respostas.get(idQuestao)));
        }
        if (questoes.isEmpty()) questoesContainer.getChildren().add(mensagem("Esta prova não possui questões."));
    }

    private VBox criarCartaoQuestao(int posicao, QuestaoComPeso item, RespostaAluno resposta) {
        var questao = item.getQuestao();
        String tipo = questao.getDescricao() == null ? "" : questao.getDescricao().trim();
        List<Alternativa> alternativas = questao.getAlternativas();
        if (alternativas == null) alternativas = List.of();
        boolean correspondencia = TIPO_CORRESPONDENCIA.equalsIgnoreCase(tipo);
        boolean sequencia = TIPO_SEQUENCIA.equalsIgnoreCase(tipo);
        boolean dissertativa = TIPO_DISSERTATIVA.equalsIgnoreCase(tipo);
        // Questões antigas guardavam a instrução no campo descricao (por exemplo,
        // "Assinale a alternativa correta:") em vez do nome do tipo.
        boolean multiplaEscolha = TIPO_MULTIPLA_ESCOLHA.equalsIgnoreCase(tipo)
                || (!correspondencia && !sequencia && !dissertativa && !alternativas.isEmpty());

        VBox cartao = new VBox(10);
        cartao.getStyleClass().add("question-row");

        Label numeroPeso = new Label("Questão " + posicao + "  •  Peso " + formatar(item.getPeso()));
        numeroPeso.getStyleClass().add("step-title");
        numeroPeso.setWrapText(true);
        Label enunciado = new Label(questao.getTitulo());
        enunciado.getStyleClass().add("card-titulo");
        enunciado.setWrapText(true);

        VBox conteudo = new VBox(7);
        double pontosObtidos;
        String situacao;
        String classeSituacao;

        if (multiplaEscolha) {
            Integer idMarcada = resposta == null ? null : resposta.getIdAlternativaEscolhida();
            Alternativa marcada = encontrarPorId(alternativas, idMarcada);
            Alternativa correta = alternativas.stream().filter(Alternativa::isCorreta).findFirst().orElse(null);
            boolean acertou = marcada != null && marcada.isCorreta();
            pontosObtidos = notaSalvaOu(resposta, acertou ? item.getPeso() : 0.0);
            situacao = acertou ? "Acertou" : "Errou";
            classeSituacao = acertou ? "status-correto" : "status-incorreto";
            conteudo.getChildren().addAll(
                    detalhe("Tipo", TIPO_MULTIPLA_ESCOLHA),
                    detalhe("Resposta do aluno", rotular(alternativas, marcada)),
                    detalhe("Resposta correta", rotular(alternativas, correta)));
        } else if (TIPO_CORRESPONDENCIA.equalsIgnoreCase(tipo)) {
            int acertos = renderizarCorrespondencia(conteudo, alternativas, resposta);
            double proporcional = alternativas.isEmpty() ? 0.0 : item.getPeso() * acertos / alternativas.size();
            pontosObtidos = notaSalvaOu(resposta, proporcional);
            situacao = situacaoProporcional(acertos, alternativas.size());
            classeSituacao = classeProporcional(acertos, alternativas.size());
        } else if (TIPO_SEQUENCIA.equalsIgnoreCase(tipo)) {
            int acertos = renderizarSequencia(conteudo, alternativas, resposta);
            double proporcional = alternativas.isEmpty() ? 0.0 : item.getPeso() * acertos / alternativas.size();
            pontosObtidos = notaSalvaOu(resposta, proporcional);
            situacao = situacaoProporcional(acertos, alternativas.size());
            classeSituacao = classeProporcional(acertos, alternativas.size());
        } else {
            String texto = resposta == null ? "" : resposta.getRespostaTexto();
            boolean respondida = texto != null && !texto.isBlank();
            Double nota = resposta == null ? null : resposta.getNotaObtida();
            pontosObtidos = nota == null ? 0.0 : nota;
            if (!respondida) {
                situacao = "Sem resposta";
                classeSituacao = "status-incorreto";
            } else if (nota == null) {
                situacao = "Aguardando correção do professor";
                classeSituacao = "status-pendente";
            } else {
                situacao = "Corrigida pelo professor";
                classeSituacao = "status-correto";
            }
            conteudo.getChildren().add(detalhe("Resposta do aluno", respondida ? texto : "Não respondeu"));
            if (!dissertativa)
                conteudo.getChildren().add(detalhe("Tipo", tipo.isBlank() ? "Resposta aberta" : tipo));
        }

        HBox cabecalho = new HBox(10);
        cabecalho.setAlignment(Pos.CENTER_LEFT);
        Label estado = new Label(situacao);
        estado.getStyleClass().addAll("status-badge", classeSituacao);
        javafx.scene.layout.Region espaco = new javafx.scene.layout.Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);
        Label pontuacao = new Label("Pontuação: " + formatar(pontosObtidos) + " / " + formatar(item.getPeso()));
        pontuacao.getStyleClass().add("card-destaque");
        cabecalho.getChildren().addAll(estado, espaco, pontuacao);
        cartao.getChildren().addAll(numeroPeso, enunciado, cabecalho, conteudo);
        return cartao;
    }

    private int renderizarCorrespondencia(VBox conteudo, List<Alternativa> pares, RespostaAluno resposta) {
        String[] idsEscolhidos = separar(resposta == null ? null : resposta.getRespostaTexto());
        int acertos = 0;
        for (int i = 0; i < pares.size(); i++) {
            Alternativa par = pares.get(i);
            Integer idEscolhido = inteiroNaPosicao(idsEscolhidos, i);
            Alternativa definicao = encontrarPorId(pares, idEscolhido);
            boolean acertou = idEscolhido != null && idEscolhido == par.getId();
            if (acertou) acertos++;
            conteudo.getChildren().add(detalheItem(
                    (i + 1) + ". " + par.getTexto(),
                    "Resposta do aluno: " + (definicao == null ? "Não respondeu" : textoOuVazio(definicao.getTextoCorrespondencia())),
                    "Correspondência correta: " + textoOuVazio(par.getTextoCorrespondencia()),
                    acertou ? "Acertou" : "Errou",
                    acertou ? "status-correto" : "status-incorreto"));
        }
        return acertos;
    }

    private int renderizarSequencia(VBox conteudo, List<Alternativa> afirmacoes, RespostaAluno resposta) {
        String[] respostas = separar(resposta == null ? null : resposta.getRespostaTexto());
        int acertos = 0;
        for (int i = 0; i < afirmacoes.size(); i++) {
            Alternativa afirmacao = afirmacoes.get(i);
            String respostaAluno = textoNaPosicao(respostas, i).toUpperCase(Locale.ROOT);
            String gabarito = afirmacao.isCorreta() ? "V" : "F";
            boolean acertou = gabarito.equals(respostaAluno);
            if (acertou) acertos++;
            conteudo.getChildren().add(detalheItem(
                    (i + 1) + ". " + afirmacao.getTexto(),
                    "Resposta do aluno: " + traduzirVF(respostaAluno),
                    "Resposta correta: " + traduzirVF(gabarito),
                    acertou ? "Acertou" : "Errou",
                    acertou ? "status-correto" : "status-incorreto"));
        }
        return acertos;
    }

    private VBox detalheItem(String titulo, String respostaAluno, String gabarito, String status, String classeStatus) {
        VBox linha = new VBox(4);
        linha.getStyleClass().add("answer-row");
        Label conceito = new Label(titulo);
        conceito.getStyleClass().add("step-title");
        conceito.setWrapText(true);
        Label aluno = new Label(respostaAluno);
        aluno.setWrapText(true);
        Label correta = new Label(gabarito);
        correta.setWrapText(true);
        correta.getStyleClass().add("subtitulo");
        Label resultado = new Label(status);
        resultado.getStyleClass().addAll("status-badge", classeStatus);
        HBox statusLinha = new HBox(resultado);
        statusLinha.setAlignment(Pos.CENTER_RIGHT);
        linha.getChildren().addAll(conceito, aluno, correta, statusLinha);
        return linha;
    }

    private Label detalhe(String titulo, String valor) {
        Label linha = new Label(titulo + ": " + textoOuVazio(valor));
        linha.setWrapText(true);
        return linha;
    }

    private Label mensagem(String texto) {
        Label label = new Label(texto);
        label.getStyleClass().add("subtitulo");
        label.setWrapText(true);
        return label;
    }

    private String[] separar(String resposta) {
        return resposta == null ? new String[0] : resposta.split(",", -1);
    }

    private Integer inteiroNaPosicao(String[] valores, int posicao) {
        if (posicao >= valores.length || valores[posicao].isBlank()) return null;
        try {
            return Integer.parseInt(valores[posicao].trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String textoNaPosicao(String[] valores, int posicao) {
        return posicao >= valores.length ? "" : valores[posicao].trim();
    }

    private Alternativa encontrarPorId(List<Alternativa> alternativas, Integer id) {
        if (id == null) return null;
        return alternativas.stream().filter(item -> item.getId() == id).findFirst().orElse(null);
    }

    private String rotular(List<Alternativa> alternativas, Alternativa selecionada) {
        if (selecionada == null) return "Não respondeu";
        int indice = alternativas.indexOf(selecionada);
        return CardQuestao.letra(indice) + " " + selecionada.getTexto();
    }

    private String situacaoProporcional(int acertos, int total) {
        if (total == 0 || acertos == 0) return "Errou";
        return acertos == total ? "Acertou" : "Parcial: " + acertos + " de " + total;
    }

    private String classeProporcional(int acertos, int total) {
        if (acertos == 0 || total == 0) return "status-incorreto";
        return acertos == total ? "status-correto" : "status-parcial";
    }

    private String traduzirVF(String resposta) {
        return switch (resposta) {
            case "V" -> "Verdadeiro";
            case "F" -> "Falso";
            default -> "Não respondeu";
        };
    }

    private String textoOuVazio(String texto) {
        return texto == null || texto.isBlank() ? "—" : texto;
    }

    private String formatar(double nota) {
        return String.format(Locale.forLanguageTag("pt-BR"), "%.2f", nota);
    }

    private double notaSalvaOu(RespostaAluno resposta, double calculada) {
        return resposta != null && resposta.getNotaObtida() != null
                ? resposta.getNotaObtida()
                : calculada;
    }

    @FXML
    private void voltar(ActionEvent event) {
        try {
            ResultadosProva.abrir((Stage) tituloTela.getScene().getWindow(), prova, provaServico, usuarioStorage);
        } catch (Exception e) {
            ((Stage) tituloTela.getScene().getWindow()).close();
        }
    }
}
