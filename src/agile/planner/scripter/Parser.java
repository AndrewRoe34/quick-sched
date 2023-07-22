package agile.planner.scripter;

import agile.planner.data.Card;
import agile.planner.data.Label;
import agile.planner.data.Task;
import agile.planner.scripter.exception.InvalidFunctionException;
import agile.planner.scripter.exception.InvalidGrammarException;
import agile.planner.scripter.exception.InvalidPairingException;
import agile.planner.scripter.exception.UnknownClassException;
import agile.planner.util.CheckList;

import java.util.*;

public class Parser {

    private List<Type> variables = new ArrayList<>();

    public void parsePreProcessor(String line) {
        String[] tokens = line.split(",");

        String[] func = tokens[0].split(":");
        if("include:".equals(func[0].trim())) {
            //code below
            boolean config = false;
            boolean debug = false;
            boolean log = false;
            boolean imprt = false;
            boolean exprt = false;
            boolean build = false;
            boolean stats = false;
            for(int i = 1; i < tokens.length; i++) {
                switch(tokens[i]) {
                    case "__CURR_CONFIG__":
                        if(config) {
                            //throw exception
                        }
                        config = true;
                        break;
                    case "__DEF_CONFIG__":
                        if(config) {
                            //throw exception
                        }
                        config = true;
                    case "__DEBUG__":
                        if(debug) {
                            //throw exception
                        }
                        debug = true;
                    case "__LOG__":
                        if(log) {
                            //throw exception
                        }
                        log = true;
                    case "__IMPORT__":
                        if(imprt) {
                            //throw exception
                        }
                        imprt = true;
                    case "__EXPORT__":
                        if(exprt) {
                            //throw exception
                        }
                        exprt = true;
                    case "__BUILD__":
                        if(build) {
                            //throw exception
                        }
                        build = true;
                    case "__STATS__":
                        if(stats) {
                            //throw exception
                        }
                        stats = true;
                    default:
                        //throw exception here
                }
            }
        }
    }

    public void parseCallCustomFunc(String line) {
        /*
        1. First line represents the parameters
        2. The remaining string is for the code being executed (or ignored if a comment)

        Example:
        Map<"foo:", "task, card\nadd: task, card">
        task, card      <-- Match with given input
        add: task, card <-- Call with given input
        ...

        Steps for how to solve:
        (ask me if you'd like a hint)
         */

        /*
         * 1. Process the arguments to ensure that they are valid
         * 2. Update a boolean value that resembles "inside_func" to allow ease of code calling
         *    -OR, we process it here
         * 3. Store the values inside the associated global function variables
         * 4. Call each function with the correct variables (will need boolean variable to make this easier)
         */


        //process arguments TODO (and ensure they match with parameters)
        String[] args = processArguments(line, 5, ",");
        for (String arg : args) {
            if (arg == null) {
                break;
            }
            String[] tokens = processTokens(arg, 2, null);
            switch (tokens[0]) {
                case "_task":
                    if (tokens[1] == null) {
                        tasks = new ArrayList<>();
                        tasks.add(taskList.get(taskList.size() - 1));
                    } else {
                        int idx = Integer.parseInt(tokens[1]);
                        tasks = new ArrayList<>();
                        tasks.add(taskList.get(idx));
                    }
                    break;
                case "task":
                    if (tokens[1] == null) {
                        tasks = taskList;
                    } else {
                        throw new InvalidFunctionException();
                    }
                    break;
                case "_checklist":
                    if (tokens[1] == null) {
                        checkLists = new ArrayList<>();
                        checkLists.add(clList.get(clList.size() - 1));
                    } else {
                        int idx = Integer.parseInt(tokens[1]);
                        checkLists = new ArrayList<>();
                        checkLists.add(clList.get(idx));
                    }
                    break;
                case "checklist":
                    if (tokens[1] == null) {
                        checkLists = clList;
                    } else {
                        throw new InvalidFunctionException();
                    }
                    break;
                case "_card":
                    if (tokens[1] == null) {
                        cards = new ArrayList<>();
                        cards.add(cardList.get(cardList.size() - 1));
                    } else {
                        int idx = Integer.parseInt(tokens[1]);
                        cards = new ArrayList<>();
                        cards.add(cardList.get(idx));
                    }
                    break;
                case "card":
                    if (tokens[1] == null) {
                        cards = cardList;
                    } else {
                        throw new InvalidFunctionException();
                    }
                    break;
                case "_label":
                    if (tokens[1] == null) {
                        labels = new ArrayList<>();
                        labels.add(labelList.get(labelList.size() - 1));
                    } else {
                        int idx = Integer.parseInt(tokens[1]);
                        labels = new ArrayList<>();
                        labels.add(labelList.get(idx));
                    }
                    break;
                case "label":
                    if (tokens[1] == null) {
                        labels = labelList;
                    } else {
                        throw new InvalidFunctionException();
                    }
                    break;
                default:
                    throw new InvalidFunctionException();
            }
        } //TODO need to add "board" to the above argument processing

        Scanner strScanner = new Scanner(line);
        String script = funcMap.get(strScanner.next());
        Scanner funcScanner = new Scanner(script);
        funcScanner.nextLine(); //skips the function definition and parameters
        while(funcScanner.hasNextLine()) {
            String statement = funcScanner.nextLine();
            //process each line of code while making function calls here
            parseFunction(statement);
        }

        scriptLog.reportFunctionCall(line);

        //reset all the variables
        resetVariables();
    }

