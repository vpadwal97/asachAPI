package com.example.chat.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
    public ResponseEntity<String> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "subdir", required = false) String subDir) {
        try {
            String fullPath = baseUploadDir + File.separator + "images";

            if (subDir != null && !subDir.trim().isEmpty()) {
                fullPath = fullPath + File.separator + subDir;
            }

            File dir = new File(fullPath);
            if (!dir.exists())
                dir.mkdirs();

            Path filePath = Paths.get(fullPath, file.getOriginalFilename());
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String relativePath = filePath.toString().replace(baseUploadDir, "");
            return ResponseEntity.ok(relativePath.replace("\\", "/"));
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
