package com.planner.io;

import com.planner.models.Card;
import com.planner.models.Task;
import com.planner.models.UserConfig;
import com.planner.schedule.day.Day;
import com.planner.util.EventLog;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import com.planner.util.SpreadsheetUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
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

    /**
     * Constructor for the {@code SpreadsheetIO} class. Sets the event log.
     *
     * @param eventLog Global event log object. Logs events of value during function execution in the
     *                 {@code SpreadsheetIO} class.
     */
    public SpreadsheetIO(EventLog eventLog) throws GeneralSecurityException, IOException {
        this.eventLog = eventLog;
    }

    /**
     * Updates the default filename to the filename provided by the user.
     * This function adds the appropriate file extension if the provided filename has no extension.
     * It also replaces incorrect file extensions with the appropriate extension, to rectify extension spelling errors.
     *
     * @param filename The filename that was entered by the user. Can be {@code 'none'} or another string.
     */
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

    /**
     * Exports created schedule to an Excel file, if it isn't empty.
     *
     * @param schedule The schedule to be exported to an Excel file.
     * @param cards Cards to be used to create a {@code Boards} Worksheet in the Excel file.
     * @param archivedTasks Archived tasks that will be used to set appropriate flags
     *                     when populating the {@code Boards} Worksheet.
     * @param userConfig User configuration settings that will be checked to determine whether
     *                   coloring will be enabled when populating the {@code Schedule} Worksheet.
     */
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
            Worksheet scheduleSheet = SpreadsheetUtil.createScheduleSheet(wb, schedule);
            Worksheet eventSheet = SpreadsheetUtil.createEventSheet(wb, schedule);
            Worksheet taskSheet = SpreadsheetUtil.createTaskSheet(wb, schedule);

            SpreadsheetUtil.populateScheduleSheet(scheduleSheet, schedule, userConfig);
            SpreadsheetUtil.populateEventSheet(eventSheet, schedule);
            SpreadsheetUtil.populateTaskSheet(taskSheet, schedule);

            wb.finish();
            wb.close();

            eventLog.reportExcelExportSchedule();
        }
    }
}