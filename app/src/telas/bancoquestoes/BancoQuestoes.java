package telas.bancoquestoes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import servicos.QuestaoServico;
import telas.componentes.Janela;

public class BancoQuestoes extends Application {
    private static QuestaoServico questaoServico;

    public static void setQuestaoServico(QuestaoServico servico) { questaoServico = servico; }

    public static void abrir(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(BancoQuestoes.class.getResource("/telas/bancoquestoes/banco-questoes.fxml"));
        loader.setController(new BancoQuestoesController(questaoServico));
        Parent raiz = loader.load();
        Janela.exibir(stage, raiz, "Banco de questões", 1280, 800, 1000, 600);
    }

    @Override
    public void start(Stage stage) throws Exception { abrir(stage); }
}
