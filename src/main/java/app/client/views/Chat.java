package app.client.views;

import app.client.Client;
import app.client.db.MyConnection;
import app.types.Message;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Chat {
    private Stage window;
    public static Scene scene;
    private TextField messageField;
    private ArrayList<Message> messages;
    private VBox messagesVBox;

    // A client has a socket to connect to the server and a reader and writer to receive and send messages respectively.
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Chat(Stage primaryStage) throws SQLException {
        try {
            this.socket = new Socket("localhost", 1234);
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            bufferedWriter.write(Client.currentUser.name);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            this.listenForMessage();
        } catch (IOException e) {
            System.out.println("Error connecting to the socket");
            // Gracefully close everything.
            closeEverything(socket, bufferedReader, bufferedWriter);
        }

        this.window = primaryStage;

        messageField = new TextField();
        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> { this.sendMessage(); });
        HBox hBox = new HBox(5, messageField, sendButton);
        hBox.setPadding(new Insets(0, 0, 10, 0));
        hBox.setHgrow(messageField, Priority.ALWAYS);

        ScrollPane scrollPane = new ScrollPane();
        messagesVBox = new VBox(5);
        messagesVBox.setPadding(new Insets(10));
        scrollPane.setContent(messagesVBox);

        // Get messages from the database
        String sql = "select u.id as senderId, u.name as senderName, m.text as message " +
                "from messages m " +
                "left join users as u on u.id = m.userId " +
                "order by m.created desc;";
        Statement s = MyConnection.con.createStatement();
        ResultSet rs = s.executeQuery(sql);
        while(rs.next()) {
            appendToMessages(
                    rs.getInt("senderId"),
                    rs.getString("senderName"),
                    rs.getString("message")
            );
        }

        VBox vBox = new VBox(hBox, scrollPane);
        vBox.setPadding(new Insets(15));
        scene = new Scene(vBox, 400, 300);
    }

    public void appendToMessages(int senderId, String senderName, String message) {
        Text messageElement = new Text();

        if(senderId == Client.currentUser.id) {
            senderName = "You";
        }
        messageElement.setText(senderName + ": " + message);
        messagesVBox.getChildren().add(messageElement);
    }

    // Sending a message isn't blocking and can be done without spawning a thread, unlike waiting for a message.
    public void sendMessage() {
        try {
            String messageToSend = messageField.getText();
            if(messageToSend.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("Please type a message first!");
                alert.show();
                return;
            }
            // Store message in the db
            PreparedStatement ps = MyConnection.con.prepareStatement(
                    "insert into messages (userId, text) " +
                            "values (?, ?)"
            );
            ps.setInt(1, Client.currentUser.id);
            ps.setString(2, messageToSend);
            ps.executeUpdate();

            // Broadcast new message to other users
            bufferedWriter.write(Client.currentUser.name + ": " + messageToSend);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            messageField.clear();

            // Display new message
            appendToMessages(Client.currentUser.id, Client.currentUser.name, messageToSend);
        } catch (IOException | SQLException e) {
            // Gracefully close everything.
            closeEverything(socket, bufferedReader, bufferedWriter);
            System.out.println(e);
        }
    }

    // Listening for a message is blocking so need a separate thread for that.
    public void listenForMessage() {
        new Thread(() -> {
            String msgFromGroupChat;
            // While there is still a connection with the server, continue to listen for messages on a separate thread.
            while (socket.isConnected()) {
                try {
                    // Get the messages sent from other users and print it to the console.
                    msgFromGroupChat = bufferedReader.readLine();
                    // Display new message received
                    String finalMsgFromGroupChat = msgFromGroupChat;
                    Platform.runLater(() -> {
                        Text newMessageEl = new Text();
                        newMessageEl.setText(finalMsgFromGroupChat);
                        messagesVBox.getChildren().add(newMessageEl);
                    });
                } catch (IOException e) {
                    // Close everything gracefully.
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
        }).start();
    }

    // Helper method to close everything, so you don't have to repeat yourself.
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        // Note you only need to close the outer wrapper as the underlying streams are closed when you close the wrapper.
        // Note you want to close the outermost wrapper so that everything gets flushed.
        // Note that closing a socket will also close the socket's InputStream and OutputStream.
        // Closing the input stream closes the socket. You need to use shutdownInput() on socket to just close the input stream.
        // Closing the socket will also close the socket's input stream and output stream.
        // Close the socket after closing the streams.
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logout() {
        // todo
    }
}
