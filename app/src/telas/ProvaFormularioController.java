package telas;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelos.Prova;
import servicos.ProvaServico;

public class ProvaFormularioController {
    @FXML private Label tituloTela;
    @FXML private Label codigoLabel;
    @FXML private TextField tituloCaixaTexto;
    @FXML private TextField materiaCaixaTexto;
    @FXML private DatePicker dataPicker;
    @FXML private TextField cursoCaixaTexto;
    @FXML private TextField bimestreCaixaTexto;

    private final ProvaServico provaServico;
    private String codigo;

    public ProvaFormularioController(ProvaServico provaServico) { this.provaServico = provaServico; }

    public void definirProva(Prova prova) {
        if (prova == null) {
            tituloTela.setText("Nova prova");
            codigoLabel.setText("O código será gerado ao salvar.");
            return;
        }
        codigo = prova.getCodigo();
        tituloTela.setText("Editar prova");
        codigoLabel.setText("Código: " + codigo);
        tituloCaixaTexto.setText(prova.getTitulo());
        materiaCaixaTexto.setText(prova.getMateria());
        dataPicker.setValue(prova.getData());
        cursoCaixaTexto.setText(prova.getCurso());
        bimestreCaixaTexto.setText(String.valueOf(prova.getBimestre()));
    }

    @FXML
    private void confirmar() {
        try {
            int bimestre = Integer.parseInt(bimestreCaixaTexto.getText().trim());
            Prova prova = new Prova(codigo, tituloCaixaTexto.getText().trim(), materiaCaixaTexto.getText().trim(),
                    dataPicker.getValue(), cursoCaixaTexto.getText().trim(), bimestre);
            provaServico.salvarProva(prova);
            info("Prova salva com sucesso. Código: " + prova.getCodigo());
            voltarParaProvas();
        } catch (NumberFormatException e) {
            mostrar(Alert.AlertType.WARNING, "Informe um bimestre entre 1 e 4.");
        } catch (IllegalArgumentException e) {
            mostrar(Alert.AlertType.WARNING, e.getMessage());
        } catch (RuntimeException e) {
            mostrar(Alert.AlertType.ERROR, "Não foi possível salvar a prova.\n" + e.getMessage());
        }
    }

    @FXML private void voltar(ActionEvent event) { voltarParaProvas(); }

    private void voltarParaProvas() {
        try { telas.Prova.abrir((Stage) tituloTela.getScene().getWindow()); }
        catch (Exception e) { mostrar(Alert.AlertType.ERROR, "Não foi possível voltar à lista de provas."); }
    }

    private void info(String mensagem) { mostrar(Alert.AlertType.INFORMATION, mensagem); }
    private void mostrar(Alert.AlertType tipo, String mensagem) {
        Alert alerta = new Alert(tipo, mensagem);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
