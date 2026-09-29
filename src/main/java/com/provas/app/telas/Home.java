package com.provas.app.telas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

//Essa é a primeira tela do sistema
//O objetivo é deixá-la pronta para a entrega da primeira sprint no dia 30/09
public class Home extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Home.class.getResource("home.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);
        stage.setTitle("Sistema Gerenciador de Provas");
        stage.setScene(scene);
        stage.show();
    }
}
