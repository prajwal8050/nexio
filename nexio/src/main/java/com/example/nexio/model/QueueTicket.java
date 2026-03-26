package com.example.nexio.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "queue_tickets")
public class QueueTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private ServiceItem service;

    private String queueNumber;
    private int peopleAhead;
    private int waitTime;
    private String status; // WAITING, SERVING, COMPLETED, CANCELLED, AWAY
    private LocalDateTime bookingDate;
    private boolean priority;

    public QueueTicket() {
    }

    public QueueTicket(Long id, User user, ServiceItem service, String queueNumber, int peopleAhead, int waitTime,
            String status, LocalDateTime bookingDate) {
        this.id = id;
        this.user = user;
        this.service = service;
        this.queueNumber = queueNumber;
        this.peopleAhead = peopleAhead;
        this.waitTime = waitTime;
        this.status = status;
        this.bookingDate = bookingDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ServiceItem getService() {
        return service;
    }

    public void setService(ServiceItem service) {
        this.service = service;
    }

    public String getQueueNumber() {
        return queueNumber;
    }

    public void setQueueNumber(String queueNumber) {
        this.queueNumber = queueNumber;
    }

    public int getPeopleAhead() {
        return peopleAhead;
    }

    public void setPeopleAhead(int peopleAhead) {
        this.peopleAhead = peopleAhead;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public boolean isPriority() {
        return priority;
    }

    public void setPriority(boolean priority) {
        this.priority = priority;
    }
}
