package telas.corrigirprova;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import modelos.Prova;
import servicos.ProvaServico;
import telas.componentes.Janela;

public final class CorrigirProva {
    private CorrigirProva() { }

    public static void abrir(Stage stage, Prova prova, ProvaServico provaServico) throws Exception {
        FXMLLoader loader = new FXMLLoader(CorrigirProva.class.getResource("/telas/corrigirprova/corrigir-prova.fxml"));
        CorrigirProvaController controller = new CorrigirProvaController(prova, provaServico);
        loader.setController(controller);
        Parent raiz = loader.load();
        controller.carregar();
        Janela.exibir(stage, raiz, "Correção — " + prova.getTitulo(), 850, 640, 620, 440);
    }
}
