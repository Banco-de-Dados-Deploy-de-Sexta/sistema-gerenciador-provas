package telas.resultadosprova;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import modelos.Prova;
import servicos.ProvaServico;
import storage.UsuarioStorage;
import telas.componentes.Janela;

public final class ResultadosProva {
    private ResultadosProva() { }

    public static void abrir(Stage stage, Prova prova, ProvaServico provaServico,
            UsuarioStorage usuarioStorage) throws Exception {
        FXMLLoader loader = new FXMLLoader(ResultadosProva.class
                .getResource("/telas/resultadosprova/resultados-prova.fxml"));
        ResultadosProvaController controller = new ResultadosProvaController(prova, provaServico, usuarioStorage);
        loader.setController(controller);
        Parent raiz = loader.load();
        controller.carregar();
        Janela.exibir(stage, raiz, "Resultados — " + prova.getTitulo(), 1050, 760, 760, 560);
    }
}
