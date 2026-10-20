package telas;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import servicos.ProvaServico;

import java.time.LocalDate;

public class ProvaController {

    @FXML
    private TextField tituloCaixaTexto;

    @FXML
    private TextField materiaCaixaTexto;

    @FXML
    private DatePicker dataPicker;

    @FXML
    private TextField cursoCaixaTexto;

    @FXML
    private TextField bimestreCaixaTexto;

    private final ProvaServico provaServico;

    public ProvaController(ProvaServico provaServico) {
        this.provaServico = provaServico;
    }

    @FXML
    private void confirmar() {

        String titulo = tituloCaixaTexto.getText().trim();
        String materia = materiaCaixaTexto.getText().trim();
        LocalDate data = LocalDate.now();
        String curso = cursoCaixaTexto.getText().trim();
        String bimestreTexto = bimestreCaixaTexto.getText().trim();

        if (titulo.isEmpty()
                || materia.isEmpty()
                || curso.isEmpty()
                || bimestreTexto.isEmpty()) {

            mostrar(
                    Alert.AlertType.WARNING,
                    "Preencha todos os campos.");
            return;
        }

        try {

            int bimestre = Integer.parseInt(bimestreTexto);
            if (bimestre < 1 || bimestre > 4) {
                mostrar(
                        Alert.AlertType.WARNING,
                        "Preencha o bimestre corretamente (1-4).");
                return;
            }

            var prova = provaServico.cadastrarProva(
                    titulo,
                    materia,
                    data,
                    curso,
                    bimestre);

            mostrar(
                    Alert.AlertType.INFORMATION,
                    "Prova cadastrada:\n" + prova.toString());

            limpar();

        } catch (NumberFormatException e) {

            mostrar(
                    Alert.AlertType.ERROR,
                    "Bimestre inválido.");
        }
    }

    private void limpar() {
        tituloCaixaTexto.clear();
        materiaCaixaTexto.clear();
        cursoCaixaTexto.clear();
        bimestreCaixaTexto.clear();
        dataPicker.setValue(null);
    }

    private void mostrar(Alert.AlertType tipo, String mensagem) {
        Alert alerta = new Alert(tipo, mensagem);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}