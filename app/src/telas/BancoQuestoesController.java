package telas;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

public class BancoQuestoesController {
    @FXML
    private void abrirQuestao(ActionEvent event) {
        try {
            new Questao().start((Stage) ((Node) event.getSource()).getScene().getWindow());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void voltar(ActionEvent event) {
        try {
            new Home().start((Stage) ((Node) event.getSource()).getScene().getWindow());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
