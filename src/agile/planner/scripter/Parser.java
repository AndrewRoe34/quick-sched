package agile.planner.scripter;

import agile.planner.data.*;
import agile.planner.scripter.exception.DereferenceNullException;
import agile.planner.scripter.exception.InvalidGrammarException;
import agile.planner.scripter.exception.InvalidPairingException;
import agile.planner.util.CheckList;

import java.util.*;

public class Parser {

    private List<Type> variables = new ArrayList<>();
    private int varNameLength;
    private int constructIdx;
    private static final String FUNC_REGEX = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

    public enum Operation {
        FUNCTION,
        INSTANCE,
        ATTRIBUTE,
        PRE_PROCESSOR
    }

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
//        String[] args = processArguments(line, 5, ",");
//        for (String arg : args) {
//            if (arg == null) {
//                break;
//            }
//            String[] tokens = processTokens(arg, 2, null);
//            switch (tokens[0]) {
//                case "_task":
//                    if (tokens[1] == null) {
//                        tasks = new ArrayList<>();
//                        tasks.add(taskList.get(taskList.size() - 1));
//                    } else {
//                        int idx = Integer.parseInt(tokens[1]);
//                        tasks = new ArrayList<>();
//                        tasks.add(taskList.get(idx));
//                    }
//                    break;
//                case "task":
//                    if (tokens[1] == null) {
//                        tasks = taskList;
//                    } else {
//                        throw new InvalidFunctionException();
//                    }
//                    break;
//                case "_checklist":
//                    if (tokens[1] == null) {
//                        checkLists = new ArrayList<>();
//                        checkLists.add(clList.get(clList.size() - 1));
//                    } else {
//                        int idx = Integer.parseInt(tokens[1]);
//                        checkLists = new ArrayList<>();
//                        checkLists.add(clList.get(idx));
//                    }
//                    break;
//                case "checklist":
//                    if (tokens[1] == null) {
//                        checkLists = clList;
//                    } else {
//                        throw new InvalidFunctionException();
//                    }
//                    break;
//                case "_card":
//                    if (tokens[1] == null) {
//                        cards = new ArrayList<>();
//                        cards.add(cardList.get(cardList.size() - 1));
//                    } else {
//                        int idx = Integer.parseInt(tokens[1]);
//                        cards = new ArrayList<>();
//                        cards.add(cardList.get(idx));
//                    }
//                    break;
//                case "card":
//                    if (tokens[1] == null) {
//                        cards = cardList;
//                    } else {
//                        throw new InvalidFunctionException();
//                    }
//                    break;
//                case "_label":
//                    if (tokens[1] == null) {
//                        labels = new ArrayList<>();
//                        labels.add(labelList.get(labelList.size() - 1));
//                    } else {
//                        int idx = Integer.parseInt(tokens[1]);
//                        labels = new ArrayList<>();
//                        labels.add(labelList.get(idx));
//                    }
//                    break;
//                case "label":
//                    if (tokens[1] == null) {
//                        labels = labelList;
//                    } else {
//                        throw new InvalidFunctionException();
//                    }
//                    break;
//                default:
//                    throw new InvalidFunctionException();
//            }
//        } //TODO need to add "board" to the above argument processing

//        Scanner strScanner = new Scanner(line);
//        String script = funcMap.get(strScanner.next());
//        Scanner funcScanner = new Scanner(script);
//        funcScanner.nextLine(); //skips the function definition and parameters
//        while(funcScanner.hasNextLine()) {
//            String statement = funcScanner.nextLine();
//            //process each line of code while making function calls here
//            parseFunction(statement);
//        }
//
//        scriptLog.reportFunctionCall(line);
//
//        //reset all the variables
//        resetVariables();
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

    public Type[] parseForEach(String line) {
        String[] tokens = processTokens(line, 4, null);
        if(!"foreach".equals(tokens[0]) && !"in".equals(tokens[2])) {
            throw new InvalidGrammarException();
        }
        return lookupVariablePair(tokens[1], tokens[3]);

        //how to reference a type:
        //  1. Holds a class: Task<name, hours, due_date>
        //  2. Has a name:    "t1"
        //
        //need a way to look up variable quickly (will not worry about locality of variables for the moment)
        //TODO we could use a heap to represent variables (offers O(logn) lookup and O(logn) removal/insertion)
    }

