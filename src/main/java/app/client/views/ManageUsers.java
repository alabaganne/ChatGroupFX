package app.client.views;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ManageUsers {
    private Stage window;

    public static Scene scene;

    public ManageUsers(Stage window) {
        this.window = window;

        Text headerText = new Text();
        headerText.setText("Manage Users");
        headerText.setFont(Font.font(null, FontWeight.BOLD, 12));

        VBox vBox = new VBox(10, headerText);
        vBox.setPadding(new Insets(10));
        scene = new Scene(vBox);
    }
}
