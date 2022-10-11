package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HabrCareerParse implements Parse {

    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);

    private static final int COUNT_PAGES = 5;

    private static String linkPages = "https://career.habr.com/vacancies/java_developer?page=%d";

    private DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser; }

    private static String retrieveDescription(String link) {
        String rslt;
        try {
            Connection connection = Jsoup.connect(link);
            Document document = connection.get();
            Element row = document.select(".style-ugc").first();
            rslt = row.text();
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException();
        }
        return rslt;
    }

    @Override
    public List<Post> list(String link) {
        List<Post> postList = new ArrayList<>();
        for (int i = 1; i <= COUNT_PAGES; i++) {
            Connection connection = Jsoup.connect(String.format(link, i));
            try {
                Document document = connection.get();
                Elements rows = document.select(".vacancy-card__inner");
                for (Element row : rows) {
                    postList.add(getPost(row));
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return postList;
    }

    private Post getPost(Element row) {
        Element titleElement = row.select(".vacancy-card__title").first();
        Element linkElement = titleElement.child(0);
        String vacancyName = titleElement.text();
        String link2 = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
        String description = retrieveDescription(link2);
        Element dateElement = row.select(".vacancy-card__date").first();
        Element dateElement2 = dateElement.child(0);
        String date2 = dateElement2.attr("datetime");
        LocalDateTime ldt = dateTimeParser.parse(date2);
        return  new Post(link2, vacancyName, description, ldt);
    }

    public static void main(String[] args) {
        DateTimeParser dateTimeParser = new HabrCareerDateTimeParser();
        HabrCareerParse habrCareerParse = new HabrCareerParse(dateTimeParser);
        List<Post> res = habrCareerParse.list(PAGE_LINK + "?page=%d");
        for (Post post : res) {
            System.out.println(post);
        }
    }
}
