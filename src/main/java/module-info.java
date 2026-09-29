module com.provas.app {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.provas.app.controladores to javafx.fxml;
    exports com.provas.app;
    exports com.provas.app.telas to javafx.graphics;
}
