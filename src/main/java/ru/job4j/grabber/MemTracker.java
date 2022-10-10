package ru.job4j.grabber;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MemTracker implements Store {

    private final List<Post> posts = new ArrayList<>();

    private int id = 0;

    public void save(Post item) {
        item.setId(id++);
        posts.add(item);
    }

    public Post findById(int id) {
        int index = indexOf(id);
        return index != -1 ? posts.get(index) : null;
    }

    public List<Post> getAll() {
        return posts;
    }

    private int indexOf(int id) {
        int index = -1;
        for (int i = 0; i < posts.size(); i++) {
            if (posts.get(i).getId() == id) {
                index = i;
                break;
            }
        }
        return index;
    }
}
