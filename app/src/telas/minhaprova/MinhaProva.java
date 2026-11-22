package telas.minhaprova;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import modelos.Prova;
import servicos.ProvaServico;
import telas.componentes.Janela;

// Área do aluno: prova já realizada, com as respostas enviadas e, após a correção, o resultado (PB06 e PB11)
public final class MinhaProva {
    private MinhaProva() { }

    public static void abrir(Stage stage, Prova prova, ProvaServico provaServico) throws Exception {
        FXMLLoader loader = new FXMLLoader(MinhaProva.class.getResource("/telas/minhaprova/minha-prova.fxml"));
        MinhaProvaController controller = new MinhaProvaController(prova, provaServico);
        loader.setController(controller);
        Parent raiz = loader.load();
        controller.carregar();
        Janela.exibir(stage, raiz, "Minha prova — " + prova.getTitulo(), 1050, 760, 760, 560);
    }
}
