package agile.planner.scripter;

import agile.planner.user.UserConfig;
import agile.planner.util.EventLog;

import java.io.FileNotFoundException;

public class PreProcessorState extends State {

    @Override
    protected void processFunc(String line) {
        if("__DEF_CONFIG__".equals(line)) {
            int[] arr = {8, 8, 8, 8, 8, 8, 8};
            State.userConfig = new UserConfig("name", "email", arr, 14,
                    14, false, true, false,0, 0);
        } else if("__CURR_CONFIG__".equals(line)) {
            //State.userConfig = scheduleManager.getUserConfig();
        } else if("__LOG__".equals(line)) {
            /*
            try {
                State.eventLog = EventLog.getEventLog(System.out);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } */
        } else if("__DEBUG__".equals(line)) {
            //DO NOTHING FOR NOW
        }else if("__IMPORT__".equals(line)) {
            //DO NOTHING FOR NOW
        } else if("__EXPORT__".equals(line)) {
            //DO NOTHING FOR NOW
        } else {
            //DO NOTHING FOR BUILD (for now)
        }
    }
}
