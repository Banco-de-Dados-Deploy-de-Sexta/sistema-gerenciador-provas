package telas.componentes;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import modelos.Alternativa;
import modelos.Questao;

import java.util.List;
import java.util.Locale;

// Card "VisualizarQuestão" do Figma: número, pergunta em destaque e alternativas com a correta marcada
public final class CardQuestao {
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
        HBox cabecalho = new HBox(numero, titulo, espaco);
        cabecalho.setAlignment(Pos.CENTER_LEFT);
        if (peso != null) {
            Label etiquetaPeso = new Label(String.format(Locale.forLanguageTag("pt-BR"), "Peso %.1f", peso));
            etiquetaPeso.getStyleClass().add("questao-peso");
            cabecalho.getChildren().add(etiquetaPeso);
        }

        Label pergunta = new Label(questao.getTitulo());
        pergunta.getStyleClass().add("questao-pergunta");
        pergunta.setWrapText(true);
        pergunta.setMaxWidth(Double.MAX_VALUE);

        VBox listaAlternativas = new VBox(8);
        List<Alternativa> alternativas = questao.getAlternativas();
        for (int i = 0; i < alternativas.size(); i++)
            listaAlternativas.getChildren().add(linhaAlternativa(i, alternativas.get(i)));
        if (alternativas.isEmpty()) {
            Label semAlternativas = new Label("Esta questão não tem alternativas cadastradas.");
            listaAlternativas.getChildren().add(semAlternativas);
        }

        card.getChildren().addAll(cabecalho, pergunta, listaAlternativas);
        return card;
    }

    // Letra da alternativa a partir da posição: 0 -> "a)"
    public static String letra(int indice) { return (char) ('a' + indice) + ")"; }

    private static HBox linhaAlternativa(int indice, Alternativa alternativa) {
        // Indicador somente leitura de qual alternativa é a correta
        RadioButton seletor = new RadioButton();
        seletor.setSelected(alternativa.isCorreta());
        seletor.setMouseTransparent(true);
        seletor.setFocusTraversable(false);
        Label letra = new Label(letra(indice));
        letra.getStyleClass().add("alternativa-letra");
        Label texto = new Label(alternativa.getTexto());
        texto.setWrapText(true);
        HBox linha = new HBox(6, seletor, letra, texto);
        linha.setAlignment(Pos.CENTER_LEFT);
        return linha;
    }
}
