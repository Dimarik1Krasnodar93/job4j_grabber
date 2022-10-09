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

    private final DateTimeParser dateTimeParser;

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    private static String retrieveDescription(String link) {
        String rslt = null;
        try {
            Connection connection = Jsoup.connect(link);
            Document document = connection.get();
            Element row = document.select(".style-ugc").first();
            rslt = row.text();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return rslt;
    }

    public List<Post> list(String link) {
        List<Post> postList = new ArrayList<>();
        String strLinkPages = "https://career.habr.com/vacancies/java_developer?page=%d";
        try {
            Connection connection = Jsoup.connect(link);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                String vacancyName = titleElement.text();
                String link2 = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                String description = retrieveDescription(link2);
                Element dateElement = row.select(".vacancy-card__date").first();
                Element dateElement2 = dateElement.child(0);
                String date2 = dateElement2.attr("datetime");
                date2 = date2.substring(0, 19);
                HabrCareerDateTimeParser dataParser = new HabrCareerDateTimeParser();
                LocalDateTime ldt = dataParser.parse(date2);
                Element titleCardActions = row.select(".vacancy-favorite-btn").first();
                int id = Integer.parseInt(titleCardActions.attr("data-vacancy-favorite-for"));
                postList.add(new Post(id, link2, vacancyName, description, ldt));
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return postList;
    }

    private List<Post> parsePages(int count) {
        List<Post> result = new ArrayList<>();
        String strLinkPages = "https://career.habr.com/vacancies/java_developer?page=%d";
        for (int i = 1; i <= count; i++) {
            List<Post> resultTemp = list(String.format(strLinkPages, i));
            for (Post post : resultTemp) {
                result.add(post);
            }
        }
        return result;
    }

    public static void main2HabrCareerParse() {
        HabrCareerDateTimeParser dataParser = new HabrCareerDateTimeParser();
        HabrCareerParse habrCareerParse = new HabrCareerParse(dataParser);
        List<Post> res = habrCareerParse.parsePages(5);
        for (Post post : res) {
            System.out.println(post);
        }
    }

    public static void main(String[] args) throws IOException {
        main2HabrCareerParse();
        System.out.println("____________");
        String strLinkPages = "https://career.habr.com/vacancies/java_developer?page=%d";
        for (int i = 1; i <= 5; i++) {
            Connection connection = Jsoup.connect(String.format(strLinkPages, i));
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                String vacancyName = titleElement.text();
                String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                String description = retrieveDescription(link);
                Element dateElement = row.select(".vacancy-card__date").first();
                Element dateElement2 = dateElement.child(0);
                String date2 = dateElement2.attr("datetime");
                date2 = date2.substring(0, 19);
                HabrCareerDateTimeParser dataParser = new HabrCareerDateTimeParser();
                LocalDateTime ldt = dataParser.parse(date2);
                System.out.printf("%s %s date %s %n Описание %n %s", vacancyName, link, date2, description);
            });
        }
    }
}
