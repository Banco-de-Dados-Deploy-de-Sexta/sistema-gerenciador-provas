package telas;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import modelos.Prova;
import servicos.ProvaServico;

public final class CorrigirProva {
    private CorrigirProva() { }

    public static void abrir(Stage stage, Prova prova, ProvaServico provaServico) throws Exception {
        FXMLLoader loader = new FXMLLoader(CorrigirProva.class.getResource("/telas/corrigir-prova.fxml"));
        CorrigirProvaController controller = new CorrigirProvaController(prova, provaServico);
        loader.setController(controller);
        Parent raiz = loader.load();
        controller.carregar();
        stage.setTitle("Correção — " + prova.getTitulo());
        stage.setScene(new Scene(raiz, 850, 640));
        stage.setMinWidth(620);
        stage.setMinHeight(440);
        stage.show();
    }
}
