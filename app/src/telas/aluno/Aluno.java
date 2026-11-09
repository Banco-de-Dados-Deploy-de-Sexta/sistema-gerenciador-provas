package telas.aluno;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import servicos.ProvaServico;
import servicos.QuestaoServico;
import storage.UsuarioStorage;
import telas.componentes.Janela;

public class Aluno extends Application {
    private static ProvaServico provaServico;
    private static QuestaoServico questaoServico;
    private static UsuarioStorage usuarioStorage;

    public static void setProvaServico(ProvaServico servico) { provaServico = servico; }
    public static void setQuestaoServico(QuestaoServico servico) { questaoServico = servico; }
    public static void setUsuarioStorage(UsuarioStorage storage) { usuarioStorage = storage; }
    public static ProvaServico getProvaServico() { return provaServico; }
    public static QuestaoServico getQuestaoServico() { return questaoServico; }
    public static UsuarioStorage getUsuarioStorage() { return usuarioStorage; }

    public static void abrir(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Aluno.class.getResource("/telas/aluno/aluno.fxml"));
        loader.setController(new AlunoController(provaServico, questaoServico));
        Parent raiz = loader.load();
        Janela.exibir(stage, raiz, "Área do Aluno", 1280, 800, 1000, 600);
    }

    @Override
    public void start(Stage stage) throws Exception { abrir(stage); }
}
