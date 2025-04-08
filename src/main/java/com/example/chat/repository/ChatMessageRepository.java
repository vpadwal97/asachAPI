package com.example.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.chat.model.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}
