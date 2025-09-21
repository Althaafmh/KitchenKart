package com.PROJECT.kitchenkart.Models;

import java.util.Date;
import java.util.List;

/**
 * Data model for a chat conversation.
 * This class represents a document in the 'chats' collection.
 * @noinspection ALL
 */
public class Chat {
    private String chatId;
    private List<String> users; // The two user IDs in the chat
    private String lastMessage;
    private Date timestamp;

    public Chat() {
        // Default constructor required for Firebase Firestore
    }

    public Chat(String chatId, List<String> users, String lastMessage, Date timestamp) {
        this.chatId = chatId;
        this.users = users;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }

    // Getters and setters for all fields
    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Helper method to get the ID of the other user in the chat.
     * @param currentUserId The ID of the current logged-in user.
     * @return The ID of the other user, or null if not found.
     */
    public String getOtherUserId(String currentUserId) {
        if (users != null && users.size() == 2) {
            if (users.get(0).equals(currentUserId)) {
                return users.get(1);
            } else {
                return users.get(0);
            }
        }
        return null;
    }
}
