package app.client.views;

import app.client.Client;
import app.client.db.MyConnection;
import app.types.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Login {
    private Stage window;
    public static Scene scene;
    public GridPane root;
    private TextField emailTextField = new TextField();
    private PasswordField passwordField = new PasswordField();
    private Button loginButton = new Button("Login");

    public Login(Stage primaryStage) {
        this.window = primaryStage;

        root = new GridPane();
        root.setVgap(10);
        root.setHgap(10);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(15));

        Label emailLabel = new Label("Email Address:");
        root.add(emailLabel, 0, 0, 1, 1);
        root.add(emailTextField, 1, 0, 2, 1);

        Label passwordLabel = new Label("Password:");
        root.add(passwordLabel,0, 1, 1, 1);
        root.add(passwordField, 1, 1, 2, 1);

        root.add(loginButton, 0, 2, 1, 1);
        loginButton.setOnAction(e -> {
            this.handleLogin();
        });

        Hyperlink registerLink = new Hyperlink("Create an account");
        registerLink.setOnAction(e -> {
            primaryStage.setScene(Register.scene);
        });
        root.add(registerLink, 0, 3);

        scene = new Scene(root);
    }

    private void handleLogin() {
        // get user from the db
        String sql = "select * from users " +
                "where email = '" + emailTextField.getText() + "' " +
                "and password = '" + passwordField.getText() + "' " +
                "limit 1";
        try {
            Statement s = MyConnection.con.createStatement();
            ResultSet rs = s.executeQuery(sql);

            if(!rs.isBeforeFirst()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Wrong credentials!");
                alert.show();
                return;
            }

            rs.next();

            Client.currentUser = new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("role")
            );

            if(Client.currentUser.role.equals("admin")) {
                new ManageUsers(window);
                window.setScene(ManageUsers.scene);
            } else {
                new Chat(window);
                window.setScene(Chat.scene);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
