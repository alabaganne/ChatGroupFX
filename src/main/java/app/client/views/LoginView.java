package app.client.views;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class LoginView {
    private Stage primaryStage;
    public static Scene scene;
    public GridPane root;
    private TextField emailTextField = new TextField();
    private TextField passwordTextField = new TextField();
    private Button loginButton = new Button("Login");

    public LoginView(Stage primaryStage) {
        this.primaryStage = primaryStage;

        root = new GridPane();
        root.setVgap(10);
        root.setHgap(10);
        root.setAlignment(Pos.CENTER);

        Label emailLabel = new Label("Email Address:");
        root.add(emailLabel, 0, 0, 1, 1);
        root.add(emailTextField, 1, 0, 2, 1);

        Label passwordLabel = new Label("Password:");
        root.add(passwordLabel,0, 1, 1, 1);
        root.add(passwordTextField, 1, 1, 2, 1);

        root.add(loginButton, 0, 2, 1, 1);
        loginButton.setOnAction(e -> {
            System.out.println("Login clicked");
            // check credentials
            // navigate to chat room
        });

        Hyperlink registerLink = new Hyperlink("Create an account");
        registerLink.setOnAction(e -> {
            primaryStage.setScene(RegisterView.scene);
        });
        root.add(registerLink, 0, 3);

        scene = new Scene(root, 480, 360);
    }
}
