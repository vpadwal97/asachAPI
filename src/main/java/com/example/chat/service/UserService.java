// === UserService.java ===
package com.example.chat.service;

import java.io.File;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.chat.model.UserCredential;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UserService {
    private final List<UserCredential> users;
    private static final String FILE_PATH = "databaseJsons/login.json";

    public UserService() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        users = objectMapper.readValue(
                new File(FILE_PATH),
                new TypeReference<List<UserCredential>>() {
                });
    }

    public boolean validateCredentials(String username, String password) {
        return users.stream().anyMatch(u -> u.getUsername().equals(username) && u.getPassword().equals(password));
    }
}
