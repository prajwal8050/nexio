package com.example.nexio.service;

import com.example.nexio.model.*;
import com.example.nexio.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class QueueService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private UserRepository userRepository;

    public List<ServiceItem> getAllServices() {
        List<ServiceItem> services = serviceRepository.findAll();
        for (ServiceItem s : services) {
            s.setWaitingCount(ticketRepository.findByServiceIdAndStatus(s.getId(), "WAITING").size());
        }
        return services;
    }

    public QueueTicket joinQueue(Long userId, Long serviceId) {
        User user = userRepository.findById(userId).orElseThrow();
        ServiceItem service = serviceRepository.findById(serviceId).orElseThrow();

        // Get all tickets for this service to find the next number
        List<QueueTicket> allTickets = ticketRepository.findByServiceId(serviceId);
        int nextId = 1;
        if (!allTickets.isEmpty()) {
            nextId = allTickets.stream()
                    .mapToInt(t -> {
                        try {
                            return Integer.parseInt(t.getQueueNumber().substring(1));
                        } catch (Exception e) {
                            return 0;
                        }
                    })
                    .max().orElse(0) + 1;
        }

        int peopleAhead = (int) ticketRepository.findByServiceIdAndStatus(serviceId, "WAITING").size() +
                (int) ticketRepository.findByServiceIdAndStatus(serviceId, "SERVING").size() +
                (int) ticketRepository.findByServiceIdAndStatus(serviceId, "AWAY").size();

        QueueTicket ticket = new QueueTicket();
        ticket.setUser(user);
        ticket.setService(service);
        ticket.setQueueNumber("Q" + nextId);
        ticket.setPeopleAhead(peopleAhead);
        ticket.setWaitTime(peopleAhead * service.getEstimatedWaitTime());
        ticket.setStatus("WAITING");
        ticket.setBookingDate(LocalDateTime.now());

        return ticketRepository.save(ticket);
    }

    public void completeTicket(Long ticketId) {
        QueueTicket ticket = ticketRepository.findById(ticketId).orElseThrow();
        ticket.setStatus("COMPLETED");
        ticket.setPeopleAhead(0);
        ticket.setWaitTime(0);
        ticketRepository.save(ticket);

        // Update others in queue
        updateWaitingPositions(ticket.getService().getId());
    }

    private void updateWaitingPositions(Long serviceId) {
        int servingCount = ticketRepository.findByServiceIdAndStatus(serviceId, "SERVING").size();
        List<QueueTicket> waiting = ticketRepository.findByServiceIdAndStatus(serviceId, "WAITING");
        waiting.addAll(ticketRepository.findByServiceIdAndStatus(serviceId, "AWAY"));

        waiting.sort((a, b) -> {
            if (a.isPriority() != b.isPriority())
                return a.isPriority() ? -1 : 1;
            return a.getId().compareTo(b.getId());
        });

        for (int i = 0; i < waiting.size(); i++) {
            QueueTicket t = waiting.get(i);
            t.setPeopleAhead(servingCount + i);
            t.setWaitTime(t.getPeopleAhead() * t.getService().getEstimatedWaitTime());
            ticketRepository.save(t);
        }
    }

    public ServiceItem saveService(ServiceItem service) {
        return serviceRepository.save(service);
    }

    public QueueTicket joinQueueManually(String name, String phone, Long serviceId) {
        ServiceItem service = serviceRepository.findById(serviceId).orElseThrow();

        // Create a walk-in user for this specific service type
        String guestEmail = phone + "@" + service.getName().toLowerCase().replaceAll("\\s+", "") + ".guest";
        User guest = userRepository.findByEmail(guestEmail).orElseGet(() -> {
            User u = new User();
            u.setName(name + " (Walk-in)");
            u.setEmail(guestEmail);
            u.setPhone(phone);
            u.setRole("USER");
            u.setPassword("guest123");
            return userRepository.save(u);
        });

        return joinQueue(guest.getId(), serviceId);
    }

    public List<QueueTicket> getActiveTicketsForService(Long serviceId) {
        return ticketRepository.findByServiceIdAndStatus(serviceId, "WAITING");
    }

    public List<QueueTicket> getAllActiveTicketsForService(Long serviceId) {
        List<QueueTicket> all = new java.util.ArrayList<>(
                ticketRepository.findByServiceIdAndStatus(serviceId, "SERVING"));
        List<QueueTicket> waiting = new java.util.ArrayList<>(
                ticketRepository.findByServiceIdAndStatus(serviceId, "WAITING"));
        waiting.addAll(ticketRepository.findByServiceIdAndStatus(serviceId, "AWAY"));

        waiting.sort((a, b) -> {
            if (a.isPriority() != b.isPriority())
                return a.isPriority() ? -1 : 1;
            return a.getId().compareTo(b.getId());
        });

        all.addAll(waiting);
        return all;
    }

    public List<QueueTicket> getUserHistory(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return ticketRepository.findByUserOrderByBookingDateDesc(user);
    }

    // --- Demo / Simulation Logic ---

    public void seedInitialQueue(Long serviceId, int count) {
        ServiceItem service = serviceRepository.findById(serviceId).orElseThrow();

        // Clear existing tickets for this service for a clean simulation
        List<QueueTicket> existing = ticketRepository.findByServiceId(serviceId);
        ticketRepository.deleteAll(existing);

        // Use a system/demo user
        User demoUser = userRepository.findByEmail("demo@nexio.com").orElseGet(() -> {
            User u = new User();
            u.setName("Simulation Base");
            u.setEmail("demo@nexio.com");
            u.setPhone("0000000000");
            u.setRole("USER");
            u.setPassword("demo123");
            return userRepository.save(u);
        });

        for (int i = 1; i <= count; i++) {
            QueueTicket t = new QueueTicket();
            t.setUser(demoUser);
            t.setService(service);
            t.setQueueNumber("Q" + i);
            t.setStatus(i == 1 ? "SERVING" : "WAITING");
            t.setPeopleAhead(i == 1 ? 0 : i - 1);
            t.setWaitTime(t.getPeopleAhead() * service.getEstimatedWaitTime());
            t.setBookingDate(LocalDateTime.now().plusSeconds(i));
            ticketRepository.save(t);
        }
    }

    public QueueTicket boostTicket(Long ticketId) {
        QueueTicket ticket = ticketRepository.findById(ticketId).orElseThrow();
        ticket.setPriority(true);
        ticketRepository.save(ticket);
        updateWaitingPositions(ticket.getService().getId());
        return ticket;
    }

    public void advanceQueue(Long serviceId) {
        // Find current SERVING
        List<QueueTicket> serving = ticketRepository.findByServiceIdAndStatus(serviceId, "SERVING");
        for (QueueTicket s : serving) {
            s.setStatus("COMPLETED");
            ticketRepository.save(s);
        }

        // Find next person (Respect priority)
        List<QueueTicket> waiting = ticketRepository.findByServiceIdAndStatus(serviceId, "WAITING");
        waiting.sort((a, b) -> {
            if (a.isPriority() != b.isPriority())
                return a.isPriority() ? -1 : 1;
            return a.getId().compareTo(b.getId());
        });

        if (!waiting.isEmpty()) {
            QueueTicket next = waiting.get(0);
            next.setStatus("SERVING");
            ticketRepository.save(next);
        }

        updateWaitingPositions(serviceId);
    }

    public QueueTicket toggleAwayStatus(Long ticketId) {
        QueueTicket ticket = ticketRepository.findById(ticketId).orElseThrow();
        if ("WAITING".equals(ticket.getStatus())) {
            ticket.setStatus("AWAY");
        } else if ("AWAY".equals(ticket.getStatus())) {
            ticket.setStatus("WAITING");
        }
        ticketRepository.save(ticket);
        updateWaitingPositions(ticket.getService().getId());
        return ticket;
    }

    public QueueTicket getCurrentlyServing(Long serviceId) {
        List<QueueTicket> serving = ticketRepository.findByServiceIdAndStatus(serviceId, "SERVING");
        return serving.isEmpty() ? null : serving.get(0);
    }

    public List<QueueTicket> getCompletedTicketsForService(Long serviceId) {
        return ticketRepository.findByServiceIdAndStatusOrderByBookingDateDesc(serviceId, "COMPLETED");
    }
}
