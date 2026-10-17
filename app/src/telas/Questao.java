package telas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import servicos.QuestaoServico;

public class Questao extends Application {

    private static QuestaoServico questaoServico;

    public static void setQuestaoServico(QuestaoServico servico) {
        questaoServico = servico;
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/telas/questao.fxml"));
        loader.setControllerFactory(tipo -> new QuestaoController(questaoServico));

        stage.setTitle("Cadastro de questão");
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }
}