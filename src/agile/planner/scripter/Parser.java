package agile.planner.scripter;

import agile.planner.scripter.exception.InvalidGrammarException;

import java.util.*;

public class Parser {

//    private static final String FUNC_REGEX = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

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
        REMOVE,

        //Integer
        SUBTRACT,
        DIVIDE,
        MULTIPLY,
        MOD,

        //String
        LENGTH,
        PARSE_INT,
        PARSE_BOOL,
        SUB_STRING,

        //Checklist
        ADD_ITEM,
        REMOVE_ITEM_BY_ID,
        REMOVE_ITEM_BY_NAME,
        MARK_ITEM_BY_ID,
        MARK_ITEM_BY_NAME,
        GET_PERCENT
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
    // String/int/bool assignment [DONE]
    // Preprocessor handling      [DONE]
    // Create custom function     [TBD]
    // Calling custom function    [TBD]

    public Operation typeOfOperation(String line) {
        if("".equals(line)) return Operation.COMMENT;
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
                        if(verifyFunc(line)) {
                            return Operation.FUNCTION;
                        } else if(tokens.length > 1 && tokens[1].length() > 0 && tokens[1].charAt(0) == '.' || verifyAttr(line)) {
                            return Operation.ATTRIBUTE;
                        } else if(tokens[0].charAt(0) == '"' && tokens[tokens.length - 1].charAt(tokens[tokens.length - 1].length() - 1) == '"') {
                            return Operation.CONSTANT;
                        } else if(containsInteger(line.trim())) {
                            return Operation.CONSTANT;
                        } else {
//                            String finalToken = tokens[tokens.length - 1].trim();
//                            if(finalToken.charAt(finalToken.length() - 1) == ')') {
//                                return Operation.FUNCTION;
//                            }
                            return Operation.VARIABLE;
                        }
                }
        }
    }

    private boolean verifyFunc(String line) {
        String trimmed = line.trim();
        for(int i = 0; i < trimmed.length(); i++) {
            if(trimmed.charAt(i) == '.') return false;
            if(trimmed.charAt(i) == '(') break;
        }
        return trimmed.charAt(trimmed.length() - 1) == ')';
    }

    private boolean verifyAttr(String line) {
        String trimmed = line.trim();
        if(line.charAt(0) == '"') return false;
        for(int i = 0; i < trimmed.length(); i++) {
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
            case "println":
            case "import":
            case "export":
            case "serialize_data":
            case "write_file":
            case "read_file":
            case "encrypt_data":
            case "decrypt_data":
            case "get_task_by_name":
            case "get_task_by_id": //todo need to add more functions
                String[] arguments = verifyArgument(line, endIdx);
                return new StaticFunction(funcName, arguments);
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
                return new Type(line.substring(1, line.length() - 1), null);
            } else if("true".equals(line)) {
                return new Type(true, null);
            } else if("false".equals(line)) {
                return new Type(false, null);
            } else {
                return null;
            }
        }

    }

    /**
     * Parses the {@link Attributes} for a given {@link ClassInstance} in order to return formatted data to allow
     * access to the object's data or functionality.
     *
     * @param line unprocessed attribute line and arguments
     * @return formatted {@link Attributes}
     */
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
        String[] args = verifyArgument(line, endIdx);

        return new Attributes(varName, attr, args);
    }

    /**
     * Parses a specific {@link ClassInstance} while verifying it to be valid both from its structure
     * and argument types and amounts.
     *
     * @param line unprocessed class instance and assignment line
     * @return formatted {@link ClassInstance}
     */
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
        String[] arguments = verifyArgument(line, endIdx);

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
            case "cl":
                inst = parseCheckList(arguments);
                break;
            case "string":
                inst = parseString(arguments);
                break;
            case "int":
                inst = parseInteger(arguments);
                break;
            case "bool":
                inst = parseBool(arguments);
                break;
            default:
                return null;
        }
        if(inst == null) return null;
        inst.setVarName(varName);
        return inst;
    }

    private BoolInstance parseBool(String[] args) {
        return args.length == 1 ? new BoolInstance(Boolean.parseBoolean(args[0])) : null;
    }

    private IntegerInstance parseInteger(String[] args) {
        return args.length == 1 ? new IntegerInstance(Integer.parseInt(args[0])) : null;
    }

    private StringInstance parseString(String[] args) {
        return args.length == 1 ? new StringInstance(args[0]) : null;
    }

    /**
     * Parses an instance of {@link agile.planner.data.Card} and wraps it around {@link CardInstance}
     *
     * @param args arguments for the given class
     * @return formatted {@link CardInstance}
     */
    private CardInstance parseCard(String[] args) {
//        String[] arguments = args.split(FUNC_REGEX, -1);
//        return arguments.length == 1 ? new CardInstance(arguments[0]) : null;
        return args.length == 1 ? new CardInstance(args[0]) : null;
    }

    /**
     * Parses an instance of {@link agile.planner.data.Task} and wraps it around {@link TaskInstance}
     *
     * @param args arguments for the given class
     * @return formatted {@link TaskInstance}
     */
    private TaskInstance parseTask(String[] args) {
//        String[] arguments = args.split(FUNC_REGEX, -1);
//        return arguments.length == 3 ? new TaskInstance(arguments[0].trim(),
//                Integer.parseInt(arguments[1].trim()), Integer.parseInt(arguments[2].trim())) : null;
        return args.length == 3 ? new TaskInstance(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2])) : null;
    }

    /**
     * Parses an instance of {@link agile.planner.data.Label} and wraps it around {@link LabelInstance}
     *
     * @param args arguments for the given class
     * @return formatted {@link LabelInstance}
     */
    private LabelInstance parseLabel(String[] args) {
//        String[] arguments = args.split(FUNC_REGEX, -1);
        return args.length == 2 ? new LabelInstance(args[0], Integer.parseInt(args[1])) : null;
    }

    /**
     * Parses an instance of {@link agile.planner.util.CheckList} and wraps it around {@link CheckListInstance}
     *
     * @param args arguments for the given class
     * @return formatted {@link CheckListInstance}
     */
    private ClassInstance parseCheckList(String[] args) {
//        String[] arguments = args.split(FUNC_REGEX, -1);
        return args.length == 1 ? new CheckListInstance(args[0]) : null;
    }

    private int skipWhiteSpace(String line, int startIdx) {
        for(; startIdx < line.length(); startIdx++) {
            if(line.charAt(startIdx) != ' ' && line.charAt(startIdx) != '\t')
                break;
        }
        return startIdx;
    }

    /**
     * Verifies the arguments of the given parameters to a given function or method
     *
     * @param line unprocessed line of arguments
     * @param startIdx beginning index where {@code '('} starts
     * @return String array of formatted arguments
     */
    private String[] verifyArgument(String line, int startIdx) {
        String trimmed = line.trim();
        if(startIdx >= trimmed.length() || trimmed.charAt(startIdx) != '(') throw new InvalidGrammarException();
        String str =  trimmed.charAt(trimmed.length() - 1) == ')' ? trimmed.substring(startIdx + 1, trimmed.length() - 1) : null;
        if(str == null) {
            throw new InvalidGrammarException();
        }
        List<String> list = new ArrayList<>();
        int start = 0;
        boolean quote = false;
        Stack<Boolean> paren = new Stack<>();
        int i = 0;
        for(; i < str.length(); i++) {
            if(str.charAt(i) == ',' && !quote && paren.isEmpty()) {
                list.add(str.substring(start, i));
                start = i + 1;
            } else if(str.charAt(i) == '"') {
                quote = !quote;
            } else if(str.charAt(i) == '(') {
                paren.push(true);
            } else if(str.charAt(i) == ')') {
                if(paren.isEmpty()) {
                    throw new InvalidGrammarException();
                }
                paren.pop();
            }
        }
        if(i > start) list.add(str.substring(start, i));
        String[] args = new String[list.size()];
        for(int j = 0; j < list.size(); j++)
            args[j] = list.get(j).trim();

        return args;

//        if(startIdx >= line.length() || line.charAt(startIdx) != '(') return null;
//        int beginIdx = startIdx;
//        for(; startIdx < line.length(); startIdx++) {
//            if(line.charAt(startIdx) == ')')
//                break;
//        }
//        if(startIdx >= line.length() || line.charAt(startIdx) != ')') return null;
//        int idx = skipWhiteSpace(line, startIdx);
//        if(idx != startIdx && line.charAt(idx) != ' ' && line.charAt(idx) != '\t') return null;
//        return startIdx - beginIdx == 1 ? "" : line.substring(beginIdx + 1, startIdx + 1);
    }

    /**
     * Determines the attribute function for the given class instance
     *
     * @param attr attribute function for the specified instance
     * @return {@link AttrFunc} enumeration of the operation type
     */
    public AttrFunc determineAttrFunc(String attr) { //todo need to finish this before working on Type attrSet()
        switch(attr) {
            case "get_id":
            case "id":
                return AttrFunc.GET_ID;
            case "set_id":
                return AttrFunc.SET_ID;
            case "get_title":
            case "title":
                return AttrFunc.GET_TITLE;
            case "set_title":
                return AttrFunc.SET_TITLE;
            case "get_color":
            case "color":
                return AttrFunc.GET_COLOR;
            case "set_color":
                return AttrFunc.SET_COLOR;
            case "get_due_date":
            case "due_date":
                return AttrFunc.GET_DUE_DATE;
            case "set_due_date":
                return AttrFunc.SET_DUE_DATE;
            case "add":
                return AttrFunc.ADD;
            case "sub":
                return AttrFunc.SUBTRACT;
            case "div":
                return AttrFunc.DIVIDE;
            case "mul":
                return AttrFunc.MULTIPLY;
            case "mod":
                return AttrFunc.MOD;
            case "length":
                return AttrFunc.LENGTH;
            case "parse_int":
                return AttrFunc.PARSE_INT;
            case "parse_bool":
                return AttrFunc.PARSE_BOOL;
            case "sub_string":
                return AttrFunc.SUB_STRING;
            //CheckList attr functions
            case "add_item":
                return AttrFunc.ADD_ITEM;
            case "remove_item_by_id":
                return AttrFunc.REMOVE_ITEM_BY_ID;
            case "remove_item_by_name":
                return AttrFunc.REMOVE_ITEM_BY_NAME;
            case "mark_item_by_id":
                return AttrFunc.MARK_ITEM_BY_ID;
            case "mark_item_by_name":
                return AttrFunc.MARK_ITEM_BY_NAME;
            case "get_percent":
                return AttrFunc.GET_PERCENT;
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
