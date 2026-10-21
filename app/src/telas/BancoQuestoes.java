package telas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import servicos.QuestaoServico;

public class BancoQuestoes extends Application {
    private static QuestaoServico questaoServico;

    public static void setQuestaoServico(QuestaoServico servico) { questaoServico = servico; }

    public static void abrir(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(BancoQuestoes.class.getResource("/telas/banco-questoes.fxml"));
        loader.setController(new BancoQuestoesController(questaoServico));
        Parent raiz = loader.load();
        stage.setTitle("Banco de questões");
        stage.setScene(new Scene(raiz, 900, 600));
        stage.setMinWidth(650);
        stage.setMinHeight(420);
        stage.show();
    }

    @Override
    public void start(Stage stage) throws Exception { abrir(stage); }
}
