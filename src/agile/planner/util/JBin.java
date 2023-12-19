package agile.planner.util;

import agile.planner.data.Card;
import agile.planner.data.Label;
import agile.planner.data.Task;
import agile.planner.manager.ScheduleManager;
import agile.planner.schedule.day.Day;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Java Binary Serialization is modeled after JSON for data storage/retrieval.
 * This will provide greater efficiency, structure, security, as well as ease of passage
 * of data with the Front-End component
 *
 * @author Andrew Roe
 */
public class JBin {

    /**
     * Creates a Java Binary Serialization string to be later passed or stored
     *
     * @param cards all Cards in System
     * @return JBin String
     */
    public static String createJBin(List<Card> cards) {
        //TODO might add Days and the tasks they hold (for schedule preservation versus creating another one)
        //TODO key thing to note is that you'll need to create a Calendar instance and use it for the number of days for each Task

        /* NOTES:
        1. Create Label section (with ID, not associated with system)
        2. Create Task section (with ID, not associated with system)
            --> Could hold a Label, which will be identified as "L#"
        3. Create Card section
            --> Could hold a Label, which will be identified as "L#"
            --> Could hold a Task, which will be identified as "T#"

        HashMaps will be very helpful for decoding JBin later on
        Need to account for possibility that file is read two times in a row
            --> This would mean tasks, cards, and labels are the same/duplicates (will need to verify via the equals method, i.e. HashSet)
            --> Otherwise, it simply gets added to the system

        FORMATTING OF DATA:
        LABEL {
          <Title>, <COLOR>
          ...
        }
        CHECKLIST {
          <ITEM0>, <ITEM1>, ...
          ...
        }
        TASK {
          <NAME>, <DUE_DATE>, <TOTAL_HR>, <USED_HR>, CL#, L#
          ...
        }
        CARD {
          <Title>, T#, L#
          ...
        }
        DAY {
          T0 4, T3 2, T1 4
          ...
        }

        NOTE: Try working from bottom to top (might be able to save on efficiency and storage)
         */
        Calendar calendar = Time.getFormattedCalendarInstance(0);
        StringBuilder calendarSB = new StringBuilder();
        if(calendar.get(Calendar.DAY_OF_MONTH) < 10) {
            calendarSB.append("0").append(calendar.get(Calendar.DAY_OF_MONTH));
        } else {
            calendarSB.append(calendar.get(Calendar.DAY_OF_MONTH));
        }
        calendarSB.append("-");
        if(calendar.get(Calendar.MONTH) < 10) {
            calendarSB.append("0").append(calendar.get(Calendar.MONTH));
        } else {
            calendarSB.append(calendar.get(Calendar.MONTH));
        }
        calendarSB.append("-").append(calendar.get(Calendar.YEAR)).append("\n\n");
        StringBuilder cardSB = new StringBuilder();
        List<Task> taskList = new ArrayList<>();
        List<Label> labelList = new ArrayList<>();
        List<CheckList> checkListList = new ArrayList<>();
        if(!cards.isEmpty()) {
            cardSB.append("CARD {\n");
            for(Card c : cards) {
                String title = c.getTitle();
                cardSB.append("  ").append(title);
                for(Task t : c.getTask()) {
                    if(!taskList.contains(t)) {
                        taskList.add(t);
                        cardSB.append(", T").append(taskList.size() - 1);
                    } else {
                        cardSB.append(", T").append(taskList.indexOf(t));
                    }
                }
                for(Label l : c.getLabel()) {
                    if(!labelList.contains(l)) {
                        labelList.add(l);
                        cardSB.append(", L").append(labelList.size() - 1);
                    } else {
                        cardSB.append(", L").append(labelList.indexOf(l));
                    }
                }
                cardSB.append("\n");
            }
            cardSB.append("}\n");
        }
        StringBuilder taskSB = new StringBuilder();
        if(!taskList.isEmpty()) {
            taskSB.append("TASK {\n");
            for(Task t : taskList) {
                String name = t.getName();
                int totalHours = t.getTotalHours();
                //int remainingHours = t.getSubTotalHoursRemaining(); //TODO need to verify this
                taskSB.append("  ")
                        .append(name)
                        .append(", ")
                        .append(totalHours)
                        .append(", ")
                        .append(Time.determineRangeOfDays(calendar, t.getDueDate()));
                for(Label l : t.getLabel()) {
                    if(!labelList.contains(l)) {
                        labelList.add(l);
                        taskSB.append(", L").append(labelList.size() - 1);
                    } else {
                        taskSB.append(", L").append(labelList.indexOf(l));
                    }
                }
                CheckList cl = t.getCheckList();
                if(cl != null) {
                    checkListList.add(cl);
                    taskSB.append(", CL").append(checkListList.size() - 1);
                }
                taskSB.append("\n");
            }
            taskSB.append("}\n");
        }
        StringBuilder labelSB = new StringBuilder();
        if(!labelList.isEmpty()) {
            labelSB.append("LABEL {\n");
            for(Label l : labelList) {
                labelSB.append("  ")
                        .append(l.getName())
                        .append(", ")
                        .append(l.getColor())
                        .append("\n");
            }
            labelSB.append("}\n\n");
        }
        StringBuilder clSB = new StringBuilder();
        if(!checkListList.isEmpty()) {
            clSB.append("CHECKLIST {\n");
            for(CheckList cl : checkListList) {
                clSB.append("  ").append(cl.getName());
                for(CheckList.Item i : cl.getItems()) {
                    clSB.append(", ").append(i);
                }
                clSB.append("\n");
            }
            clSB.append("}\n\n");
        }
        StringBuilder daySB = new StringBuilder();
        ScheduleManager sm = ScheduleManager.getScheduleManager();
        if(!sm.scheduleIsEmpty()) {
            daySB.append("DAY {\n"); //TODO need to include the date at the beginning of each line (also, how are we going to sync schedule with export)
            for(Day d : sm.getSchedule()) {
                boolean flag = false;
                daySB.append("  ");
                for(Task.SubTask st : d.getSubTasks()) {
                    if(flag) {
                        daySB.append(", ");
                    }
                    Task task = st.getParentTask();
                    int i = 0;
                    for(Task t : taskList) {
                        i++;
                        if(task == t) {
                            break;
                        }
                    }
                    daySB.append("T")
                            .append(i)
                            .append(" ")
                            .append(st.getSubTaskHours());
                    flag = true;
                }
                daySB.append("\n");
            }
            daySB.append("}\n");
        }
        //now go from top to bottom with all the data you now have
        return calendarSB.append(labelSB)
                .append(clSB)
                .append(taskSB)
                .append("\n")
                .append(cardSB)
                .append("\n")
                .append(daySB)
                .toString();
    }

