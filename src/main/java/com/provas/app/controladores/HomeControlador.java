package com.provas.app.controladores;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

//Controlador da tela Home (ligado ao home.fxml)
public class HomeControlador {
    @FXML
    private Label mensagem;

    @FXML
    public void initialize() {
        mensagem.setText("Bem-vindo ao Sistema Gerenciador de Provas");
    }
}
