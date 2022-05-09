package app.client.views;

import app.client.db.MyConnection;
import app.types.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.xml.transform.Result;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ManageUsers {
    private Stage window;

    public static Scene scene;

    private TableView tableView;

    public ManageUsers(Stage window) throws SQLException {
        this.window = window;

        Text headerText = new Text();
        headerText.setText("Manage Users");
        headerText.setFont(Font.font(null, FontWeight.MEDIUM, 16));

        Button deleteButton = new Button("Delete User");
        deleteButton.setOnAction(e -> {
            try {
                this.handleDeleteUser();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        HBox hBox = new HBox(10, headerText, deleteButton);
        hBox.setPadding(new Insets(0, 10, 0, 10));

        tableView = new TableView();
        tableView.setPlaceholder(new Label("Users table is empty"));

        TableColumn<User, String> idCol = new TableColumn<>("id");
        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        TableColumn<User, String> roleCol = new TableColumn<>("Role");

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        tableView.getColumns().addAll(idCol, nameCol, emailCol, roleCol);

        this.getUsers();

        VBox vBox = new VBox(10, hBox, tableView);
        vBox.setPadding(new Insets(10, 0, 0, 0));
        vBox.setAlignment(Pos.CENTER);
        scene = new Scene(vBox, 400, 300);
    }

    public void handleDeleteUser() throws SQLException {
        // todo
        User user = (User) tableView.getSelectionModel().getSelectedItem();
        if(user == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Select a user first!");
            alert.show();
            return;
        }
        PreparedStatement ps = MyConnection.con.prepareStatement(
                "delete from users where id = " + user.getId()
        );
        ps.executeUpdate();

        this.getUsers();
    }

    public void getUsers() throws SQLException {
        tableView.refresh();
        tableView.getItems().clear();

        // Get users from the db and update tableView
        Statement s = MyConnection.con.createStatement();
        ResultSet rs = s.executeQuery("select * from users");

        while(rs.next()) {
            tableView.getItems().add(new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("role")
            ));
        }
    }
}
