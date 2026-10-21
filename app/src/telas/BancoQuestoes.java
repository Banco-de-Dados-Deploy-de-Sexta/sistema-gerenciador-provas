package telas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BancoQuestoes extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/telas/banco-questoes.fxml"));
        stage.setTitle("Banco de questões");
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }
}
