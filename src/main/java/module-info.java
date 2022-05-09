module app {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    opens app.client to javafx.fxml;
    opens app.types to javafx.base;
    exports app.client;
}