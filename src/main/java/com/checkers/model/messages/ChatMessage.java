package com.checkers.model.messages;

public class ChatMessage {
    private String username;
    private String message;

    public ChatMessage(){
    }

    public ChatMessage(String username, String message) {
        this.username = username;
        this.message = message;
    }

    public ChatMessage(Message message) {
        this.username=parse(message.getMessage())[0];
        this.message=parse(message.getMessage())[1];
    }

    private String[] parse(String message) {
        return message.replace("{\"username\":\"", "").replace("\",\"message\":","").replace("\"}", "").split("\"");
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "username='" + username + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
