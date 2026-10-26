package telas.prova;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import servicos.ProvaServico;
import servicos.QuestaoServico;
import telas.componentes.Janela;

public class Prova extends Application {
    private static ProvaServico provaServico;
    private static QuestaoServico questaoServico;

    public static void setProvaServico(ProvaServico servico) { provaServico = servico; }
    public static void setQuestaoServico(QuestaoServico servico) { questaoServico = servico; }
    public static QuestaoServico getQuestaoServico() { return questaoServico; }

    public static void abrir(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Prova.class.getResource("/telas/prova/prova.fxml"));
        loader.setController(new ProvaController(provaServico));
        Parent raiz = loader.load();
        Janela.exibir(stage, raiz, "Provas", 1280, 800, 1000, 600);
    }

    public static void abrirFormulario(Stage stage, modelos.Prova prova) throws Exception {
        FXMLLoader loader = new FXMLLoader(Prova.class.getResource("/telas/prova/prova-formulario.fxml"));
        ProvaFormularioController controller = new ProvaFormularioController(provaServico);
        loader.setController(controller);
        Parent raiz = loader.load();
        controller.definirProva(prova);
        Janela.exibir(stage, raiz, prova == null ? "Nova prova" : "Editar prova", 720, 660, 560, 500);
    }

    @Override
    public void start(Stage stage) throws Exception { abrir(stage); }
}