    private boolean parseFunction(String line) { //todo might not need this actually
        Scanner tokenScanner = new Scanner(line);
        String type = tokenScanner.next();
        switch(type) {
            case "print:":
                new PrintState().processFunc(line);
                break;
            case "add:":
                return parseAddFunc(line);
            case "edit:":
                new EditState().processFunc(line);
                break;
            case "remove:":
                new RemoveState().processFunc(line);
                break;
            case "task:":
            case "label:":
            case "day:":
            case "checklist:":
            case "card:":
            case "board:":
            default:
                throw new InvalidFunctionException();
            default:
                if(funcMap.containsKey(type)) {
                    new FunctionState().processFunc(line);
                } else if(type.charAt(0) != '#') {
                    throw new UnknownClassException();
                }
                break;
        }
    }

    public void parseIf(String line) {
        String[] tokens = line.split("and | or");
        //if(true and false or true)
        //  code

        /*
        if: 1 or 0 and 1 and 1
        --> 1 or 0
        --> 1
        push 1 since the next operation is an "or"
        loop since the next operation after 0 is an and
          return value is 0
        return 1 or 0
         */
    }

    public void parseForEach(String line) {
        String[] tokens = processArguments(line, 3, null);

        //how to reference a type:
        //  1. Holds a class: Task<name, hours, due_date>
        //  2. Has a name:    "t1"
        //
        //need a way to look up variable quickly (will not worry about locality of variables for the moment)
        //TODO we could use a heap to represent variables (offers O(logn) lookup and O(logn) removal/insertion)
    }

