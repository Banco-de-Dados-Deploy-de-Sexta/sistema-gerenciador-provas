package telas.questao;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import servicos.QuestaoServico;
import telas.componentes.Janela;

public class Questao extends Application {
    private static QuestaoServico questaoServico;

    public static void setQuestaoServico(QuestaoServico servico) { questaoServico = servico; }

    public static void abrir(Stage stage, modelos.Questao questao) throws Exception {
        FXMLLoader loader = new FXMLLoader(Questao.class.getResource("/telas/questao/questao.fxml"));
        QuestaoController controller = new QuestaoController(questaoServico);
        loader.setController(controller);
        Parent raiz = loader.load();
        controller.definirQuestao(questao);
        Janela.exibir(stage, raiz, questao == null ? "Nova questão" : "Editar questão", 1280, 800, 1000, 600);
    }

    @Override
    public void start(Stage stage) throws Exception { abrir(stage, null); }
}
