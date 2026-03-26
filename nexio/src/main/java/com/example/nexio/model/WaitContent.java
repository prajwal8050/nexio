package com.example.nexio.model;

import jakarta.persistence.*;

@Entity
@Table(name = "wait_content")
public class WaitContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String category; // ARTICLE, MUSIC, PERK
    private String icon;
    private String link;
    private int minWaitTime; // Minimum wait time to show this content

    public WaitContent() {}

    public WaitContent(String title, String description, String category, String icon, String link, int minWaitTime) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.icon = icon;
        this.link = link;
        this.minWaitTime = minWaitTime;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
    public int getMinWaitTime() { return minWaitTime; }
    public void setMinWaitTime(int minWaitTime) { this.minWaitTime = minWaitTime; }
}
