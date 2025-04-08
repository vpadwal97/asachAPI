package com.example.chat.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.chat.model.ChatMessage;
import com.example.chat.repository.ChatMessageRepository;

@Service
public class ChatService {

    private final ChatMessageRepository repo;

    public ChatService(ChatMessageRepository repo) {
        this.repo = repo;
    }

    public ChatMessage saveMessage(ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        return repo.save(message);
    }

    public List<ChatMessage> getAllMessages() {
        return repo.findAll();
    }
}
