package com.example.nexio.model;

import jakarta.persistence.*;

@Entity
@Table(name = "services")
public class ServiceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String category;
    private String imageUrl;
    private int estimatedWaitTime; // in minutes

    @Transient
    private int waitingCount;

    public int getWaitingCount() {
        return waitingCount;
    }

    public void setWaitingCount(int waitingCount) {
        this.waitingCount = waitingCount;
    }

    public ServiceItem() {
    }

    public ServiceItem(Long id, String name, String description, String category, String imageUrl,
            int estimatedWaitTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.imageUrl = imageUrl;
        this.estimatedWaitTime = estimatedWaitTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getEstimatedWaitTime() {
        return estimatedWaitTime;
    }

    public void setEstimatedWaitTime(int estimatedWaitTime) {
        this.estimatedWaitTime = estimatedWaitTime;
    }
}
