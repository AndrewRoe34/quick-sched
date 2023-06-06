package agile.planner.scripter;

import agile.planner.exception.InvalidGrammarException;
import agile.planner.schedule.day.Day;

/**
 * Class --> day: [capacity:int], [date:int] <br>
 * Creates a new instance of a Day while utilizing dynamic variable usage
 *
 * @author Andrew Roe
 */
public class DayState extends State {

    @Override
    protected void processFunc(String line) {
        String[] tokens = processArguments(line, 2, ",");
        try {
            dayList.add(new Day(scheduleManager.getLastDayId() + dayList.size(), Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1])));
            System.out.println("Day added.. [D" + (scheduleManager.getLastTaskId() + dayList.size() - 1) + "]");
        } catch(Exception e) {
            throw new InvalidGrammarException("Invalid input. Expected[day: <capacity: int>, <date: int>]");
        }
    }
}
