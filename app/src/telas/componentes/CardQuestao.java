package telas.componentes;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import modelos.Alternativa;
import modelos.Questao;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public final class CardQuestao {
    private static final String TIPO_MULTIPLA_ESCOLHA = "Múltipla escolha";
    private static final String TIPO_CORRESPONDENCIA = "Correspondência";
    private static final String TIPO_VERDADEIRO_FALSO_SEQUENCIA = "Verdadeiro ou falso em sequência";

    private CardQuestao() { }

    // peso == null esconde a etiqueta de peso (questão fora de uma prova)
    public static VBox visualizar(int posicao, Questao questao, Double peso) {
        VBox card = new VBox();
        card.getStyleClass().add("card-questao");

        Label numero = new Label(posicao + ")");
        numero.getStyleClass().add("questao-numero");
        Label titulo = new Label("Questão " + questao.getNumero());
        titulo.getStyleClass().add("questao-titulo");
        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);
        HBox cabecalho = new HBox(8, numero, titulo, espaco);
        cabecalho.setAlignment(Pos.CENTER_LEFT);

        Label tipo = new Label(rotuloTipo(questao.getDescricao()));
        tipo.getStyleClass().add("questao-tipo");
        cabecalho.getChildren().add(tipo);
        if (peso != null) {
            Label etiquetaPeso = new Label(String.format(Locale.forLanguageTag("pt-BR"), "Peso %.1f", peso));
            etiquetaPeso.getStyleClass().add("questao-peso");
            cabecalho.getChildren().add(etiquetaPeso);
        }

        Label pergunta = new Label(questao.getTitulo());
        pergunta.getStyleClass().add("questao-pergunta");
        pergunta.setWrapText(true);
        pergunta.setMaxWidth(Double.MAX_VALUE);

        List<Alternativa> alternativas = questao.getAlternativas();
        VBox conteudo = switch (rotuloTipo(questao.getDescricao())) {
            case TIPO_CORRESPONDENCIA -> listaCorrespondencias(alternativas);
            case TIPO_VERDADEIRO_FALSO_SEQUENCIA -> listaAfirmacoes(alternativas);
            default -> listaAlternativas(alternativas);
        };

        card.getChildren().addAll(cabecalho, pergunta, conteudo);
        return card;
    }

    public static String rotuloTipo(String descricao) {
        if (TIPO_CORRESPONDENCIA.equals(descricao)) return TIPO_CORRESPONDENCIA;
        if (TIPO_VERDADEIRO_FALSO_SEQUENCIA.equals(descricao)) return TIPO_VERDADEIRO_FALSO_SEQUENCIA;
        return TIPO_MULTIPLA_ESCOLHA;
    }

    // Letra da alternativa a partir da posição: 0 -> "a)"
    public static String letra(int indice) { return (char) ('a' + indice) + ")"; }

    private static VBox listaAlternativas(List<Alternativa> alternativas) {
        VBox lista = novaLista("Alternativas");
        for (int i = 0; i < alternativas.size(); i++) {
            Alternativa alternativa = alternativas.get(i);
            Label letra = new Label(letra(i));
            letra.getStyleClass().add("alternativa-letra");
            Label texto = texto(alternativa.getTexto(), "alternativa-texto");
            HBox.setHgrow(texto, Priority.ALWAYS);

            Label correta = new Label(alternativa.isCorreta() ? "Correta" : "");
            if (alternativa.isCorreta()) correta.getStyleClass().add("alternativa-correta");
            HBox linha = new HBox(10, letra, texto, correta);
            linha.setAlignment(Pos.CENTER_LEFT);
            linha.getStyleClass().add("linha-alternativa-leitura");
            lista.getChildren().add(linha);
        }
        adicionarVazioSeNecessario(lista, alternativas);
        return lista;
    }

    private static VBox listaCorrespondencias(List<Alternativa> pares) {
        VBox lista = novaLista("Pares correspondentes");
        for (int i = 0; i < pares.size(); i++) {
            Alternativa par = pares.get(i);
            Label numero = new Label(String.valueOf(i + 1));
            numero.getStyleClass().add("par-numero");

            VBox conceito = colunaPar("CONCEITO", par.getTexto());
            VBox definicao = colunaPar("CORRESPONDÊNCIA", par.getTextoCorrespondencia());
            HBox.setHgrow(conceito, Priority.ALWAYS);
            HBox.setHgrow(definicao, Priority.ALWAYS);

            Label seta = new Label("→");
            seta.getStyleClass().add("par-seta");
            HBox linha = new HBox(12, numero, conceito, seta, definicao);
            linha.setAlignment(Pos.CENTER_LEFT);
            linha.getStyleClass().add("linha-par-leitura");
            lista.getChildren().add(linha);
        }
        adicionarVazioSeNecessario(lista, pares);
        return lista;
    }

    private static VBox listaAfirmacoes(List<Alternativa> afirmacoes) {
        VBox lista = novaLista("Afirmações");
        if (!afirmacoes.isEmpty()) {
            String sequencia = afirmacoes.stream()
                    .map(a -> a.isCorreta() ? "V" : "F")
                    .collect(Collectors.joining(", "));
            Label resumo = new Label("Sequência correta: " + sequencia);
            resumo.getStyleClass().add("resumo-sequencia");
            lista.getChildren().add(resumo);
        }

        for (int i = 0; i < afirmacoes.size(); i++) {
            Alternativa afirmacao = afirmacoes.get(i);
            Label numero = new Label((i + 1) + ".");
            numero.getStyleClass().add("par-numero");
            Label texto = texto(afirmacao.getTexto(), "alternativa-texto");
            HBox.setHgrow(texto, Priority.ALWAYS);

            String resposta = afirmacao.isCorreta() ? "Verdadeiro" : "Falso";
            Label marcador = new Label(resposta);
            marcador.getStyleClass().addAll("marcador-sequencia",
                    afirmacao.isCorreta() ? "verdadeiro" : "falso");

            HBox linha = new HBox(10, numero, texto, marcador);
            linha.setAlignment(Pos.CENTER_LEFT);
            linha.getStyleClass().add("linha-alternativa-leitura");
            lista.getChildren().add(linha);
        }
        adicionarVazioSeNecessario(lista, afirmacoes);
        return lista;
    }

    private static VBox novaLista(String titulo) {
        Label cabecalho = new Label(titulo);
        cabecalho.getStyleClass().add("secao-questao-titulo");
        VBox lista = new VBox(8, cabecalho);
        lista.getStyleClass().add("lista-leitura-questao");
        return lista;
    }

    private static VBox colunaPar(String titulo, String valor) {
        Label rotulo = new Label(titulo);
        rotulo.getStyleClass().add("par-rotulo");
        Label conteudo = texto(valor, "par-texto");
        VBox coluna = new VBox(4, rotulo, conteudo);
        coluna.getStyleClass().add("coluna-par-leitura");
        return coluna;
    }

    private static Label texto(String valor, String classe) {
        Label label = new Label(valor == null || valor.isBlank() ? "Não informado" : valor);
        label.getStyleClass().add(classe);
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);
        return label;
    }

    private static void adicionarVazioSeNecessario(VBox lista, List<Alternativa> alternativas) {
        if (alternativas.isEmpty()) {
            Label semAlternativas = new Label("Esta questão não tem alternativas cadastradas.");
            semAlternativas.getStyleClass().add("vazio");
            lista.getChildren().add(semAlternativas);
        }
    }
}
