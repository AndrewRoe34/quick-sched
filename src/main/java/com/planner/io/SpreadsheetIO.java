package com.planner.io;

import com.planner.models.Event;
import com.planner.models.Task;
import com.planner.schedule.day.Day;
import com.planner.util.EventLog;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

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

        this.filename = filename;
        eventLog.reportExcelFileNameChange(filename);
    }

    public void exportScheduleToExcel(List<Day> schedule) throws IOException {
        if (schedule.isEmpty())
            return;

        // Creates spreadsheets directory if it doesn't exist
        new File("spreadsheets").mkdir();

        File path = new File("spreadsheets", this.filename);
        if (Files.exists(path.toPath()))
            Files.delete(path.toPath());

        Files.createFile(path.toPath());

        try (
                OutputStream os = Files.newOutputStream(Paths.get(path.toURI()));
                Workbook wb = new Workbook(os, "AgilePlannerApplication", "1.0")
        ) {
            // Creates sheets as needed
            Worksheet eventSheet = null, taskSheet = null;
            for (Day day : schedule) {
                if (!day.getEventList().isEmpty() && eventSheet == null)
                    eventSheet = wb.newWorksheet("Events");

                if (day.getNumSubTasks() != 0 && taskSheet == null)
                    taskSheet = wb.newWorksheet("Tasks");

                if (eventSheet != null && taskSheet != null)
                    break;
            }

            // Populates created worksheets with event and task data
            int eventCount = 0, taskCount = 0;
            for (Day day : schedule) {
                if (eventSheet != null) {
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

                if (taskSheet != null) {
                    for (; taskCount < day.getNumSubTasks(); taskCount++) {
                        Task.SubTask subTask = day.getSubTask(taskCount);

                        Task parentTask = subTask.getParentTask();

                        String id = String.valueOf(parentTask.getId());
                        String name = parentTask.getName();
                        String color = parentTask.getColor() == null ? "None" : parentTask.getColor().toString();
                        String time = day.getTaskTimeStamps().get(taskCount).toString();
                        String date = parentTask.getDateStamp();

                        taskSheet.value(taskCount + 1, 0, id);
                        taskSheet.value(taskCount + 1, 1, name);
                        taskSheet.value(taskCount + 1, 2, color);
                        taskSheet.value(taskCount + 1, 3, time);
                        taskSheet.value(taskCount + 1, 4, date);
                    }
                }
            }

            // Applies table format to created worksheets
            if (eventSheet != null) {
                eventSheet.range(0, 0, eventCount, 5).createTable()
                        .setDisplayName("TableDisplayName")
                        .setName("TableName")
                        .styleInfo()
                        .setStyleName("TableStyleMedium1")
                        .setShowLastColumn(true);

                eventSheet.value(0, 0, "ID");
                eventSheet.value(0, 1, "NAME");
                eventSheet.value(0, 2, "COLOR");
                eventSheet.value(0, 3, "TIME");
                eventSheet.value(0, 4, "DATE");
                eventSheet.value(0, 5, "DAYS");
            }

            if (taskSheet != null) {
                taskSheet.range(0, 0, taskCount, 4).createTable()
                        .setDisplayName("TableDisplayName")
                        .setName("TableName")
                        .styleInfo()
                        .setStyleName("TableStyleMedium1")
                        .setShowLastColumn(true);

                taskSheet.value(0, 0, "ID");
                taskSheet.value(0, 1, "NAME");
                taskSheet.value(0, 2, "COLOR");
                taskSheet.value(0, 3, "TIME");
                taskSheet.value(0, 4, "DATE");
            }

            wb.finish();
            wb.close();

            eventLog.reportExcelExportSchedule();
        }
    }
}
