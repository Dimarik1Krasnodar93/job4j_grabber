package ru.job4j.grabber;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import ru.job4j.AlertRabbit;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class GetDataFromServer {
    public static void main(String[] args) throws SchedulerException {
        Properties properties = loadProperties();
        Store mwmTraccker = new MemTracker();
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        JobDataMap jobdatamap = new JobDataMap();
        jobdatamap.put("linkPages", "https://career.habr.com/vacancies/java_developer?page=%d");
        jobdatamap.put("store", mwmTraccker);
        JobDetail job = newJob(GetData.class).usingJobData(jobdatamap).build();
        SimpleScheduleBuilder times = simpleSchedule()
                .withIntervalInSeconds(Integer.parseInt(properties.getProperty("rabbit.interval")))
                .repeatForever();
        Trigger trigger = newTrigger()
                .startNow()
                .withSchedule(times)
                .build();
        scheduler.scheduleJob(job, trigger);
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            properties.load(in);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return properties;
    }

    public static class GetData implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            Store store = (Store) context.getJobDetail().getJobDataMap().get("store");
            String linkPages = (String) context.getJobDetail().getJobDataMap().get("linkPages");
            HabrCareerDateTimeParser dataParser = new HabrCareerDateTimeParser();
            HabrCareerParse habrCareerParse = new HabrCareerParse(dataParser);
            List<Post> res = habrCareerParse.list(linkPages);
            for (Post post : res) {
                store.save(post);
            }
        }
    }
}
