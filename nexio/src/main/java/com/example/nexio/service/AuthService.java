package com.example.nexio.service;

import com.example.nexio.model.User;
import com.example.nexio.repository.UserRepository;
import com.example.nexio.dto.RegisterRequest;
import com.example.nexio.dto.ResetPasswordRequest;
import com.example.nexio.dto.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    // Temporary storage for OTPs (In a real app, use Redis or DB with expiration)
    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();

    public void sendRegistrationOtp(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        String otp = emailService.generateOtp();
        otpStorage.put(email, otp);
        System.out.println("OTP for Registration [" + email + "] is: " + otp);
        emailService.sendEmail(email, "Nexio - Verification Code", "Your Nexio registration OTP is: " + otp);
    }

    public void sendForgotPasswordOtp(String email) {
        if (!userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email not found");
        }
        String otp = emailService.generateOtp();
        otpStorage.put(email, otp);
        System.out.println("OTP for Password Reset [" + email + "] is: " + otp);
        emailService.sendEmail(email, "Nexio - Password Reset", "Your password reset OTP is: " + otp);
    }

    public User register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        String storedOtp = otpStorage.get(request.getEmail());
        if (storedOtp == null || !storedOtp.equals(request.getOtp())) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(request.getPassword()); // In production, use BCrypt
        user.setRole("USER");

        // Clear OTP after successful registration
        otpStorage.remove(request.getEmail());

        return userRepository.save(user);
    }

    public User login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        if (user.isBlocked()) {
            throw new RuntimeException("Your account has been blocked. Please contact support.");
        }

        return user;
    }

    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String storedOtp = otpStorage.get(request.getEmail());
        if (storedOtp == null || !storedOtp.equals(request.getOtp())) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        user.setPassword(request.getNewPassword()); // In production, use BCrypt
        userRepository.save(user);

        // Clear OTP after successful reset
        otpStorage.remove(request.getEmail());
    }
}
