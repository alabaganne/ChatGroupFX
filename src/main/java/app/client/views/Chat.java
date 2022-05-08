package app.client.views;

import app.client.Client;
import app.client.db.MyConnection;
import app.types.Message.Message;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

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

            // this.listenForMessage();
            // this.sendMessage();
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
        String sql = "select u.name as sender, m.text as message " +
                "from messages m " +
                "left join users as u on u.id = m.userId " +
                "order by m.created;";
        Statement s = MyConnection.con.createStatement();
        ResultSet rs = s.executeQuery(sql);
        while(rs.next()) {
            Text messageText = new Text();
            String sender = rs.getString("sender");
            String message = rs.getString("message");
            messageText.setText(sender + ": " + message);
            messagesVBox.getChildren().add(messageText);
        }

        VBox vBox = new VBox(hBox, scrollPane);
        vBox.setPadding(new Insets(15));
        scene = new Scene(vBox);
    }


    // Sending a message isn't blocking and can be done without spawning a thread, unlike waiting for a message.
    public void sendMessage() {
        try {
            String messageToSend = messageField.getText();

            bufferedWriter.write(Client.currentUser.name + ": " + messageToSend);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            messageField.clear();
        } catch (IOException e) {
            // Gracefully close everything.
            closeEverything(socket, bufferedReader, bufferedWriter);
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
                    System.out.println(msgFromGroupChat);
                } catch (IOException e) {
                    // Close everything gracefully.
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
        }).start();
    }

    // Helper method to close everything so you don't have to repeat yourself.
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
        // close socket connection
        // clear user data
        // redirect to login page
    }
}
