package com.example.nexio.controller;

import com.example.nexio.model.*;
import com.example.nexio.service.QueueService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class QueueController {

    @Autowired
    private QueueService queueService;

    @Autowired
    private com.example.nexio.repository.WaitContentRepository waitContentRepository;

    @GetMapping("/services")
    public List<ServiceItem> getServices() {
        return queueService.getAllServices();
    }

    @PostMapping("/queues/join")
    public ResponseEntity<?> joinQueue(@RequestParam Long serviceId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body("Please login first");
        }
        try {
            return ResponseEntity.ok(queueService.joinQueue(user.getId(), serviceId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/queues/history")
    public ResponseEntity<?> getHistory(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body("Please login first");
        }
        return ResponseEntity.ok(queueService.getUserHistory(user.getId()));
    }

    // Agent Endpoints
    @PostMapping("/agent/complete")
    public ResponseEntity<?> completeTicket(@RequestParam Long ticketId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"AGENT".equals(user.getRole())) {
            return ResponseEntity.status(403).body("Forbidden");
        }
        queueService.completeTicket(ticketId);
        return ResponseEntity.ok("Ticket completed");
    }

    @PostMapping("/agent/service/add")
    public ResponseEntity<?> addService(@RequestBody ServiceItem service, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"AGENT".equals(user.getRole())) {
            return ResponseEntity.status(403).body("Forbidden");
        }
        return ResponseEntity.ok(queueService.saveService(service));
    }

    @PostMapping("/agent/service/update")
    public ResponseEntity<?> updateService(@RequestBody ServiceItem service, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"AGENT".equals(user.getRole())) {
            return ResponseEntity.status(403).body("Forbidden");
        }
        return ResponseEntity.ok(queueService.saveService(service));
    }

    @PostMapping("/agent/manual-join")
    public ResponseEntity<?> manualJoin(@RequestParam String name, @RequestParam String phone,
            @RequestParam Long serviceId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"AGENT".equals(user.getRole())) {
            return ResponseEntity.status(403).body("Forbidden");
        }
        return ResponseEntity.ok(queueService.joinQueueManually(name, phone, serviceId));
    }

    @GetMapping("/agent/queues")
    public ResponseEntity<?> getActiveQueues(@RequestParam Long serviceId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"AGENT".equals(user.getRole())) {
            return ResponseEntity.status(403).body("Forbidden");
        }
        return ResponseEntity.ok(queueService.getActiveTicketsForService(serviceId));
    }

    // Simulation Endpoints
    @PostMapping("/agent/simulation/seed")
    public ResponseEntity<?> seedQueue(@RequestParam Long serviceId, @RequestParam int count, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"AGENT".equals(user.getRole())) {
            return ResponseEntity.status(403).body("Forbidden");
        }
        queueService.seedInitialQueue(serviceId, count);
        return ResponseEntity.ok("Queue seeded");
    }

    @PostMapping("/agent/simulation/advance")
    public ResponseEntity<?> advanceQueue(@RequestParam Long serviceId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"AGENT".equals(user.getRole())) {
            return ResponseEntity.status(403).body("Forbidden");
        }
        queueService.advanceQueue(serviceId);
        return ResponseEntity.ok("Queue advanced");
    }

    @PostMapping("/queues/toggle-away")
    public ResponseEntity<?> toggleAway(@RequestParam Long ticketId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body("Please login first");
        }
        return ResponseEntity.ok(queueService.toggleAwayStatus(ticketId));
    }

    @PostMapping("/queues/boost")
    public ResponseEntity<?> boostTicket(@RequestParam Long ticketId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null)
            return ResponseEntity.status(401).body("Please login first");
        return ResponseEntity.ok(queueService.boostTicket(ticketId));
    }

    @GetMapping("/queues/active-path")
    public ResponseEntity<?> getActivePath(@RequestParam Long serviceId) {
        return ResponseEntity.ok(queueService.getAllActiveTicketsForService(serviceId));
    }

    @GetMapping("/queues/serving")
    public ResponseEntity<?> getServing(@RequestParam Long serviceId) {
        return ResponseEntity.ok(queueService.getCurrentlyServing(serviceId));
    }

    @GetMapping("/wait-content")
    public ResponseEntity<?> getWaitContent() {
        return ResponseEntity.ok(waitContentRepository.findAll());
    }

    @GetMapping("/queues/service/history")
    public ResponseEntity<?> getServiceHistory(@RequestParam Long serviceId) {
        return ResponseEntity.ok(queueService.getCompletedTicketsForService(serviceId));
    }
}
