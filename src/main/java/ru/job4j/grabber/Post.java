package ru.job4j.grabber;

import java.time.LocalDateTime;

public class Post {
    private int id;
    private String title;
    private String link;
    private String description;
    private LocalDateTime created;

    @Override
    public boolean equals(Object p) {
        return id == ((Post) p).id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("ID = %d. %s %s", id, title, link);
    }
}
