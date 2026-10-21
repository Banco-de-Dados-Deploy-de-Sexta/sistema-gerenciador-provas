package telas;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class HomeController {

    @FXML
    private void abrirQuestao(ActionEvent event) {
        try {
            new Questao().start(palcoDe(event));
        } catch (Exception e) {
            mostrarErro("Não foi possível abrir a tela de questão.", e);
        }
    }

    @FXML
    private void abrirBancoQuestoes(ActionEvent event) {
        try {
            new BancoQuestoes().start(palcoDe(event));
        } catch (Exception e) {
            mostrarErro("Não foi possível abrir o banco de questões.", e);
        }
    }

    @FXML
    private void abrirProva(ActionEvent event) {
        try {
            new Prova().start(palcoDe(event));
        } catch (Exception e) {
            mostrarErro("Não foi possível abrir a tela de prova.", e);
        }
    }

    private Stage palcoDe(ActionEvent event) {
        Node origem = (Node) event.getSource();
        return (Stage) origem.getScene().getWindow();
    }

    private void mostrarErro(String mensagem, Exception e) {
        e.printStackTrace();

        Throwable causa = e;
        while (causa.getCause() != null) {
            causa = causa.getCause();
        }

        Alert alerta = new Alert(AlertType.ERROR);
        alerta.setHeaderText(mensagem);
        alerta.setContentText(causa.toString());
        alerta.showAndWait();
    }
}