    public Type lookupVariable(String token) {
        for(Type t : variables) {
            if(token.equals(t.getVariableName())) {
                return t;
            }
        }
        return null;
    }

    public Type[] lookupVariablePair(String token1, String token2) {
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
        /*
        1. Verify if a variable (if so, call toString() method)
        2. Verify if proper string (if so, utilize it)
        3. Use System.out.print() with each string token
        4. If call is "print:", do not use a new line at the end
        5. If call is "println:", use a new line at the end
         */
        String trimmedStr = line.trim();
        int isValidPrint = trimmedStr.indexOf("print(");
        int isValidPrintln = trimmedStr.indexOf("println(");
        if(isValidPrint != 0 && isValidPrintln != 0) {
            throw new InvalidGrammarException();
        }
        boolean println = isValidPrintln == 0;
        if(trimmedStr.charAt(trimmedStr.length() - 1) != ')') {
            throw new InvalidGrammarException();
        }
        trimmedStr = println ? trimmedStr.substring(8, trimmedStr.length() - 1) : trimmedStr.substring(6, trimmedStr.length() - 1);
        String[] tokens = trimmedStr.split(FUNC_REGEX, -1);
        for(int i = 0; i < tokens.length; i++) {
            Type t1 = lookupVariable(tokens[i].trim());
            if(t1 == null) {
                System.out.print(verifyString(tokens[i].trim()));
            } else {
                System.out.print(t1.toString());
            }
        }
        if(println) {
            System.out.println();
        }
    }

    public Type getVariable(int idx) {
        return variables.get(idx);
    }