    public boolean parseAddFunc(String line) {
        String[] tokens = processArguments(line, 2, ",");
        Type[] types = lookupVariablePair(tokens[0], tokens[1]);
        if(types == null) {
            throw new InvalidGrammarException();
        }

        /*
            0. Task
            1. Label
            2. Checklist
            3. Card
            4. Board
            5. List<Task>
            6. List<Label>
            7. List<CheckList>
            8. List<Card>
            9. List<Board>
            10. Item
            11. List<Item>
            12. String
            13. Integer
            14. Boolean


            1. Find out if variable exists
            2. Determine the type and cast it to another variable
            3. Perform the addition operation
     */

        boolean flag = true;
        switch(types[1].getVariableIdx()) {
            case 0:
                Task task = (Task) types[1].getDatatype();
                switch(types[0].getVariableIdx()) {
                    case 1:
                        return task.addLabel((Label) types[0].getDatatype());
                    case 2:
                        return task.addCheckList((CheckList) types[0].getDatatype());
                    case 6:
                        return task.addLabelList((List<Label>) types[0].getDatatype());
                    default:
                        throw new InvalidPairingException();
                }
            case 2:
                CheckList checkList = (CheckList) types[1].getDatatype();
                switch(types[0].getVariableIdx()) {
                    case 10:
                        return checkList.addItem((CheckList.Item) types[0].getDatatype());
                    case 11:
                        return checkList.addItemList((List<CheckList.Item>) types[0].getDatatype());
                    case 12:
                        return checkList.addItem((String) types[0].getDatatype());
                    default:
                        throw new InvalidPairingException();
                }
            case 3:
                Card card = (Card) types[1].getDatatype();
                switch(types[0].getVariableIdx()) {
                    case 1:
                        return card.addLabel((Label) types[0].getDatatype());
                    case 6:
                        return card.addLabelList((List<Label>) types[0].getDatatype());
                    default:
                        throw new InvalidPairingException();
                }
            case 4:
                //todo will add once Board is finally complete
                break;
            case 5:
                List<Task> tasks = (List<Task>) types[1].getDatatype();
                switch(types[0].getVariableIdx()) {
                    case 1:
                        for(Task t : tasks) {
                            flag &= t.addLabel((Label) types[0].getDatatype());
                        }
                        return flag;
                    case 2:
                        for(Task t : tasks) {
                            flag &= t.addCheckList((CheckList) types[0].getDatatype());
                        }
                        return flag;
                    case 6:
                        for(Task t : tasks) {
                            flag &= t.addLabelList((List<Label>) types[0].getDatatype());
                        }
                        return flag;
                    default:
                        throw new InvalidPairingException();
                }
            case 7:
                List<CheckList> checkLists = (List<CheckList>) types[1].getDatatype();
                switch(types[0].getVariableIdx()) {
                    case 10:
                        for(CheckList cl : checkLists) {
                            flag &= cl.addItem((CheckList.Item) types[0].getDatatype());
                        }
                        return flag;
                    case 11:
                        for(CheckList cl : checkLists) {
                            flag &= cl.addItemList((List<CheckList.Item>) types[0].getDatatype());
                        }
                        return flag;
                    case 12:
                        for(CheckList cl : checkLists) {
                            flag &= cl.addItem((String) types[0].getDatatype());
                        }
                        return flag;
                    default:
                        throw new InvalidPairingException();
                }
            case 8:
                List<Card> cards = (List<Card>) types[1].getDatatype();
                switch(types[0].getVariableIdx()) {
                    case 1:
                        for(Card c : cards) {
                            flag &= c.addLabel((Label) types[0].getDatatype());
                        }
                        return flag;
                    case 6:
                        for(Card c : cards) {
                            flag &= c.addLabelList((List<Label>) types[0].getDatatype());
                        }
                        return flag;
                    default:
                        throw new InvalidPairingException();
                }
            case 9:
                //todo will do once board is finished
                break;
            default:
                throw new InvalidPairingException();
        }
        return false;
    }

    private Type lookupVariable(String token) {
        for(Type t : variables) {
            if(token.equals(t.getVariableName())) {
                return t;
            }
        }
        return null;
    }

    private Type[] lookupVariablePair(String token1, String token2) {
        Type[] types = new Type[2];
        for(Type t : variables) {
            if (token1.equals(t.getVariableName())) {
                types[0] = t;
            } else if(token2.equals(t.getVariableName())) {
                types[1] = t;
            }
        }
        return types[0] != null && types[1] != null ? types : null;
    }

    public void parsePrintFunc(String line) {
        String[] tokens = line.split(","); //todo need to look up regex so that comma is not within quotes or parentheses
        /*
        1. Verify if a variable (if so, call toString() method)
        2. Verify if proper string (if so, utilize it)
        3. Use System.out.print() with each string token
        4. If call is "print:", do not use a new line at the end
        5. If call is "println:", use a new line at the end
         */
        for(int i = 1; i < tokens.length; i++) {
            Type t1 = lookupVariable(tokens[i]);
            if(t1 == null) {
                //todo process it as a string
                System.out.print(verifyString(tokens[i]));
            } else {
                System.out.print(t1.toString());
            }
        }
        if("println:".equals(tokens[0])) { //todo this will always fail (need to break up token[0] by ":"
            System.out.println("\n");
        }
    }

    /**
     * Parses string to create an instance of Card.<p>
     * Example:
     * <blockquote><pre>
     *     c1: card("name")
     * </pre></blockquote>
     * @param line code being processes
     */
    public Card parseCard(String line) {
        String[] tokens = line.split(",");
        if(tokens.length > 1) {
            throw new InvalidFunctionException();
        }
        Card c = new Card(tokens[0]);

        //todo need to have id for Card
        //todo need to log this data

        return c;



//        String[] tokens = processArguments(line, 1, ",");
//        try {
//            int id = scheduleManager.getLastCardId() + cardList.size();
//            String name = tokens[0];
//            cardList.add(new Card(name));
//            scriptLog.reportCardCreation(id, name);
//            System.out.println("Card added.. [C" + (scheduleManager.getLastCardId() + cardList.size() - 1) + "]");
//        } catch(Exception e) {
//            throw new InvalidGrammarException("Invalid input. Expected[card: <title: string>]");
//        }
    }

