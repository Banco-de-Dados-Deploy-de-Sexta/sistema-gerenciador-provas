package telas.componentes;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

// Logo do sistema (logo.png, 512x512 com fundo transparente): folha de prova com questões
// e uma segunda folha azul atrás. Uso no FXML: <Logo tamanho="28" />
public class Logo extends ImageView {
    private static final String ARQUIVO = "/telas/componentes/logo.png";

    private final DoubleProperty tamanho = new SimpleDoubleProperty(this, "tamanho", 28);

    public Logo() {
        getStyleClass().add("logo");
        setPreserveRatio(true);
        setSmooth(true);
        tamanho.addListener((obs, antes, agora) -> aplicarTamanho());
        aplicarTamanho();
    }

    private void aplicarTamanho() {
        double lado = tamanho.get();
        // Carrega a imagem já reduzida (com folga para telas de alta densidade),
        // o que deixa o desenho mais nítido do que reduzir 512px direto na exibição
        double carga = Math.min(512, Math.ceil(lado * 2));
        setImage(new Image(Logo.class.getResource(ARQUIVO).toExternalForm(), carga, carga, true, true));
        setFitWidth(lado);
        setFitHeight(lado);
    }

    public double getTamanho() { return tamanho.get(); }
    public void setTamanho(double valor) { tamanho.set(valor); }
    public DoubleProperty tamanhoProperty() { return tamanho; }
}
