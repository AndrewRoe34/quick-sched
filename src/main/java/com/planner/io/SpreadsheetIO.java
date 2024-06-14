package com.planner.io;

import com.planner.models.Card;
import com.planner.models.Event;
import com.planner.models.Task;
import com.planner.models.UserConfig;
import com.planner.schedule.day.Day;
import com.planner.util.EventLog;
import com.planner.util.Time;
import org.dhatim.fastexcel.Color;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import com.planner.util.SpreadsheetUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Class to handle operations related to spreadsheet files.
 *
 * @author Abah Olotuche Gabriel
 */
public class SpreadsheetIO {
    private String filename = "schedule.xlsx";
    private EventLog eventLog;

    public SpreadsheetIO(EventLog eventLog) throws GeneralSecurityException, IOException {
        this.eventLog = eventLog;
    }

    public void setFilename(String filename) {
        if (filename.isBlank() || filename.isEmpty() || filename.equalsIgnoreCase("none"))
            return;

        int extensionIndex = filename.lastIndexOf('.');

        if (extensionIndex == -1)
            filename += ".xlsx";
        else {
            String fileExtension = filename.substring(extensionIndex);

            if (!fileExtension.equals(".xlsx")) {
                System.out.println(
                        "Invalid file extension provided (" + fileExtension + "). " +
                                "Ignoring in favor of expected extension (.xlsx)."
                );
                filename = filename.substring(0, extensionIndex) + ".xlsx";
            }
        }

        this.filename = filename;
        eventLog.reportExcelFileNameChange(filename);
    }

    public void exportScheduleToExcel(
            List<Day> schedule,
            List<Card> cards,
            PriorityQueue<Task> archivedTasks,
            UserConfig userConfig
    ) throws IOException {
        if (schedule.isEmpty())
            return;

        // Creates spreadsheets directory if it doesn't exist
        String spreadsheetDir = "data/spreadsheets";
        new File(spreadsheetDir).mkdir();

        File path = new File(spreadsheetDir, this.filename);
        if (Files.exists(path.toPath()))
            Files.delete(path.toPath());

        Files.createFile(path.toPath());

        try (
                OutputStream os = Files.newOutputStream(Paths.get(path.toURI()));
                Workbook wb = new Workbook(os, "AgilePlannerApplication", "1.0")
        ) {
            Worksheet scheduleSheet = createScheduleSheet(wb, schedule);
            Worksheet boardSheet = createBoardSheet(wb, cards);
            Worksheet eventSheet = createEventSheet(wb, schedule);
            Worksheet taskSheet = createTaskSheet(wb, schedule);

            populateScheduleSheet(scheduleSheet, schedule, userConfig);
            populateBoardSheet(boardSheet, cards, archivedTasks);
            populateEventSheet(eventSheet, schedule);
            populateTaskSheet(taskSheet, schedule);

            wb.finish();
            wb.close();

            eventLog.reportExcelExportSchedule();
        }
    }

    private Worksheet createEventSheet(Workbook wb, List<Day> schedule) {
        Worksheet eventSheet = null;

        for (Day day : schedule) {
            if (!day.getEventList().isEmpty()) {
                eventSheet = wb.newWorksheet("Events");

                eventSheet.value(0, 0, "ID");
                eventSheet.value(0, 1, "NAME");
                eventSheet.value(0, 2, "COLOR");
                eventSheet.value(0, 3, "TIME");
                eventSheet.value(0, 4, "DATE");
                eventSheet.value(0, 5, "DAYS");

                eventSheet.range(
                        0,
                        0,
                        0,
                        5
                ).style().bold().fillColor(Color.GRAY7).set();

                break;
            }
        }

        return eventSheet;
    }

    private Worksheet createTaskSheet(Workbook wb, List<Day> schedule) {
        Worksheet taskSheet = null;

        for (Day day : schedule) {
            if (day.getNumSubTasks() != 0) {
                taskSheet = wb.newWorksheet("Tasks");

                taskSheet.value(0, 0, "ID");
                taskSheet.value(0, 1, "NAME");
                taskSheet.value(0, 2, "COLOR");
                taskSheet.value(0, 3, "HOURS");
                taskSheet.value(0, 4, "TIME");
                taskSheet.value(0, 5, "DATE");

                taskSheet.range(
                        0,
                        0,
                        0,
                        5
                ).style().bold().fillColor(Color.GRAY7).set();

                break;
            }
        }

        return taskSheet;
    }

    private Worksheet createBoardSheet(Workbook wb, List<Card> cards) {
        Worksheet boardSheet = null;

        if (cards.isEmpty())
            return boardSheet;

        boardSheet = wb.newWorksheet("Board");

        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);

