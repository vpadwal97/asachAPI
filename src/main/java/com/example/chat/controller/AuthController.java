// === AuthController.java ===
package com.example.chat.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.chat.model.LoginRequest;
// import com.example.chat.service.JWTService;
import com.example.chat.service.UserService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final UserService userService;
    // private final JWTService jwtService;

    // public AuthController(UserService userService, JWTService jwtService) {
    public AuthController(UserService userService) {
        this.userService = userService;
        // this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (userService.validateCredentials(request.getUsername(), request.getPassword())) {
            // String token = jwtService.generateToken(request.getUsername());
            // return ResponseEntity.ok().body("Bearer " + token);
            return ResponseEntity.ok().body("Login Success");
        }
        return ResponseEntity.status(401).body("Invalid username or password");
    }
}
