package agile.planner.scripter;

import java.util.*;

public class Parser {

    private static final String FUNC_REGEX = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

    public enum AttrFunc {
        GET_ID,
        SET_ID,
        GET_TITLE,
        SET_TITLE,
        GET_COLOR,
        SET_COLOR,
        GET_DUE_DATE,
        SET_DUE_DATE,
        //todo need to finish (don't worry about working with Lists yet, focus on single objects)
        TOTAL_HOURS,
        SUB_TOTAL_HOURS,
        AVG_HOURS,
        TASKS,
        CHECKLIST,
        LABELS,
        ADD,
        REMOVE
    }

    public enum Operation {
        FUNCTION,        // <foo>()
        INSTANCE,        // <var>: <type>
        ATTRIBUTE,       // <var>.<attr>
        PRE_PROCESSOR,   // include: ..
        SETUP_CUST_FUNC, // func ..
        COMMENT,         // #
        CONSTANT,        // # or "abc" or true/false
        VARIABLE         // anything besides the above
    }

    public PreProcessor parsePreProcessor(String line) {
        String[] func = line.split(":");
        if(func.length != 2) return null;
        if("include".equals(func[0].trim())) {
            String[] tokens = line.split(",");
            tokens[0] = tokens[0].substring("include:".length());
            //code below
            boolean config = false;
            boolean defConfig = false;
            boolean debug = false;
            boolean log = false;
            boolean imprt = false;
            boolean exprt = false;
            boolean build = false;
            boolean stats = false;
            for (String s : tokens) {
                switch (s.trim()) {
                    case "__CURR_CONFIG__":
                        if (config) {
                            return null;
                        }
                        config = true;
                        break;
                    case "__DEF_CONFIG__":
                        if (config) {
                            return null;
                        }
                        config = true;
                        defConfig = true;
                        break;
                    case "__DEBUG__":
                        if (debug) {
                            return null;
                        }
                        debug = true;
                        break;
                    case "__LOG__":
                        if (log) {
                            return null;
                        }
                        log = true;
                        break;
                    case "__IMPORT__":
                        if (imprt) {
                            return null;
                        }
                        imprt = true;
                        break;
                    case "__EXPORT__":
                        if (exprt) {
                            return null;
                        }
                        exprt = true;
                        break;
                    case "__BUILD__":
                        if (build) {
                            return null;
                        }
                        build = true;
                        break;
                    case "__STATS__":
                        if (stats) {
                            return null;
                        }
                        stats = true;
                        break;
                    default:
                        return null;
                }
            }
            return config ? new PreProcessor(defConfig, log, imprt, exprt, build, stats) : null;
        }
        return null;
    }

    // class instance assignment  [DONE]
    // attribute handling         [DONE]
    // function calls             [DONE]
    // String/int/bool assignment [TBD]
    // Preprocessor handling      [DONE]
    // Create custom function     [TBD]
    // Calling custom function    [TBD]

    public Operation typeOfOperation(String line) {
        String[] tokens = line.trim().split("\\s");
        if(tokens[0].charAt(0) == '#') return Operation.COMMENT;
        switch(tokens[0]) {
            case "include:":
                return Operation.PRE_PROCESSOR;
            case "func":
                return Operation.SETUP_CUST_FUNC;
            case "true":
            case "false":
                return Operation.CONSTANT;
            default:
                switch(tokens[0].charAt(tokens[0].length() - 1)) {
                    case ':':
                        return Operation.INSTANCE;
                    case '.':
                        return Operation.ATTRIBUTE;
                    default:
                        if(tokens.length > 1 && tokens[1].length() > 0 && tokens[1].charAt(0) == '.' || verifyAttr(line)) {
                            return Operation.ATTRIBUTE;
                        } else if(tokens[0].charAt(0) == '"' && tokens[tokens.length - 1].charAt(tokens[tokens.length - 1].length() - 1) == '"') {
                            return Operation.CONSTANT;
                        } else if(containsInteger(line.trim())) {
                            return Operation.CONSTANT;
                        } else {
                            String finalToken = tokens[tokens.length - 1].trim();
                            if(finalToken.charAt(finalToken.length() - 1) == ')') {
                                return Operation.FUNCTION;
                            }
                            return Operation.VARIABLE;
                        }
                }
        }
    }

