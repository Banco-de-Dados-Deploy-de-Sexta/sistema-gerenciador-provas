package telas.login;

import auxiliares.Sessao;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelos.Usuario;
import storage.UsuarioStorage;
import telas.prova.Prova;
import telas.aluno.Aluno;

public class LoginController {

    @FXML
    private Label lbl_registro;
    @FXML
    private TextField txt_registro;
    @FXML
    private Label lbl_erro;
    @FXML
    private Button btn_entrar;
    @FXML
    private Button btn_sair;

    private final UsuarioStorage usuarioStorage;

    public LoginController(UsuarioStorage usuarioStorage) {
        this.usuarioStorage = usuarioStorage;
    }

    @FXML
    private void initialize() {
        // Ao digitar de novo, some a mensagem de erro anterior
        txt_registro.textProperty().addListener((obs, antes, agora) -> limparErro());
        // Ao abrir, nenhum controle começa focado: o foco fica no fundo da tela
        Platform.runLater(() -> txt_registro.getScene().getRoot().requestFocus());
    }

    @FXML
    void sair(ActionEvent event) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, "Deseja realmente sair do sistema?",
                ButtonType.CANCEL, ButtonType.OK);
        alerta.setHeaderText(null);
        alerta.setTitle("Sair");
        if (alerta.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            System.exit(0);
        }
    }

    @FXML
    void entrar(ActionEvent event) {
        String registro = txt_registro.getText() != null ? txt_registro.getText().trim() : "";
        if (registro.isEmpty()) {
            mostrarErro("Informe o registro.");
            return;
        }
        btn_entrar.setDisable(true);
        btn_entrar.setText("Entrando...");
        try {
            Usuario usuario = usuarioStorage.buscarPorRegistro(registro);
            if (usuario == null) {
                mostrarErro("Registro não encontrado.");
                return;
            }
            Stage stage = (Stage) txt_registro.getScene().getWindow();
            if (usuario.isProfessor()) {
                Sessao.iniciar(usuario);
                Prova.abrir(stage);
            } else if (usuario.isAluno()) {
                Sessao.iniciar(usuario);
                Aluno.abrir(stage);
            } else {
                mostrarErro("Tipo de usuário desconhecido: " + usuario.getTipoUsuario() + ".");
            }
        } catch (Exception e) {
            System.err.println("Erro ao autenticar: " + e.getMessage());
            mostrarErro("Erro ao conectar ao banco de dados.");
        } finally {
            btn_entrar.setDisable(false);
            btn_entrar.setText("Entrar");
        }
    }

    private void mostrarErro(String mensagem) {
        lbl_erro.setText(mensagem);
        lbl_erro.setVisible(true);
        lbl_erro.setManaged(true);
        if (!txt_registro.getStyleClass().contains("invalido")) txt_registro.getStyleClass().add("invalido");
        txt_registro.requestFocus();
        txt_registro.selectAll();
    }

    private void limparErro() {
        lbl_erro.setVisible(false);
        lbl_erro.setManaged(false);
        txt_registro.getStyleClass().remove("invalido");
    }
}
