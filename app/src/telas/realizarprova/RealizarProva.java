package telas.realizarprova;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import modelos.Prova;
import servicos.ProvaServico;
import telas.componentes.Janela;

public final class RealizarProva {

    private RealizarProva() {}

    public static void abrir(Stage stage, Prova prova, ProvaServico provaServico) throws Exception {
        FXMLLoader loader = new FXMLLoader(RealizarProva.class.getResource("/telas/realizarprova/realizar-prova.fxml"));
        RealizarProvaController controller = new RealizarProvaController(prova, provaServico);
        loader.setController(controller);
        Parent raiz = loader.load();
        controller.carregar();
        Janela.exibir(stage, raiz, "Realizar Prova — " + prova.getTitulo(), 1100, 750, 900, 600);
    }
}
