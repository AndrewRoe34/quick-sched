package com.planner.ui;

import com.planner.models.UserConfig;
import com.planner.scripter.ScriptFSM;
import com.planner.ui.tables.TableFormatter;
import com.planner.util.JsonHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Thread.sleep;

public class SessionState implements TUIState {

    Scanner scanner = new Scanner(System.in);

    @Override
    public void setupAndDisplayPage() {
        File file = new File("data/scripts");
        File[] files = file.listFiles();
        List<File> scriptList = new ArrayList<>();
        assert files != null;
        for (File f : files) {
            if (f.getName().length() > 5 && ".smpl".equals(f.getName().substring(f.getName().length() - 5))) {
                scriptList.add(f);
            }
        }
        System.out.println();
        System.out.println(TableFormatter.formatPrettyScriptOptionsTable(scriptList));
        System.out.print("\n                                                         Enter ID: ");
        if (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            if (!input.isBlank() && hasInteger(input)) {
                int id = Integer.parseInt(input);
                if (id >= 0 && id < scriptList.size()) {
                    try {
                        TUIState.clearScreen();
                        ScriptFSM scriptFSM = new ScriptFSM();
                        scriptFSM.getScheduleManager().resetData();
                        scriptFSM.executeScript(scriptList.get(id).getAbsolutePath());
//                        System.out.println(
//                                "\n-------------------------------------------------------------\n" +
//                                "#############################################################\n" +
//                                        "#           Exiting the Simple Script Environment           #\n" +
//                                        "#############################################################\n"
//                        );
                        // need to have ScheduleManager reset (causing a lot of glitches when you keep rerunning the same file over and over)
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        System.out.print("\n\nWould you like to leave? ");
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }

    private boolean hasInteger(String s) {
        try {
            Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

}
