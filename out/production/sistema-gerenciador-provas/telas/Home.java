package telas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Home extends Application {

    @Override
    public void start(Stage palco) throws Exception {
        Parent raiz = FXMLLoader.load(getClass().getResource("home.fxml"));

        palco.setTitle("Início");
        Scene cena = new Scene(raiz, 760, 520);
        palco.setScene(cena);
        palco.setMinWidth(520);
        palco.setMinHeight(380);
        palco.setResizable(true);
        palco.show();
    }
}