    public void parseCheckList(String line) {
        String[] tokens = processArguments(line, 1, ",");
        try {
            int id = scheduleManager.getLastCLId() + clList.size();
            String name = tokens[0];
            clList.add(new CheckList(id, name));
            scriptLog.reportCheckListCreation(id, name);
            System.out.println("Checklist added.. [C" + (scheduleManager.getLastCLId() + clList.size() - 1) + "]");
        } catch(Exception e) {
            throw new InvalidGrammarException("Invalid input. Expected[checklist: <name: string>]");
        }
    }

    public void parseLabel(String line) {
        String[] tokens = processArguments(line, 2, ",");
        try {
            int id = scheduleManager.getLastLabelId() + labelList.size();
            String name = tokens[0];
            int color = Integer.parseInt(tokens[1]);
            labelList.add(new Label(id, name, color));
            scriptLog.reportLabelCreation(id, name, color);
            System.out.println("Label added.. [L" + (scheduleManager.getLastLabelId() + labelList.size() - 1) + "]");
        } catch(Exception e) {
            throw new InvalidGrammarException("Invalid input. Expected[label: <name: string>, <color: int>]");
        }
    }

    public void parseTask(String line) {
        String[] tokens = processArguments(line, 3, ",");
        try {
            int id = scheduleManager.getLastTaskId() + taskList.size();
            int hours = Integer.parseInt(tokens[1]);
            int days = Integer.parseInt(tokens[2]);
            Task t = new Task(id, tokens[0], hours, days);
            taskList.add(t);
            scriptLog.reportTaskCreation(id, tokens[0], hours, days);
            System.out.println("Task added.. [T" + (scheduleManager.getLastTaskId() + taskList.size() - 1) + "]");
            //adds task to default card
            cardList.get(0).addTask(t);
        } catch(Exception e) {
            throw new InvalidGrammarException("Invalid input. Expected[task: <name: string>, <hours: int>, <num_days: int>]");
        }
    }

    private String verifyString(String line) {
        boolean quoteBegin = false;
        boolean quoteEnd = false;
        int beginIdx = 0;
        int endIdx = 0;
        for(int i = 0; i < line.length(); i++) {
            if(line.charAt(i) == ' ' || line.charAt(i) == '\t') {
                beginIdx++;
            } else if(line.charAt(i) == 'p') break;
        }
        beginIdx += 6;
        for(int i = beginIdx; i < line.length(); i++) {
            if(!quoteBegin && line.charAt(i) == '"') {
                quoteBegin = true;
                beginIdx = i;
            } else if(!quoteEnd && line.charAt(i) == '"') {
                quoteEnd = true;
                endIdx = i;
            } else if(!quoteBegin && line.charAt(i) != ' ' && line.charAt(i) != '\t') {
                return null;
            } else if(quoteEnd && line.charAt(i) != ' ' && line.charAt(i) != '\t' &&
                    line.charAt(i) != '\n' && line.charAt(i) != '\r') {
                return null;
            }
        }
        if(beginIdx >= endIdx) return null;
        return line.substring(beginIdx + 1, endIdx);
    }

    private String[] processArguments(String line, int maxArgs, String delimiter) {
        String[] tokens = new String[maxArgs];
        Scanner strScanner = new Scanner(line);
        strScanner.next();
        if(delimiter != null) {
            strScanner.useDelimiter(delimiter);
        }
        int i = 0;
        while(strScanner.hasNext()){
            if(i < maxArgs) {
                tokens[i++] = strScanner.next().trim();
            } else {
                throw new InvalidGrammarException();
            }
        }
        return tokens;
    }

    /**
     * Processes all the tokens in a given string provided by its delimiter
     *
     * @param line string being processed
     * @param numTokens maximum number of tokens possible
     * @param delimiter pattern for separating tokens
     * @return string array of all arguments
     */
    private String[] processTokens(String line, int numTokens, String delimiter) {
        String[] tokens = new String[numTokens];
        Scanner strScanner = new Scanner(line);
        if(delimiter != null) {
            strScanner.useDelimiter(delimiter);
        }
        int i = 0;
        while(strScanner.hasNext()){
            if(i < numTokens) {
                tokens[i++] = strScanner.next().trim();
            } else {
                throw new InvalidGrammarException();
            }
        }
        return tokens;
    }
}
