package ru.job4j.grabber;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Post {
    private int id;
    private String title;
    private String link;
    private String description;
    private LocalDateTime created;

    public Post(int id, String link, String title, String descripton, LocalDateTime created) {
        this.id = id;
        this.title = title;
        this.link = link;
        this.description = descripton;
        this.created = created;
    }

    public Post(String link, String title, String descripton, LocalDateTime created) {
        this.title = title;
        this.link = link;
        this.description = descripton;
        this.created = created;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object p) {
        return id == ((Post) p).id && link.equals(((Post) p).link);
    }

    @Override
    public int hashCode() {
        return id * Integer.parseInt(link.substring(link.length() - 4, 4));
    }

    @Override
    public String toString() {
        DateTimeFormatter aFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String foramttedString = created.format(aFormatter);
        return String.format("ID = %d. %s created %s %s %n %s %n", id, title, foramttedString, link, description);
    }
}
