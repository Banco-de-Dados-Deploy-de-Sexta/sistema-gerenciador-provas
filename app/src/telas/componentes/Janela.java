package telas.componentes;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Troca a tela exibida na janela. Reaproveita a Scene já existente para que a janela mantenha
// o tamanho atual (inclusive maximizada) ao navegar; o tamanho informado só vale na primeira tela
public final class Janela {
    private Janela() { }

    public static void exibir(Stage stage, Parent raiz, String titulo, double largura, double altura, double larguraMinima, double alturaMinima) {
        stage.setTitle(titulo);
        if (stage.getScene() == null) stage.setScene(new Scene(raiz, largura, altura));
        else stage.getScene().setRoot(raiz);
        stage.setMinWidth(larguraMinima);
        stage.setMinHeight(alturaMinima);
        stage.show();
    }
}
