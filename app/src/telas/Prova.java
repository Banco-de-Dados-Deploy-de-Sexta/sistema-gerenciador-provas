package telas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import servicos.ProvaServico;

public class Prova extends Application {
    private static ProvaServico provaServico;

    public static void setProvaServico(ProvaServico servico){
        provaServico = servico;
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/telas/prova.fxml"));
        loader.setControllerFactory(tipo -> new ProvaController(provaServico));

        stage.setTitle("Cadastro de prova");
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }
}