    private boolean verifyAttr(String line) {
        int startIdx = skipWhiteSpace(line, 0);
        for(int i = startIdx; i < line.length(); i++) {
            if(line.charAt(i) == '.') return true;
        }
        return false;
    }

    private boolean containsInteger(String line) {
        try {
            Integer.parseInt(line.trim());
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    public StaticFunction parseCustomFunction(String[] lines) {
        return null;
    }

    public StaticFunction parseStaticFunction(String line) {
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
            case "serialize_data":
            case "write_file":
            case "read_file":
            case "encrypt_data":
            case "decrypt_data":
            case "get_task_by_name":
            case "get_task_by_id": //todo need to add more functions
                String arguments = verifyArgument(line, endIdx);
                if(arguments == null) return null;
                return new StaticFunction(funcName, "".equals(arguments.trim()) ? null : arguments.split(FUNC_REGEX, -1));
            default:
                //todo need to use a map to locate custom functions
                return null;
        }
    }

    public Type parseConstant(String line) {
        String trimmed = line.trim();
        try {
            int val = Integer.parseInt(trimmed);
            return new Type(val, null);
        } catch(Exception e) {
            //process string now
            if(line.charAt(0) == '"' && line.charAt(line.length() - 1) == '"') {
                return new Type(line, null);
            } else if("true".equals(line)) {
                return new Type(true, null);
            } else if("false".equals(line)) {
                return new Type(false, null);
            } else {
                return null;
            }
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
            //todo need to fix
            String[] args = Objects
                    .requireNonNull(verifyArgument(line, endIdx))
                    .split(FUNC_REGEX, -1);
            if(args.length == 1 && "".equals(args[0])) {
                args = null;
            }
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
        String instanceType = line.substring(startIdx + 1, endIdx).trim();

        // parses arguments in between parentheses (...)
        String arguments = verifyArgument(line, endIdx);
        if(arguments == null) return null;

        // verifies parsed class instance exists and returns correct ClassInstance child class
        ClassInstance inst;
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
        return arguments.length == 3 ? new TaskInstance(arguments[0].trim(),
                Integer.parseInt(arguments[1].trim()), Integer.parseInt(arguments[2].trim())) : null;
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

    //todo need to fix
    private String verifyArgument(String line, int startIdx) {
        startIdx = skipWhiteSpace(line, startIdx);
        if(startIdx >= line.length() || line.charAt(startIdx) != '(') return null;
        int beginIdx = startIdx;
        for(; startIdx < line.length(); startIdx++) {
            if(line.charAt(startIdx) == ')')
                break;
        }
        if(startIdx >= line.length() || line.charAt(startIdx) != ')') return null;
        int idx = skipWhiteSpace(line, startIdx);
        if(idx != startIdx && line.charAt(idx) != ' ' && line.charAt(idx) != '\t') return null;
        return startIdx - beginIdx == 1 ? "" : line.substring(beginIdx + 1, startIdx + 1);
    }

    public AttrFunc determineAttrFunc(String attr) {
        switch(attr) {
            case "get_id":
                return AttrFunc.GET_ID;
            case "set_id":
                return AttrFunc.SET_ID;
            case "get_title":
                return AttrFunc.GET_TITLE;
            case "set_title":
                return AttrFunc.SET_TITLE;
            case "get_color":
                return AttrFunc.GET_COLOR;
            case "set_color":
                return AttrFunc.SET_COLOR;
            case "get_due_date":
                return AttrFunc.GET_DUE_DATE;
            case "set_due_date":
                return AttrFunc.SET_DUE_DATE;
            default:
                return null;
        }
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


     Will also need to provide the ".func" file for static functions
     */
}
