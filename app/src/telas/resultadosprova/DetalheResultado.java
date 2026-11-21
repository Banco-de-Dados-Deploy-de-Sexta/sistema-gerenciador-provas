package telas.resultadosprova;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import modelos.Prova;
import servicos.ProvaServico;
import storage.UsuarioStorage;
import telas.componentes.Janela;

public final class DetalheResultado {
    private DetalheResultado() { }

    public static void abrir(Stage stage, Prova prova, String registroAluno, String nomeAluno,
            ProvaServico provaServico, UsuarioStorage usuarioStorage) throws Exception {
        FXMLLoader loader = new FXMLLoader(DetalheResultado.class
                .getResource("/telas/resultadosprova/detalhe-resultado.fxml"));
        DetalheResultadoController controller = new DetalheResultadoController(
                prova, registroAluno, nomeAluno, provaServico, usuarioStorage);
        loader.setController(controller);
        Parent raiz = loader.load();
        controller.carregar();
        Janela.exibir(stage, raiz, "Desempenho — " + nomeAluno, 1050, 760, 760, 560);
    }
}
