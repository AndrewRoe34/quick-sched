package com.planner.io;

import com.planner.models.Task;
import com.planner.models.UserConfig;
import com.planner.schedule.day.Day;
import com.planner.util.EventLog;
import com.planner.util.GoogleCalendarUtil;
import com.planner.util.Time;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

/**
 * Class to demonstrate use of Calendar events list API.
 *
 * @author Abah Olotuche Gabriel
 */
public class GoogleCalendarIO {
    /**
     * Application name.
     */
    private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    /**
     * Directory to store authorization tokens for this application.
     */
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES =
            Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final String calendarId = "primary";
    private final Calendar service;
    private EventLog eventLog;

    public GoogleCalendarIO(EventLog eventLog) throws GeneralSecurityException, IOException {
        this.eventLog = eventLog;
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        this.eventLog.reportGoogleCalendarAuthorization();
    }

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        // Load client secrets.
        InputStream in = GoogleCalendarIO.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        //returns an authorized Credential object.
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    // [COMPLETE]
    public void exportScheduleToGoogle(UserConfig userconfig, List<Day> week) throws IOException {
        // need to handle null pointer here since if we try to export to Google without building, else we'll get an exception
        for(Day day : week) {
            // todo need config option that prevents writing of log output to console
            for (com.planner.models.Event e1 : day.getEventList()) {
                Event event = GoogleCalendarUtil.formatEventToGoogleEvent(e1);
                event = service.events().insert(calendarId, event).execute();
                System.out.printf("Event created: %s\n", event.getHtmlLink());
            }

            for(Task.SubTask subTask : day.getSubTaskList()) {
                Event event = GoogleCalendarUtil.formatTaskToGoogleEvent(subTask);
                event = service.events().insert(calendarId, event).execute();
                System.out.printf("Task created: %s\n", event.getHtmlLink());
            }
        }
        eventLog.reportGoogleCalendarExportSchedule();
    }

    // [COMPLETE]
    public void importScheduleFromGoogle() throws IOException {
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = service.events().list("primary")
                .setMaxResults(100)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();
        List<String> tasks = GoogleCalendarUtil.formatGoogleEventsToTasks(items);
        if (tasks.isEmpty()) {
            System.out.println("No upcoming tasks found.");
        } else {
            System.out.println("Upcoming tasks");
            for (String t1 : tasks) {
                System.out.println(t1);
            }
        }
        eventLog.reportGoogleCalendarImportSchedule();
    }

    // [COMPLETE]
    public int cleanGoogleSchedule() throws IOException {
        DateTime now = new DateTime(Time.getFormattedCalendarInstance(0).getTime());
        Events events = service.events().list("primary")
                .setMaxResults(100)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();
        int count = 0;
        for(Event e : items) {
            // hashcode representing an Agile Planner created event
            if(e.getDescription() != null && e.getDescription().contains("eb007aba6df2559a02ceb17ddba47c85b3e2b930")) {
                service.events().delete(calendarId, e.getId()).execute();
                count++;
            }
        }
        eventLog.reportGoogleCalendarCleanSchedule(count);
        return count;
    }
}