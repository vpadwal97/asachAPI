package com.example.chat.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.chat.model.ChatMessage;
import com.example.chat.service.ChatService;
import com.example.chat.service.OpenAIService;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final OpenAIService openAIService;

    public ChatController(ChatService chatService, OpenAIService openAIService) {
        this.chatService = chatService;
        this.openAIService = openAIService;
    }

    @PostMapping("/send")
    public ChatMessage sendMessage(@RequestBody ChatMessage message) {
        return chatService.saveMessage(message);
    }

    @GetMapping("/messages")
    public List<ChatMessage> getMessages() {
        return chatService.getAllMessages();
    }

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public ChatMessage broadcastMessage(ChatMessage message) {
        chatService.saveMessage(message);
        return message; // Will be sent to React
    }

    @PostMapping
    public ResponseEntity<?> chat(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");

        String reply = openAIService.sendMessage(userMessage);

        return ResponseEntity.ok(Map.of("reply", reply));
    }

}
