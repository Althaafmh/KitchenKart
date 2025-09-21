package com.PROJECT.kitchenkart.Models;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

/**
 * Data model for a chat message.
 * @noinspection ALL
 */
public class Message {
    private String senderId;
    private String receiverId;
    private String text;
    @ServerTimestamp
    private Date timestamp;

    public Message() {}

    public Message(String senderId, String receiverId, String text, Date timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}
