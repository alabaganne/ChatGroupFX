package app.client.views;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class RegisterView {
    private Stage primaryStage;
    public static Scene scene;
    public GridPane root = new GridPane();
    private TextField fullNameTextField = new TextField();
    private TextField usernameTextField = new TextField();
    private TextField emailTextField = new TextField();
    private TextField passwordTextField = new TextField();
    private Button registerButton = new Button("Register");

    public RegisterView(Stage primaryStage) {
        this.primaryStage = primaryStage;

        root.setHgap(10);
        root.setVgap(10);
        root.setAlignment(Pos.CENTER);

        Label fullNameLabel = new Label("Full Name:");
        root.add(fullNameLabel, 0, 0, 1, 1);
        root.add(fullNameTextField, 1, 0, 2, 1);

        Label usernameLabel = new Label("Username:");
        root.add(usernameLabel, 0, 1, 1, 1);
        root.add(usernameTextField, 1, 1, 2, 1);

        Label emailLabel = new Label("Email Address:");
        root.add(emailLabel, 0, 2, 1, 1);
        root.add(emailTextField, 1, 2, 2, 1);

        Label passwordLabel = new Label("Password:");
        root.add(passwordLabel,0, 3, 1, 1);
        root.add(passwordTextField, 1, 3, 2, 1);

        root.add(registerButton, 0, 4, 1, 1);
        registerButton.setOnAction(e -> {
            System.out.println("Register clicked");
            // validate data
            // send request to create user if valid
        });

        Hyperlink loginLink = new Hyperlink("Log in if you have an account");
        loginLink.setOnAction(e -> {
            primaryStage.setScene(LoginView.scene);
        });
        root.add(loginLink, 0, 5);

        scene = new Scene(root, 480, 360);
    }
}
