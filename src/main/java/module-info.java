module com.cera {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires java.sql;

    opens com.cera to javafx.fxml;
    opens com.cera.controller to javafx.fxml;

    exports com.cera;
    exports com.cera.controller;
}
