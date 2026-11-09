package telas.prova;

import auxiliares.Sessao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import modelos.Prova;
import servicos.ProvaServico;
import javafx.scene.control.ComboBox;
import javafx.collections.FXCollections;

public class ProvaFormularioController {
    @FXML
    private Label tituloTela;
    @FXML
    private Label codigoLabel;
    @FXML
    private TextField tituloCaixaTexto;
    @FXML
    private TextField materiaCaixaTexto;
    @FXML
    private DatePicker dataPicker;
    @FXML
    private ComboBox<String> horaInicioComboBox;
    @FXML
    private ComboBox<String> horaTerminoComboBox;
    @FXML
    private TextField cursoCaixaTexto;
    @FXML
    private TextField bimestreCaixaTexto;

    private final ProvaServico provaServico;
    private String codigo;

    private javafx.collections.ObservableList<String> horarios = FXCollections.observableArrayList(
        // Manhã
        "07:10", "07:55", "08:40", "09:40", "10:25", "11:10",
        // Noite 
        "18:45", "19:35", "20:25", "21:15", "21:25", "22:15",
        "23:05"
    );

    public ProvaFormularioController(ProvaServico provaServico) {
        this.provaServico = provaServico;
    }

    @FXML
    private void initialize() {
        if (horaInicioComboBox != null) {
            horaInicioComboBox.setItems(horarios);
        }
        if (horaTerminoComboBox != null) {
            horaTerminoComboBox.setItems(horarios);
        }
        if (dataPicker != null) {
            java.time.LocalDate hoje = java.time.LocalDate.now();
            int semestreAtual = hoje.getMonthValue() <= 6 ? 1 : 2;
            int anoAtual = hoje.getYear();
            dataPicker.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(java.time.LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        return;
                    }
                    boolean ehPassado = item.isBefore(hoje);
                    boolean ehFimDeSemana = item.getDayOfWeek() == java.time.DayOfWeek.SATURDAY
                            || item.getDayOfWeek() == java.time.DayOfWeek.SUNDAY;
                    int semItem = item.getMonthValue() <= 6 ? 1 : 2;
                    boolean outroSemestre = item.getYear() != anoAtual || semItem != semestreAtual;
                    if (ehPassado || ehFimDeSemana || outroSemestre) {
                        setDisable(true);
                        setStyle("-fx-opacity: 0.4;");
                    }
                }
            });
        }
    }

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
        if (prova.getDataHora() != null) {
            horaInicioComboBox.setValue(prova.getDataHora().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
            horaTerminoComboBox.setValue(prova.getDataHoraFim() != null ? prova.getDataHoraFim().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")) : null);
        } else {
            horaInicioComboBox.setValue(null);
            horaTerminoComboBox.setValue(null);
        }
        cursoCaixaTexto.setText(prova.getCurso());
        bimestreCaixaTexto.setText(String.valueOf(prova.getBimestre()));
    }

    @FXML
    private void confirmar() {
        try {
            int bimestre;
            try {
                bimestre = Integer.parseInt(bimestreCaixaTexto.getText().trim());
            } catch (NumberFormatException e) {
                mostrar(Alert.AlertType.WARNING, "Informe um bimestre válido entre 1 e 4.");
                return;
            }

            String registroProfessor = Sessao.getUsuarioLogado().getRegistro();
            java.time.LocalDate data = dataPicker.getValue();

            String textoHoraInicio = (horaInicioComboBox != null && horaInicioComboBox.getEditor().getText() != null)
                    ? horaInicioComboBox.getEditor().getText().trim() : "";
            String textoHoraFim = (horaTerminoComboBox != null && horaTerminoComboBox.getEditor().getText() != null)
                    ? horaTerminoComboBox.getEditor().getText().trim() : "";

            java.time.LocalDateTime dataHoraInicio = null;
            java.time.LocalDateTime dataHoraFim = null;

            if (data != null) {
                if (!textoHoraInicio.isEmpty()) {
                    if (!textoHoraInicio.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {
                        mostrar(Alert.AlertType.WARNING, "Horário de início inválido. Use o formato HH:mm (exemplo: 07:10 ou 18:45).");
                        return;
                    }
                    java.time.LocalTime horarioInicio = java.time.LocalTime.parse(
                            textoHoraInicio.length() == 4 ? "0" + textoHoraInicio : textoHoraInicio,
                            java.time.format.DateTimeFormatter.ofPattern("HH:mm")
                    );
                    dataHoraInicio = java.time.LocalDateTime.of(data, horarioInicio);
                } else {
                    dataHoraInicio = data.atStartOfDay();
                }

                if (!textoHoraFim.isEmpty()) {
                    if (!textoHoraFim.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {
                        mostrar(Alert.AlertType.WARNING, "Horário de término inválido. Use o formato HH:mm (exemplo: 08:40 ou 20:25).");
                        return;
                    }
                    java.time.LocalTime horarioFim = java.time.LocalTime.parse(
                            textoHoraFim.length() == 4 ? "0" + textoHoraFim : textoHoraFim,
                            java.time.format.DateTimeFormatter.ofPattern("HH:mm")
                    );
                    dataHoraFim = java.time.LocalDateTime.of(data, horarioFim);

                    if (dataHoraInicio != null && !dataHoraFim.isAfter(dataHoraInicio)) {
                        mostrar(Alert.AlertType.WARNING, "O horário de término deve ser posterior ao horário de início.");
                        return;
                    }
                }
            } else if (!textoHoraInicio.isEmpty() || !textoHoraFim.isEmpty()) {
                mostrar(Alert.AlertType.WARNING, "Para definir os horários, você também deve selecionar a data da prova.");
                return;
            }

            Prova prova = new Prova(codigo, tituloCaixaTexto.getText().trim(), materiaCaixaTexto.getText().trim(),
                    dataHoraInicio, dataHoraFim, cursoCaixaTexto.getText().trim(), bimestre, registroProfessor);

            if (codigo != null && provaServico.aplicada(codigo)) {
                mostrar(Alert.AlertType.WARNING, "A prova já foi aplicada a alunos e não pode mais ser editada.");
                return;
            }

            provaServico.salvarProva(prova);
            info("Prova salva com sucesso. Código: " + prova.getCodigo());
            voltarParaProvas();
        } catch (IllegalArgumentException e) {
            mostrar(Alert.AlertType.WARNING, e.getMessage());
        } catch (Exception e) {
            System.err.println("[ERRO] Falha ao salvar prova: " + e.getMessage());
            e.printStackTrace();
            mostrar(Alert.AlertType.ERROR, "Não foi possível salvar a prova. Verifique as informações preenchidas.");
        }
    }

    @FXML
    private void voltar(ActionEvent event) {
        voltarParaProvas();
    }

    private void voltarParaProvas() {
        try {
            telas.prova.Prova.abrir((Stage) tituloTela.getScene().getWindow());
        } catch (Exception e) {
            mostrar(Alert.AlertType.ERROR, "Não foi possível voltar à lista de provas.");
        }
    }

    private void info(String mensagem) {
        mostrar(Alert.AlertType.INFORMATION, mensagem);
    }

    private void mostrar(Alert.AlertType tipo, String mensagem) {
        Alert alerta = new Alert(tipo, mensagem);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
