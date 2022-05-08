package app.client.views;

import app.client.db.MyConnection;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Register {
    private Stage window;
    public static Scene scene;
    public GridPane root = new GridPane();
    private TextField nameTextField = new TextField();
    private TextField usernameTextField = new TextField();
    private TextField emailTextField = new TextField();
    private PasswordField passwordField = new PasswordField();
    private Button registerButton = new Button("Register");

    public Register(Stage primaryStage) {
        this.window = primaryStage;

        root.setHgap(10);
        root.setVgap(10);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(15));

        Label nameLabel = new Label("Full Name:");
        root.add(nameLabel, 0, 0, 1, 1);
        root.add(nameTextField, 1, 0, 2, 1);

        Label emailLabel = new Label("Email Address:");
        root.add(emailLabel, 0, 1, 1, 1);
        root.add(emailTextField, 1, 1, 2, 1);

        Label passwordLabel = new Label("Password:");
        root.add(passwordLabel,0, 2, 1, 1);
        root.add(passwordField, 1, 2, 2, 1);

        root.add(registerButton, 0, 3, 1, 1);
        registerButton.setOnAction(e -> { this.handleRegister(); });

        Hyperlink loginLink = new Hyperlink("Log in if you have an account");
        loginLink.setOnAction(e -> {
            primaryStage.setScene(Login.scene);
        });
        root.add(loginLink, 0, 4);

        scene = new Scene(root);
    }

    private void handleRegister() {
        String name = nameTextField.getText();
        String email = emailTextField.getText();
        String password = passwordField.getText();

        String sql = "insert into users (name, email, password, role)" +
                "values (?, ?, ?, 'user');";
        try {
            PreparedStatement ps = MyConnection.con.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            int rowsAffected = ps.executeUpdate();
            System.out.println(rowsAffected);

            if(rowsAffected == 1) {
                nameTextField.clear();
                emailTextField.clear();
                passwordField.clear();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Account successfully created!");
                alert.show();

                window.setScene(Login.scene);
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Email address already exists.");
            alert.show();
            System.out.println(e);
        }
    }
}
