package telas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import servicos.ProvaServico;
import servicos.QuestaoServico;

public class Prova extends Application {
    private static ProvaServico provaServico;
    private static QuestaoServico questaoServico;

    public static void setProvaServico(ProvaServico servico) { provaServico = servico; }
    public static void setQuestaoServico(QuestaoServico servico) { questaoServico = servico; }
    public static QuestaoServico getQuestaoServico() { return questaoServico; }

    public static void abrir(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Prova.class.getResource("/telas/prova.fxml"));
        loader.setController(new ProvaController(provaServico));
        Parent raiz = loader.load();
        stage.setTitle("Provas");
        stage.setScene(new Scene(raiz, 1000, 640));
        stage.setMinWidth(700);
        stage.setMinHeight(450);
        stage.show();
    }

    public static void abrirFormulario(Stage stage, modelos.Prova prova) throws Exception {
        FXMLLoader loader = new FXMLLoader(Prova.class.getResource("/telas/prova-formulario.fxml"));
        ProvaFormularioController controller = new ProvaFormularioController(provaServico);
        loader.setController(controller);
        Parent raiz = loader.load();
        controller.definirProva(prova);
        stage.setTitle(prova == null ? "Nova prova" : "Editar prova");
        stage.setScene(new Scene(raiz, 720, 660));
        stage.setMinWidth(560);
        stage.setMinHeight(500);
        stage.show();
    }

    @Override
    public void start(Stage stage) throws Exception { abrir(stage); }
}
