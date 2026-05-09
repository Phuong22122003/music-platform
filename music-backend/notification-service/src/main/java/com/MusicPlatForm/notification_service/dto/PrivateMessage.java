package com.MusicPlatForm.notification_service.dto;

public class PrivateMessage {
    private String recipient;
    private String content;
    public String getRecipient() {
        return recipient;
    }
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public PrivateMessage() {
    }

}
