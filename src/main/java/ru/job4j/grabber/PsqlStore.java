package ru.job4j.grabber;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {

    private Connection cnn;

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        try {
            cnn = DriverManager.getConnection(cfg.get("url").toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement preparedStatement = cnn.prepareStatement("Insert into post (name, text, link, created) values (?, ?, ?, ?)")) {
            preparedStatement.setString(1, post.getTitle());
            preparedStatement.setString(2, post.getDescription());
            preparedStatement.setString(3, post.getLink());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            preparedStatement.execute();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            post.setId(generatedKeys.getInt("id"));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> listpost = new ArrayList<>();
        try (Statement st = cnn.createStatement()) {
            ResultSet rslt = st.executeQuery("select * from post");
            while (rslt.next()) {
                listpost.add(new Post(rslt.getInt("id"),
                        rslt.getString("title"),
                        rslt.getString("link"),
                        rslt.getString("description"),
                        rslt.getTimestamp("created").toLocalDateTime()));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return listpost;
    }

    @Override
    public Post findById(int id) {
        Post rslt = null;
        try (PreparedStatement preparedStatement = cnn.prepareStatement("select * from post where id = ?")) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            rslt = new Post(resultSet.getInt("id"),
                    resultSet.getString("title"),
                    resultSet.getString("link"),
                    resultSet.getString("description"),
                    resultSet.getTimestamp("created").toLocalDateTime());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return rslt;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    public static void main(String[] args) {
        Properties properties = loadProperties();
        PsqlStore psqlstore = new PsqlStore(properties);
        Post post1 = new Post("https://career.habr.com/vacancies/1000095970", "Руководитель проектов в Центр управления данными", "description", LocalDateTime.now());
        psqlstore.save(post1);
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream in = PsqlStore.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            properties.load(in);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return  properties;
    }
}
