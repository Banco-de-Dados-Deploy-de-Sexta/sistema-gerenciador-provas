package telas;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import modelos.Prova;
import servicos.ProvaServico;
import servicos.QuestaoServico;

public final class ComposicaoProva {
    private ComposicaoProva() { }

    public static void abrir(Stage stage, Prova prova, ProvaServico provaServico, QuestaoServico questaoServico) throws Exception {
        FXMLLoader loader = new FXMLLoader(ComposicaoProva.class.getResource("/telas/composicao-prova.fxml"));
        ComposicaoProvaController controller = new ComposicaoProvaController(prova, provaServico, questaoServico);
        loader.setController(controller);
        Parent raiz = loader.load();
        controller.carregar();
        stage.setTitle("Questões da prova " + prova.getCodigo());
        stage.setScene(new Scene(raiz, 900, 640));
        stage.setMinWidth(650);
        stage.setMinHeight(460);
        stage.show();
    }
}
