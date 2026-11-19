package telas.corrigirdissertativas;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import modelos.Prova;
import servicos.ProvaServico;
import telas.componentes.Janela;

public final class CorrigirDissertativas {
    private CorrigirDissertativas() { }

    public static void abrir(Stage stage, Prova prova, ProvaServico provaServico) throws Exception {
        FXMLLoader loader = new FXMLLoader(CorrigirDissertativas.class
                .getResource("/telas/corrigirdissertativas/corrigir-dissertativas.fxml"));
        CorrigirDissertativasController controller = new CorrigirDissertativasController(prova, provaServico);
        loader.setController(controller);
        Parent raiz = loader.load();
        controller.carregar();
        Janela.exibir(stage, raiz, "Corrigir dissertativas — " + prova.getTitulo(), 900, 700, 680, 520);
    }
}
