package com.example.chat.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.chat.model.ChatMessage;
import com.example.chat.service.ChatService;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Value("${file.upload-dir}")
    private String baseUploadDir;

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

    @PostMapping("/upload/image")
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("sender") String sender) {

        try {
            LocalDateTime now = LocalDateTime.now();
            String year = String.valueOf(now.getYear());
            String month = String.format("%02d", now.getMonthValue());

            // Create directory: /uploads/images/chat/YYYY/MM
            String subDir = "images/chat/" + year + "/" + month;
            String fullPath = baseUploadDir + File.separator + subDir;

            File dir = new File(fullPath);
            if (!dir.exists())
                dir.mkdirs();

            String originalFilename = file.getOriginalFilename();
            Path filePath = Paths.get(fullPath, originalFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String relativePath = (subDir + "/" + originalFilename).replace("\\", "/");

            // Save message metadata
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setSender(sender);
            chatMessage.setMessage(relativePath);
            chatMessage.setTimestamp(now);
            chatMessage.setType("image");
            // chatMessage.setFilePath(relativePath);

            chatService.saveMessage(chatMessage);

            return ResponseEntity.ok(chatMessage);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload error: " + e.getMessage());
        }
    }

    @PostMapping("")
    public ResponseEntity<String> fallback() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This endpoint is disabled.");
    }

}
