package agile.planner.scripter;

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
        PRE_PROCESSOR,
        RETURN,
        FUNC_FILE,
        ATTR_FILE,
        SETUP_CUST_FUNC,
        CALL_CUST_FUNC
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
            for(int i = 0; i < tokens.length; i++) {
                String s = tokens[i];
                switch(tokens[i].trim()) {
                    case "__CURR_CONFIG__":
                        if(config) {
                            return null;
                        }
                        config = true;
                        break;
                    case "__DEF_CONFIG__":
                        if(config) {
                            return null;
                        }
                        config = true;
                        defConfig = true;
                        break;
                    case "__DEBUG__":
                        if(debug) {
                            return null;
                        }
                        debug = true;
                        break;
                    case "__LOG__":
                        if(log) {
                            return null;
                        }
                        log = true;
                        break;
                    case "__IMPORT__":
                        if(imprt) {
                            return null;
                        }
                        imprt = true;
                        break;
                    case "__EXPORT__":
                        if(exprt) {
                            return null;
                        }
                        exprt = true;
                        break;
                    case "__BUILD__":
                        if(build) {
                            return null;
                        }
                        build = true;
                        break;
                    case "__STATS__":
                        if(stats) {
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

    public Statement parseMethod(String line) {
        String varName = null;

        return new AssignmentState(null, null);
    }

    // class instance assignment  [DONE]
    // attribute handling         [DONE]
    // function calls             [DONE]
    // String/int/bool assignment [TBD]
    // Preprocessor handling      [DONE]
    // Custom function            [TBD]
    // Calling custom function    [TBD]
    // Reading .func file         [TBD]

    //todo need to include whatever data is available when possible (allows us to throw exceptions such as DereferenceNullPointer)

    public Operation typeOfOperation(String line) {

        return null;
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
                String arguments = verifyArgument(line, endIdx);
                if(arguments == null) return null;
                return new StaticFunction(funcName, "".equals(arguments.trim()) ? null : arguments.split(FUNC_REGEX, -1));
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
        if(startIdx >= line.length() || line.charAt(startIdx) != '(') return null;
        int beginIdx = startIdx;
        for(; startIdx < line.length(); startIdx++) {
            if(line.charAt(startIdx) == ')')
                break;
        }
        if(startIdx >= line.length() || line.charAt(startIdx) != ')') return null;
        int idx = skipWhiteSpace(line, startIdx);
        if(idx != startIdx && line.charAt(idx) != ' ' && line.charAt(idx) != '\t') return null;
        return startIdx - beginIdx == 1 ? "" : line.substring(beginIdx + 1, startIdx);
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
