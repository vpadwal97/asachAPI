package com.example.chat.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sender;

    // We'll use 'message' here for clarity (but you can keep 'message' if preferred)
    private String message;

    private LocalDateTime timestamp;

    public ChatMessage() {
        // Default constructor
    }

    public ChatMessage(String sender, String message, LocalDateTime timestamp) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getmessage() { return message; }
    public void setmessage(String message) { this.message = message; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
