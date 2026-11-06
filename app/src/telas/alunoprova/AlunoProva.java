package telas.alunoprova;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import modelos.Prova;
import servicos.ProvaServico;
import storage.UsuarioStorage;
import telas.componentes.Janela;


// Tela de aplicação da prova (PB07): o professor marca os alunos que farão a prova
public final class AlunoProva {
    private AlunoProva() { }

    public static void abrir(Stage stage, Prova prova, ProvaServico provaServico, UsuarioStorage usuarioStorage) throws Exception {
        FXMLLoader loader = new FXMLLoader(AlunoProva.class.getResource("/telas/alunoprova/alunoprova.fxml"));
        AlunoProvaController controller = new AlunoProvaController(prova, provaServico, usuarioStorage);
        loader.setController(controller);
        Parent raiz = loader.load();
        controller.carregar();
        Janela.exibir(stage, raiz, "Alunos da prova " + prova.getCodigo(), 900, 640, 650, 460);
    }
}
