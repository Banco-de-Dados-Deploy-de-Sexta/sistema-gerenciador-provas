package telas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import servicos.QuestaoServico;

public class Questao extends Application {
    private static QuestaoServico questaoServico;

    public static void setQuestaoServico(QuestaoServico servico) { questaoServico = servico; }

    public static void abrir(Stage stage, modelos.Questao questao) throws Exception {
        FXMLLoader loader = new FXMLLoader(Questao.class.getResource("/telas/questao.fxml"));
        QuestaoController controller = new QuestaoController(questaoServico);
        loader.setController(controller);
        Parent raiz = loader.load();
        controller.definirQuestao(questao);
        stage.setTitle(questao == null ? "Nova questão" : "Editar questão");
        stage.setScene(new Scene(raiz, 760, 600));
        stage.setMinWidth(560);
        stage.setMinHeight(440);
        stage.show();
    }

    @Override
    public void start(Stage stage) throws Exception { abrir(stage, null); }
}
