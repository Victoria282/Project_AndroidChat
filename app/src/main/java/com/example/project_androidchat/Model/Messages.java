package com.example.project_androidchat.Model;
import java.util.Date;

public class Messages {
    // id получателя / id отправителя / тело сообщения
    private String receiver;
    private String sender;
    private String message;
    private long date;

    public Messages() {}

    public Messages(String receiver, String sender, String message) {
        this.receiver = receiver;
        this.sender = sender;
        this.message = message;
        date = new Date().getTime();
    }
    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getDate() { return date; }

    public void setDate(long date) { this.date = date; }
}
