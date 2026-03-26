package com.example.nexio.controller;

import com.example.nexio.dto.*;
import com.example.nexio.model.User;
import com.example.nexio.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register/send-otp")
    public ResponseEntity<?> sendRegistrationOtp(@RequestBody OtpRequest request) {
        try {
            authService.sendRegistrationOtp(request.getEmail());
            return ResponseEntity.ok("OTP sent to " + request.getEmail());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/forgot-password/send-otp")
    public ResponseEntity<?> sendForgotPasswordOtp(@RequestBody OtpRequest request) {
        try {
            authService.sendForgotPasswordOtp(request.getEmail());
            return ResponseEntity.ok("Password reset OTP sent to " + request.getEmail());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            return ResponseEntity.ok(authService.register(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            authService.resetPassword(request);
            return ResponseEntity.ok("Password reset successfully. You can now login.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
        try {
            User user = authService.login(request);
            session.setAttribute("user", user);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logged out");
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        return ResponseEntity.ok(user);
    }
}
