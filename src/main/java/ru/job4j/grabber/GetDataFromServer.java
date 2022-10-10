package ru.job4j.grabber;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class GetDataFromServer {
    public static void main(String[] args) throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
    }
}
