package app.types;

import java.sql.Date;

public class Message {
    public int id;
    public int senderId;
    public String text;
    public Date created;
    public String senderName; // INNER JOIN
}