    /**
     * Parses string to create an instance of Card.<p>
     * Example:
     * <blockquote><pre>
     *     c1: card("name")
     * </pre></blockquote>
     * @param line code being processes
     */
    public Card parseCardOld(String line) {
        String varName = processVariable(line);
        if(varName == null) return null;
        if(!processType(line, 3, varNameLength)) return null;
        String argumentSet = processConstructor(line, constructIdx);
        assert argumentSet != null;
        String[] tokens = argumentSet.split(",");
        if(tokens.length != 1) {
            throw new InvalidGrammarException();
        }
        Card c1 = new Card(tokens[0]);
        Type t1 = lookupVariable(varName);
        if(t1 == null) {
            variables.add(new Type(c1, varName, Type.TypeId.CARD));
        } else {
            t1.setDatatype(c1, Type.TypeId.CARD);
        }
        return c1;

        //todo need to have id for Card
        //todo need to log this data



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

    private boolean processType(String line, int type, int startIdx) {
        int i = startIdx;
        for(; i < line.length(); i++) {
            if(line.charAt(i) != ' ' && line.charAt(i) != '\t')
                break;
        }
        int endIdx = i;
        for(; endIdx < line.length(); endIdx++) {
            if(!Character.isAlphabetic(line.charAt(endIdx)))
                break;
        }
        constructIdx = endIdx;
        if(endIdx > line.length() || line.charAt(endIdx) != '(')
            return false;
        String codeword = line.substring(i, endIdx);
        switch(codeword) {
            case "card":
                return type == 3;
            case "task":
                return type == 0;
            case "label":
                return type == 1;
            case "checklist":
                return type == 2;
            default:
                return false;
        }
    }

    public String processVariable(String line) {
        int startIdx = 0;
        for(; startIdx < line.length(); startIdx++) {
            if(line.charAt(startIdx) != ' ' && line.charAt(startIdx) != '\t')
                break;
        }
        int endIdx = startIdx;
        for(; endIdx < line.length(); endIdx++) {
            if(Character.isAlphabetic(line.charAt(endIdx)) && Character.isDigit(line.charAt(endIdx))) {
                continue;
            } else if(line.charAt(endIdx) == ':' || line.charAt(endIdx) == ' ' || line.charAt(endIdx) == '\t'
                    || line.charAt(endIdx) == '_' && endIdx + 1 < line.length() && line.charAt(endIdx + 1) == '_')
                break;
        }
        varNameLength = endIdx - startIdx + 1;
        return endIdx > startIdx && line.charAt(endIdx) == ':'
                && (Character.isAlphabetic(line.charAt(endIdx - 1)) || Character.isDigit(line.charAt(endIdx - 1)))
                ? line.substring(startIdx, endIdx) : null;
    }









    public Object parseAttr(String line) {
        /*
        String: c1.title
        Tokenize by '.': [c1, title]
        Lookup c1 for actual variable reference
            If exists, continue
            Else, null pointer
        Tokenize by ',' for attr operation: []
        Use switch logic to find correct enum value for attr operation
        Call 'attrSet' with enum value and array of arguments

        t1.label: COLOR, 3
        vs
        t1.edit_label(COLOR, 3)
         */
        String[] tokens = line.split("\\."); //todo will need to update so it doesn't include . inside " "
        Type t1 = lookupVariable(tokens[0].trim());
        if(t1 == null) throw new DereferenceNullException();
        DataAttr attr = null;
        switch(tokens[1]) {
            case "title":
                attr = DataAttr.GET_TITLE;
                break;
        }
        if(attr == null) throw new DereferenceNullException();
        return t1.attrSet(attr, null);
    } //todo need to create another method that involves returning an Attribute instance
















    // if it ends with ':', it's variable assignment
    // if it ends with ')', it's a function or attribute

    // class instance assignment  [DONE]
    // attribute handling         [DONE]
    // function calls             [DONE]
    // String/int/bool assignment [TBD]
    // Preprocessor handling      [TBD]
    // Custom function            [TBD]
    // Calling custom function    [TBD]








    public Operation typeOfOperation(String line) {

        return null;
    }


    //todo need to include whatever data is available when possible (allows us to throw exceptions such as DereferenceNullPointer)


    public StaticFunction parseStaticFunction(String line) { //todo need to allow for custom functions as well
        int startIdx = skipWhiteSpace(line, 0);
        int endIdx = startIdx;
        for(; endIdx < line.length(); endIdx++) {
            if(line.charAt(endIdx) == '(')
                break;
        }
        String funcName = line.substring(startIdx, endIdx);
        switch(funcName) {
            case "print":
            case "build":
            case "import":
            case "export":
                String arguments = verifyArgument(line, endIdx);
                if(arguments == null) return null;
                return new StaticFunction(funcName, arguments.split(FUNC_REGEX, -1));
            default:
                return null;
        }
    }

    public Attributes parseAttributes(String line) {
        int startIdx = skipWhiteSpace(line, 0);
        int endIdx = startIdx;
        for(; endIdx < line.length(); endIdx++) {
            if(line.charAt(endIdx) == '.')
                break;
        }
        String varName = line.substring(startIdx, endIdx);
        startIdx = skipWhiteSpace(line, endIdx + 1);
        if(startIdx - endIdx > 1) return null;
        endIdx++;
        for(; endIdx < line.length(); endIdx++) {
            if(line.charAt(endIdx) == '(')
                break;
        }
        String attr = line.substring(startIdx, endIdx);

        try {
            String[] args = Objects
                    .requireNonNull(verifyArgument(line, endIdx))
                    .split(FUNC_REGEX, -1);
            return new Attributes(varName, attr, args);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public ClassInstance parseClassInstance(String line) {
        // skips whitespace between beginning of line and variable
        //    t1: task("HW", 3, 0)
        int startIdx = skipWhiteSpace(line, 0);
        int endIdx = startIdx;
        // parses the variable name
        for(; endIdx < line.length(); endIdx++) {
            if(line.charAt(endIdx) == ':')
                break;
        }
        String varName = line.substring(startIdx, endIdx);

        // skips whitespace between variable and class instance:
        endIdx = skipWhiteSpace(line, endIdx);
        startIdx = endIdx;
        // parses class instance type
        for(; endIdx < line.length(); endIdx++) {
            if(line.charAt(endIdx) == '(')
                break;
        }
        String instanceType = line.substring(startIdx, endIdx);

        // parses arguments in between parentheses (...)
        String arguments = verifyArgument(line, endIdx);
        if(arguments == null) return null;

        // verifies parsed class instance exists and returns correct ClassInstance child class
        ClassInstance inst = null;
        switch(instanceType) {
            case "card":
                inst = parseCard(arguments);
                break;
            case "task":
                inst = parseTask(arguments);
                break;
            case "label":
                inst = parseLabel(arguments);
                break;
            case "checklist":
                inst = parseCheckList(arguments);
                break;
            default:
                return null;
        }
        if(inst == null) return null;
        inst.setVarName(varName);
        return inst;
    }

    private CardInstance parseCard(String args) {
        String[] arguments = args.split(FUNC_REGEX, -1);
        return arguments.length == 1 ? new CardInstance(arguments[0]) : null;
    }

    private TaskInstance parseTask(String args) {
        String[] arguments = args.split(FUNC_REGEX, -1);
        return arguments.length == 3 ? new TaskInstance(arguments[0],
                Integer.parseInt(arguments[1]), Integer.parseInt(arguments[2])) : null;
    }

    private LabelInstance parseLabel(String args) {
        String[] arguments = args.split(FUNC_REGEX, -1);
        return arguments.length == 2 ? new LabelInstance(arguments[0], Integer.parseInt(arguments[1])) : null;
    }

    private ClassInstance parseCheckList(String args) {
        String[] arguments = args.split(FUNC_REGEX, -1);
        return arguments.length == 1 ? new CheckListInstance(arguments[0]) : null;
    }

    private int skipWhiteSpace(String line, int startIdx) {
        for(; startIdx < line.length(); startIdx++) {
            if(line.charAt(startIdx) != ' ' && line.charAt(startIdx) != '\t')
                break;
        }
        return startIdx;
    }

    private String verifyArgument(String line, int startIdx) {
        startIdx = skipWhiteSpace(line, startIdx);
        if(line.charAt(startIdx) != '(') return null;
        int beginIdx = startIdx;
        for(; startIdx < line.length(); startIdx++) {
            if(line.charAt(startIdx) == ')')
                break;
        }
        skipWhiteSpace(line, startIdx);
        if(line.charAt(startIdx) != ' ' && line.charAt(startIdx) != '\t') return null;
        return startIdx - beginIdx == 1 ? "" : line.substring(beginIdx, startIdx);
    }







    /*
    Will be stored under the ".attr" file

    .attr_task foo(sample_integer)
       temp: .get_hours() - sample_integer
       if(temp < 0)
          return false
       .set_hours(temp)
       return true


     .attr_task outputHours()
        return "total_hours=", .get_hours(), ", hours_remaining=", get_remaining_hours()

     print(t1.outputHours())
     */


    /*
    Will also have a ".func" file to store all created functions

    Another idea is to store objects in various files (need to see if this is practical or not):

    custom_type(x, y)

    .foo()
      return x

    .boo()
      return y

    .string()
      return x, y
     */



























    public CheckList parseCheckListOld(String line) {
        String varName = processVariable(line);
        if(varName == null) return null;
        if(!processType(line, 2, varNameLength)) return null;
        String argumentSet = processConstructor(line, constructIdx);
        assert argumentSet != null;
        String[] tokens = argumentSet.split(",");
        if(tokens.length != 2) {
            throw new InvalidGrammarException();
        }
        CheckList cl = new CheckList(0, tokens[0]);
        Type t1 = lookupVariable(varName);
        if(t1 == null) {
            variables.add(new Type(cl, varName, Type.TypeId.CHECKLIST));
        } else {
            t1.setDatatype(cl, Type.TypeId.CHECKLIST);
        }
        return cl;


//        String[] tokens = processArguments(line, 1, ",");
//        try {
//            int id = scheduleManager.getLastCLId() + clList.size();
//            String name = tokens[0];
//            clList.add(new CheckList(id, name));
//            scriptLog.reportCheckListCreation(id, name);
//            System.out.println("Checklist added.. [C" + (scheduleManager.getLastCLId() + clList.size() - 1) + "]");
//        } catch(Exception e) {
//            throw new InvalidGrammarException("Invalid input. Expected[checklist: <name: string>]");
//        }
    }

    public Label parseLabelOld(String line) {
        String varName = processVariable(line);
        if(varName == null) return null;
        if(!processType(line, 1, varNameLength)) return null;
        String argumentSet = processConstructor(line, constructIdx);
        assert argumentSet != null;
        String[] tokens = argumentSet.split(",");
        if(tokens.length != 2) {
            throw new InvalidGrammarException();
        }
        Label l1 = new Label(0, tokens[0], Integer.parseInt(tokens[1]));
        Type t1 = lookupVariable(varName);
        if(t1 == null) {
            variables.add(new Type(l1, varName, Type.TypeId.LABEL));
        } else {
            t1.setDatatype((Linker) t1, Type.TypeId.LABEL);
        }
        return l1;

//        String[] tokens = processArguments(line, 2, ",");
//        try {
//            int id = scheduleManager.getLastLabelId() + labelList.size();
//            String name = tokens[0];
//            int color = Integer.parseInt(tokens[1]);
//            labelList.add(new Label(id, name, color));
//            scriptLog.reportLabelCreation(id, name, color);
//            System.out.println("Label added.. [L" + (scheduleManager.getLastLabelId() + labelList.size() - 1) + "]");
//        } catch(Exception e) {
//            throw new InvalidGrammarException("Invalid input. Expected[label: <name: string>, <color: int>]");
//        }
    }

    public Task parseTaskOld(String line) {
        String varName = processVariable(line);
        if(varName == null) return null;
        if(!processType(line, 0, varNameLength)) return null;
        String argumentSet = processConstructor(line, constructIdx);
        assert argumentSet != null;
        String[] tokens = argumentSet.split(",");
        if(tokens.length != 3) {
            throw new InvalidGrammarException();
        }
        Task t1 = new Task(0, tokens[0], Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
        Type type1 = lookupVariable(varName);
        if(type1 == null) {
            variables.add(new Type(t1, varName, Type.TypeId.TASK));
        } else {
            type1.setDatatype(t1, Type.TypeId.TASK);
        }
        return t1;


//        String[] tokens = processArguments(line, 3, ",");
//        try {
//            int id = scheduleManager.getLastTaskId() + taskList.size();
//            int hours = Integer.parseInt(tokens[1]);
//            int days = Integer.parseInt(tokens[2]);
//            Task t = new Task(id, tokens[0], hours, days);
//            taskList.add(t);
//            scriptLog.reportTaskCreation(id, tokens[0], hours, days);
//            System.out.println("Task added.. [T" + (scheduleManager.getLastTaskId() + taskList.size() - 1) + "]");
//            //adds task to default card
//            cardList.get(0).addTask(t);
//        } catch(Exception e) {
//            throw new InvalidGrammarException("Invalid input. Expected[task: <name: string>, <hours: int>, <num_days: int>]");
//        }
    }

    private String verifyString(String line) {
        boolean quoteBegin = false;
        boolean quoteEnd = false;
        int beginIdx = 0;
        int endIdx = 0;
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

    private String processConstructor(String line, int startIdx) {
        //assume you're starting at the first parentheses
        boolean start = false;
        boolean end = false;
        boolean inQuote = false;
        int idx = 0;
        int subStrBeg = -1;
        int subStrEnd = -1;
        for (int i = startIdx; i < line.length(); i++) {
            if (line.charAt(i) == '(' && !start && !inQuote) {
                start = true;
            } else if (line.charAt(i) == ')' && start && !inQuote) {
                end = true;
                idx = i;
                break;
            } else if (line.charAt(i) == '"') {
                if (subStrBeg == -1) {
                    subStrBeg = i;
                    subStrEnd = subStrBeg;
                } else {
                    subStrEnd++;
                }
                inQuote = !inQuote;
            } else if (inQuote || line.charAt(i) != '(' && line.charAt(i) != ')') {
                if (subStrBeg == -1) {
                    subStrBeg = i;
                    subStrEnd = subStrBeg;
                } else {
                    subStrEnd++;
                }
            } else {
                return null;
            }
        }

        for(int i = idx + 1; i < line.length(); i++) {
            if(line.charAt(i) != ' ' && line.charAt(i) != '\t'
                    && line.charAt(i) != '\r' && line.charAt(i) != '\n') {
                return null;
            }
        }

        if(!start || !end) {
            return null;
        }

        return line.substring(subStrBeg, subStrEnd + 1);

    }
}
