package com.example.chat.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OpenAIService {

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = "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"; // 🔐 Replace this with your OpenAI key

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String sendMessage(String userMessage) {
        try {
            // Request body for GPT-3.5-turbo
            Map<String, Object> body = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(
                    Map.of("role", "user", "content", userMessage)
                )
            );

            String jsonBody = objectMapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OPENAI_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + API_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("OpenAI error: " + response.body());
                return "Error from OpenAI: " + response.body();
            }

            JsonNode jsonNode = objectMapper.readTree(response.body());
            return jsonNode
                    .get("choices")
                    .get(0)
                    .get("message")
                    .get("content")
                    .asText();

        } catch (Exception e) {
            e.printStackTrace();
            return "Something went wrong while talking to GPT.";
        }
    }
}
