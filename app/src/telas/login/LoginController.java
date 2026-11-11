package telas.login;

import auxiliares.Sessao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
    private Button btn_entrar;
    @FXML
    private Button btn_sair;

    private final UsuarioStorage usuarioStorage;

    public LoginController(UsuarioStorage usuarioStorage) {
        this.usuarioStorage = usuarioStorage;
    }

    @FXML
    void sair(ActionEvent event) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, "Deseja realmente sair?");
        alerta.setHeaderText(null);
        alerta.showAndWait().ifPresent(resposta -> {
            if (resposta.getText().equals("OK")) {
                System.exit(0);
            }
        });
    }

    @FXML
    void entrar(ActionEvent event) {
        String registro = txt_registro.getText() != null ? txt_registro.getText().trim() : "";
        if (registro.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "O campo de registro não pode estar vazio.");
            return;
        }
        try {
            Usuario usuario = usuarioStorage.buscarPorRegistro(registro);
            if (usuario == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Registro não encontrado no sistema.");
                return;
            }
            Stage stage = (Stage) txt_registro.getScene().getWindow();
            if (usuario.isProfessor()) {
                // Redireciona para o painel de provas do professor
                Sessao.iniciar(usuario);
                Prova.abrir(stage);
            } else if (usuario.isAluno()) {
                // Redireciona para a tela do aluno (quando criada)
                Sessao.iniciar(usuario);
                Aluno.abrir(stage);
                // mostrarAlerta(Alert.AlertType.INFORMATION, "Bem-vindo(a) " + usuario.getNome() + "! (Área do Aluno)");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Tipo de usuário desconhecido: " + usuario.getTipoUsuario());
            }
        } catch (Exception e) {
            System.err.println("Erro ao autenticar: " + e.getMessage());
            mostrarAlerta(Alert.AlertType.ERROR, "Erro ao autenticar: " + e.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensagem) {
        Alert alerta = new Alert(tipo, mensagem);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
