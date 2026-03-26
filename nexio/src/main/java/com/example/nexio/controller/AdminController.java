package com.example.nexio.controller;

import com.example.nexio.model.*;
import com.example.nexio.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @GetMapping("/stats")
    public ResponseEntity<?> getStats(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.findAll().stream().filter(u -> "USER".equals(u.getRole())).count());
        stats.put("totalAgents", userRepository.findAll().stream().filter(u -> "AGENT".equals(u.getRole())).count());
        stats.put("totalServices", serviceRepository.count());
        stats.put("totalTickets", ticketRepository.count());
        stats.put("activeTickets",
                ticketRepository.findAll().stream().filter(t -> "WAITING".equals(t.getStatus())).count());

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return ResponseEntity.status(403).body("Forbidden");
        }
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/tickets")
    public ResponseEntity<?> getAllTickets(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return ResponseEntity.status(403).body("Forbidden");
        }
        return ResponseEntity.ok(ticketRepository.findAll());
    }

    @PostMapping("/users/{id}/toggle-block")
    public ResponseEntity<?> toggleUserBlock(@PathVariable Long id, HttpSession session) {
        User admin = (User) session.getAttribute("user");
        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        // Basic protection: do not block self (admin)
        if (admin.getId().equals(id)) {
            return ResponseEntity.status(400).body("Cannot block yourself");
        }

        user.setBlocked(!user.isBlocked());
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "User blocked status toggled", "isBlocked", user.isBlocked()));
    }
}
