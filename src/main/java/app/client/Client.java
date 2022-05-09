package app.client;

import app.client.db.MyConnection;
import app.client.views.Login;
import app.client.views.Register;
import app.types.User;
import javafx.application.Application;
import javafx.stage.Stage;

// A client sends messages to the server, the server spawns a thread to communicate with the client.
// Each communication with a client is added to an array list so any message sent gets sent to every other client
// by looping through it.

public class Client extends Application {
    public static User currentUser = null;
    public void start(Stage primaryStage) {
        // Connect to the database
        new MyConnection();

        new Login(primaryStage);
        new Register(primaryStage);

        primaryStage.setScene(Login.scene);
        primaryStage.setTitle("Chat Group App");
        primaryStage.show();
    }

    // Run the program.
    public static void main(String[] args) {
        launch(args);
    }
}
