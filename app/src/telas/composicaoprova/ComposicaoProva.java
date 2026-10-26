package telas.composicaoprova;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import modelos.Prova;
import servicos.ProvaServico;
import servicos.QuestaoServico;
import telas.componentes.Janela;

public final class ComposicaoProva {
    private ComposicaoProva() { }

    public static void abrir(Stage stage, Prova prova, ProvaServico provaServico, QuestaoServico questaoServico) throws Exception {
        FXMLLoader loader = new FXMLLoader(ComposicaoProva.class.getResource("/telas/composicaoprova/composicao-prova.fxml"));
        ComposicaoProvaController controller = new ComposicaoProvaController(prova, provaServico, questaoServico);
        loader.setController(controller);
        Parent raiz = loader.load();
        controller.carregar();
        Janela.exibir(stage, raiz, "Questões da prova " + prova.getCodigo(), 900, 640, 650, 460);
    }
}
