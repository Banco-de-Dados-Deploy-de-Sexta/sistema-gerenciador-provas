package telas.login;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import storage.UsuarioStorage;
import telas.componentes.Janela;

public class Login extends Application {
    private static UsuarioStorage usuarioStorage;

    public static void setUsuarioStorage(UsuarioStorage storage) {
        usuarioStorage = storage;
    }

    public static void abrir(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Login.class.getResource("/telas/login/login.fxml"));
        loader.setController(new LoginController(usuarioStorage));
        Parent raiz = loader.load();
        Janela.exibir(stage, raiz, "Login", 720, 660, 560, 500);
    }   

    @Override
    public void start(Stage stage) throws Exception {
        // Implementar a lógica para exibir a tela de login
        abrir(stage);
    }
}