    /**
     * Processes the Java Binary Serialization string to update the current system
     *
     * @param data JBin string being processed
     * @param tasks Tasks holder
     * @param cards Cards holder
     * @param labels Labels holder
     */
    public static void processJBin(String data, PriorityQueue<Task> tasks, List<Card> cards, List<Label> labels) {
        //NOTE: When processing, you should work from top to bottom (use ArrayLists to easily locate data by index value)
        Scanner jbinScanner = new Scanner(data);
        LocalDate ld = null;
        if(jbinScanner.hasNextLine()) {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            ld = LocalDate.parse(jbinScanner.nextLine(), df);
        }
        Calendar calendar = Time.getFormattedCalendarInstance(0);
        assert ld != null;
        int[] monthSet = {
                Calendar.JANUARY,
                Calendar.FEBRUARY,
                Calendar.MARCH,
                Calendar.APRIL,
                Calendar.MAY,
                Calendar.JUNE,
                Calendar.JULY,
                Calendar.AUGUST,
                Calendar.SEPTEMBER,
                Calendar.OCTOBER,
                Calendar.NOVEMBER,
                Calendar.DECEMBER};

        //todo codeblock needs to be refactored and tested further
        int x = ld.getDayOfMonth(); //debugging values
        calendar = Calendar.getInstance();
        calendar.set(ld.getYear(), Calendar.DECEMBER, ld.getDayOfMonth());
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        //todo codeblock needs to be refactored and tested further


        boolean labelOpen = false;
        boolean labelClosed = false;
        boolean checklistOpen = false;
        boolean checklistClosed = false;
        boolean taskOpen = false;
        boolean taskClosed = false;
        boolean cardOpen = false;
        boolean cardClosed = false;
        List<CheckList> checkLists = new ArrayList<>();
        List<Task> taskList = new ArrayList<>();
        while(jbinScanner.hasNextLine()) {
            String type = jbinScanner.nextLine();
            String[] tokens = type.split("\\s");
            if(!labelOpen && tokens.length == 2 && "LABEL".equals(tokens[0]) && "{".equals(tokens[1])) {
                labelOpen = true;
                while(jbinScanner.hasNextLine()) {
                    type = jbinScanner.nextLine();
                    tokens = type.split(",");
                    if("}".equals(tokens[0].trim()) && tokens.length == 1) {
                        labelClosed = true;
                        break;
                    } else if(tokens.length == 2) {
                        labels.add(new Label(labels.size(), tokens[0].trim(), Integer.parseInt(tokens[1].trim())));
                    } else {
                        throw new InputMismatchException();
                    }
                }
                if(!labelClosed) {
                    throw new IllegalArgumentException();
                }
            } else if(!checklistOpen && tokens.length == 2 && "CHECKLIST".equals(tokens[0]) && "{".equals(tokens[1])) {
                checklistOpen = true;
                while(jbinScanner.hasNextLine()) {
                    type = jbinScanner.nextLine();
                    tokens = type.split(",");
                    if(tokens.length == 0) {
                        throw new InputMismatchException();
                    } else if("}".equals(tokens[0].trim()) && tokens.length == 1) {
                        checklistClosed = true;
                        break;
                    } else if(tokens.length == 1) {
                        checkLists.add(new CheckList(checkLists.size(), tokens[0].trim()));
                    } else {
                        checkLists.add(new CheckList(checkLists.size(), tokens[0].trim()));
                        int itemId = 0;
                        for(int i = 1; i < tokens.length; i++) {
                            String item = tokens[i];
                            boolean complete = false;
                            if(item.charAt(item.length() - 1) == '✅') {
                                complete = true;
                                item = item.substring(0, item.length() - 1);
                            }
                            checkLists.get(checkLists.size() - 1).addItem(item.trim());
                            checkLists.get(checkLists.size() - 1).markItemById(itemId++, complete);
                        }
                    }
                }
                if(!checklistClosed) {
                    throw new IllegalArgumentException();
                }
            } else if(!taskOpen && tokens.length == 2 && "TASK".equals(tokens[0]) && "{".equals(tokens[1])) {
                taskOpen = true;
                while(jbinScanner.hasNextLine()) {
                    type = jbinScanner.nextLine();
                    tokens = type.split(",");
                    if(tokens.length == 0) {
                        throw new InputMismatchException();
                    } else if("}".equals(tokens[0].trim()) && tokens.length == 1) {
                        taskClosed = true;
                        break;
                    } else if(tokens.length == 3) { //todo will need to run quick check to see if task is within archive range (if not, don't add to Card or TaskManager)
                        taskList.add(new Task(taskList.size(), tokens[0].trim(), Integer.parseInt(tokens[1].trim()),
                                Time.getFormattedCalendarInstance(calendar, Integer.parseInt(tokens[2].trim()))));
                    } else if(tokens.length > 3) {
                        taskList.add(new Task(taskList.size(), tokens[0].trim(), Integer.parseInt(tokens[1].trim()),
                                Time.getFormattedCalendarInstance(calendar, Integer.parseInt(tokens[2].trim()))));
                        for(int i = 3; i < tokens.length; i++) {
                            String item = tokens[i].trim();
                            if(item.length() > 2 && item.charAt(0) == 'C' && item.charAt(1) == 'L') {
                                taskList.get(taskList.size() - 1).addCheckList(checkLists.get(Integer.parseInt(item.substring(2))));
                            } else if(item.length() > 1 && item.charAt(0) == 'L') {
                                taskList.get(taskList.size() - 1).addLabel(labels.get(Integer.parseInt(item.substring(1))));
                            } else {
                                throw new InputMismatchException();
                            }
                        }
                    } else {
                        throw new InputMismatchException();
                    }
                }
                if(!taskClosed) {
                    throw new IllegalArgumentException();
                }
            } else if(!cardOpen && tokens.length == 2 && "CARD".equals(tokens[0]) && "{".equals(tokens[1])) {
                cardOpen = true;
                while(jbinScanner.hasNextLine()) {
                    type = jbinScanner.nextLine();
                    tokens = type.split(",");
                    if(tokens.length == 0) {
                        throw new InputMismatchException();
                    } else if("}".equals(tokens[0].trim()) && tokens.length == 1) {
                        cardClosed = true;
                        break;
                    } else if(tokens.length == 1) {
                        cards.add(new Card(tokens[0].trim())); //todo need to add id eventually
                    } else {
                        cards.add(new Card(tokens[0].trim())); //todo need to add id eventually
                        for(int i = 1; i < tokens.length; i++) {
                            String item = tokens[i].trim();
                            if(item.length() > 1 && item.charAt(0) == 'T') {
                                cards.get(cards.size() - 1).addTask(taskList.get(Integer.parseInt(item.substring(1))));
                            } else if(item.length() > 1 && item.charAt(0) == 'L') {
                                cards.get(cards.size() - 1).addLabel(labels.get(Integer.parseInt(item.substring(1))));
                            } else {
                                throw new InputMismatchException();
                            }
                        }
                    }
                }
                if(!cardClosed) {
                    throw new IllegalArgumentException();
                }
            } else if(!"".equals(type)) {
                throw new InputMismatchException();
            }
        }
        tasks.addAll(taskList);
        //todo need to remove old tasks that are beyond the user config archive date (will store null temporarily inside the tasklist and not add said tasks to cards)
//        State.addAllLabels(labels);
//        State.addAllCheckLists(checkLists);
//        State.addAllTasks(taskList);
//        State.addAllCards(cards);
    }
}