            boardSheet.value(0, i, card.getTitle());
            boardSheet.style(0, i).fontColor(
                    SpreadsheetUtil.convertColorsEnumToFastExcelColorEnum(card.getColorId())
            ).bold().fillColor(Color.GRAY7).set();
        }

        return boardSheet;
    }

    private Worksheet createScheduleSheet(Workbook wb, List<Day> schedule) {
        Worksheet scheduleSheet = wb.newWorksheet("Schedule");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        for (int i = 0; i < schedule.size(); i++) {
            Day day = schedule.get(i);

            scheduleSheet.value(0, i, sdf.format(day.getDate().getTime()));
        }

        scheduleSheet.range(
                0,
                0,
                0,
                schedule.size() - 1
        ).style().bold().fillColor(Color.GRAY7).set();

        return scheduleSheet;
    }

    private void populateEventSheet(Worksheet eventSheet, List<Day> schedule) {
        if (eventSheet == null)
            return;

        int eventCount = 0;
        for (Day day : schedule) {
            for (; eventCount < day.getEventList().size(); eventCount++) {
                Event event = day.getEventList().get(eventCount);

                String id = String.valueOf(event.getId());
                String name = event.getName();
                String color = event.getColor().toString();
                String time = event.getTimeStamp().toString();
                String date = event.isRecurring() ? "-" : event.getDateStamp();
                String days = event.isRecurring() ? Arrays.toString(event.getDays()) : "-";

                eventSheet.value(eventCount + 1, 0, id);
                eventSheet.value(eventCount + 1, 1, name);
                eventSheet.value(eventCount + 1, 2, color);
                eventSheet.value(eventCount + 1, 3, time);
                eventSheet.value(eventCount + 1, 4, date);
                eventSheet.value(eventCount + 1, 5, days);
            }
        }
    }

    private void populateTaskSheet(Worksheet taskSheet, List<Day> schedule) {
        if (taskSheet == null)
            return;

        int taskCount = 0;
        for (Day day : schedule) {
            for (; taskCount < day.getNumSubTasks(); taskCount++) {
                Task.SubTask subTask = day.getSubTask(taskCount);

                Task parentTask = subTask.getParentTask();

                String id = String.valueOf(parentTask.getId());
                String name = parentTask.getName();
                String color = parentTask.getColor() == null ? "None" : parentTask.getColor().toString();
                String hours = String.valueOf(subTask.getSubTaskHours());
                String time = day.getTaskTimeStamps().get(taskCount).toString();
                String date = parentTask.getDateStamp();

                taskSheet.value(taskCount + 1, 0, id);
                taskSheet.value(taskCount + 1, 1, name);
                taskSheet.value(taskCount + 1, 2, color);
                taskSheet.value(taskCount + 1, 3, hours);
                taskSheet.value(taskCount + 1, 4, time);
                taskSheet.value(taskCount + 1, 5, date);
            }
        }
    }

    private void populateBoardSheet(Worksheet boardSheet, List<Card> cards, PriorityQueue<Task> archivedTasks) {
        if (boardSheet == null)
            return;

        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);

            for (int j = 0; j < card.getTask().size(); j++) {
                String taskString = SpreadsheetUtil.getCardTaskString(card, j, archivedTasks);

                boardSheet.value(j + 1, i, taskString);
            }
        }
    }

    private void populateScheduleSheet(Worksheet scheduleSheet, List<Day> schedule, UserConfig userConfig) {
        for (int i = 0; i < schedule.size(); i++) {
            Day day = schedule.get(i);

            List<String> arrangedIDs = SpreadsheetUtil.arrangeTasksAndEventsInOrderOfOccurrence(day);
            for (int j = 0; j < arrangedIDs.size(); j++) {
                String id = arrangedIDs.get(j);

                Time.TimeStamp ts = null;
                String name = "";
                Card.Colors color = null;

                StringBuilder output = new StringBuilder();

                int index = Integer.parseInt(String.valueOf(id.charAt(1)));
                if (id.charAt(0) == 't') {
                    Task.SubTask subTask = day.getSubTask(index);

                    ts = subTask.getTimeStamp();
                    name = subTask.getParentTask().getName();
                    color = subTask.getParentTask().getColor();
                }
                else if (id.charAt(0) == 'e') {
                    Event event = day.getEvent(index);

                    ts = event.getTimeStamp();
                    name = event.getName();
                    color = event.getColor();
                }

                output.append(ts).append(" - ").append(name);

                scheduleSheet.value(j + 1, i, output.toString());

                if (userConfig.isLocalScheduleColors()) {
                    scheduleSheet.style(j + 1, i).fontColor(
                            SpreadsheetUtil.convertColorsEnumToFastExcelColorEnum(color)
                    ).set();
                }
            }
        }
    }
}