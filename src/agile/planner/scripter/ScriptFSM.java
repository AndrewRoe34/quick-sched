package agile.planner.scripter;

import agile.planner.data.Card;
import agile.planner.data.Label;
import agile.planner.data.Task;
import agile.planner.io.IOProcessing;
import agile.planner.manager.ScheduleManager;
import agile.planner.scripter.exception.*;
import agile.planner.scripter.tools.ScriptLog;
import agile.planner.util.CheckList;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class ScriptFSM {

    private List<Type> variableList = new LinkedList<>();
    private final Parser parser = new Parser();
    private final ScriptLog scriptLog = new ScriptLog();
    private List<Type> checklistVariables = new ArrayList<>();
    private List<Type> labelVariables = new ArrayList<>();
    private List<Type> taskVariables = new ArrayList<>();
    private List<Task> taskList = new ArrayList<>();
    private List<Type> cardVariables = new ArrayList<>();
    private List<Type> primitiveVariables = new ArrayList<>();
    private Map<String, CustomFunction> funcMap = new HashMap<>();
    private List<Type> localStack = new ArrayList<>();
    private PreProcessor preProcessor = null;
    private boolean ppStatus = false;
    private Scanner scriptScanner;
    private boolean inFunction;
    private ScheduleManager scheduleManager = ScheduleManager.getScheduleManager();

    public void executeScript() throws FileNotFoundException {
        scriptScanner = new Scanner(new File("data/fun1.smpl"));
        while (scriptScanner.hasNextLine()) {
            String untrimmed = scriptScanner.nextLine();
            String line = untrimmed.trim();
            boolean status = true;
            while(status) {
                Parser.Operation operation = parser.typeOfOperation(line);
                try {
                    switch (operation) {
                        case COMMENT:
                            break;
                        case PRE_PROCESSOR:
                            PreProcessor preProcessor = parser.parsePreProcessor(line);
                            if (preProcessor == null) throw new InvalidPreProcessorException();
                            this.preProcessor = preProcessor;
                            ppStatus = true;
                            scriptLog.reportPreProcessorSetup(preProcessor);
                            break;
                        case ATTRIBUTE:
                            if(!ppStatus) throw new InvalidPreProcessorException();
                            Attributes attr = parser.parseAttributes(line);
                            if (attr == null) throw new InvalidFunctionException();
                            processAttribute(attr);
                            scriptLog.reportAttrFunc(attr);
                            break;
                        case FUNCTION:
                            if(!ppStatus) throw new InvalidPreProcessorException();
                            StaticFunction func = parser.parseStaticFunction(line);
                            processStaticFunction(func);
                            scriptLog.reportFunctionCall(func);
                            break;
                        case INSTANCE:
                            if(!ppStatus) throw new InvalidPreProcessorException();
                            ClassInstance classInstance = parser.parseClassInstance(line);
                            if (classInstance == null) {
                                int i = 0;
                                for(; i < line.length(); i++) {
                                    if(line.charAt(i) == ':')
                                        break;
                                }
                                String varName = line.substring(0, i);
                                String trimmed = line.substring(i + 1).trim();
                                Parser.Operation op = parser.typeOfOperation(trimmed);
                                Type t1 = lookupVariable(varName);
                                Type ret = null;
                                switch (op) {
                                    case ATTRIBUTE:
                                        Attributes atr = parser.parseAttributes(trimmed);
                                        if (atr == null) throw new InvalidFunctionException();
                                        ret = processAttribute(atr);
                                        break;
                                    case FUNCTION:
                                        StaticFunction fnc = parser.parseStaticFunction(trimmed);
                                        ret = processStaticFunction(fnc);
                                        break;
                                    default:
                                        throw new InvalidPairingException();
                                }
                                if(ret == null) throw new InvalidGrammarException();
                                if(t1 == null) {
                                    ret.setVariableName(varName);
                                    variableList.add(ret);
                                } else {
                                    t1.setTypeVal(ret);
                                }
                            } else {
                                Type t1 = processClassInstance(classInstance);
                                scriptLog.reportInstantiation(t1);
                            }
                            break;
                        case SETUP_CUST_FUNC:
                            if(!ppStatus) throw new InvalidPreProcessorException();
                            CustomFunction customFunction = parser.parseCustomFunction(untrimmed);
                            if(customFunction == null) {
                                throw new InvalidFunctionException();
                            } else {
                                untrimmed = setupCustomFunction(customFunction);
                                if(untrimmed != null) {
                                    line = untrimmed.trim();
                                    status = true;
                                }
                            }
                            scriptLog.reportFunctionSetup(customFunction);
                            break;
                        case CONSTANT:
                            if(!ppStatus) throw new InvalidPreProcessorException();
                            break;
                        default:
                            throw new InvalidGrammarException();
                    }
                } catch (Throwable e) {
                    System.out.println("\u001B[31m" + e.getClass() + "\u001B[0m" + ": " + e.getMessage());
                    scriptLog.reportException(e);
                    if(preProcessor.isLog()) {
                        IOProcessing.writeStringToFile(scriptLog.toString());
                    }
                    System.exit(1);
                }
                if(operation != Parser.Operation.SETUP_CUST_FUNC) {
                    status = false;
                }
            }
        }
        if(preProcessor.isBuild()) {
            scheduleManager.outputScheduleToConsole();
        }
        if(preProcessor.isLog()) {
            IOProcessing.writeStringToFile(scriptLog.toString());
        }
    }

    protected String setupCustomFunction(CustomFunction customFunction) {
        String line = null;
        boolean flag = false;
        while(scriptScanner.hasNextLine()) {
            line = scriptScanner.nextLine();
            int spacing = numSpaces(line);
            if(spacing <= customFunction.getNumSpaces()) {
                flag = true;
                break;
            }
            customFunction.addLine(line.trim());
        }
        funcMap.put(customFunction.getFuncName().trim(), customFunction);
        return flag ? line : null;
    }

    private int numSpaces(String line) {
        int count = 0;
        int idx = 0;
        for(; idx < line.length(); idx++) {
            if (line.charAt(idx) == ' ') {
                count++;
            } else if(line.charAt(idx) == '\t') {
                count += 4;
            } else break;
        }
        return count;
    }

    protected Type processClassInstance(ClassInstance classInstance) {
        Type t1 = null;
        if (classInstance instanceof CardInstance) {
            CardInstance card = (CardInstance) classInstance;
            t1 = lookupVariable(card.getVarName());
            if(t1 == null) {
                t1 = new Type(new Card(card.getTitle().substring(1, card.getTitle().length() - 1)), card.getVarName(), Type.TypeId.CARD);
                variableList.add(t1);
            } else {
                t1.setLinkerData(new Card(card.getTitle()), Type.TypeId.CARD);
            }
        } else if (classInstance instanceof TaskInstance) {
            TaskInstance task = (TaskInstance) classInstance;
            t1 = lookupVariable(task.getVarName());
            Task createdTask = new Task(0, task.getName(), task.getTotalHours(), task.getDueDate());
            if(t1 == null) {
                t1 = new Type(createdTask, task.getVarName(), Type.TypeId.TASK);
                variableList.add(t1);
            } else {
                t1.setLinkerData(createdTask, Type.TypeId.TASK);
            }
            taskList.add(createdTask);
        } else if(classInstance instanceof CheckListInstance) {
            CheckListInstance cl = (CheckListInstance) classInstance;
            t1 = lookupVariable(cl.getVarName());
            if(t1 == null) {
                t1 = new Type(new CheckList(0, cl.getTitle()), cl.getVarName(), Type.TypeId.CHECKLIST);
                variableList.add(t1);
            } else {
                t1.setLinkerData(new CheckList(0, cl.getTitle()), Type.TypeId.CHECKLIST);
            }
        } else if (classInstance instanceof LabelInstance) {
            LabelInstance label = (LabelInstance) classInstance;
            t1 = lookupVariable(label.getVarName());
            if(t1 == null) {
                t1 = new Type(new Label(0, label.getName(), label.getColor()), label.getVarName(), Type.TypeId.LABEL);
                variableList.add(t1);
            } else {
                t1.setLinkerData(new Label(0, label.getName(), label.getColor()), Type.TypeId.LABEL);
            }
        } else if (classInstance instanceof StringInstance) {
            StringInstance str = (StringInstance) classInstance;
            t1 = lookupVariable(str.getVarName());
            if(t1 == null) {
                t1 = new Type(str.getStr(), str.getVarName());
                variableList.add(t1);
            } else {
                t1.setStringConstant(str.getStr());
            }
        } else if(classInstance instanceof IntegerInstance) {
            IntegerInstance i = (IntegerInstance) classInstance;
            t1 = lookupVariable(i.getVarName());
            if(t1 == null) {
                t1 = new Type(i.getVal(), i.getVarName());
                variableList.add(t1);
            } else {
                t1.setIntConstant(i.getVal());
            }
        } else if(classInstance instanceof BoolInstance) {
            BoolInstance bool = (BoolInstance) classInstance;
            t1 = lookupVariable(bool.getVarName());
            if(t1 == null) {
                t1 = new Type(bool.isVal(), bool.getVarName());
                variableList.add(t1);
            } else {
                t1.setBoolConstant(bool.isVal());
            }
        }
        return t1;
    }

    protected Type processAttribute(Attributes attr) {
        /*
        1. Lookup the variable
        2. Attempt to call the method via the Type method attrSet()
         */
        Type[] args = processArgumentsHelper(attr.getArgs());
        Type t1 = null;
        if(inFunction) {
            t1 = lookupLocalVariable(attr.getVarName());
        } else {
            t1 =  lookupVariable(attr.getVarName());
        }
        if (t1 == null) throw new DereferenceNullException();
        Parser.AttrFunc func = parser.determineAttrFunc(attr.getAttr());
        return t1.attrSet(func, args);
    }

    protected Type processStaticFunction(StaticFunction func) {
        /*
        1. Lookup correct function
        2. Call processArguments() for Type array
        3. Call function with the specified arguments
         */
        Type[] args = processArguments(func.getArgs());
        switch (func.getFuncName()) {
            case "build":
                funcBuild();
                return null;
            case "print":
                for (Type t : args) {
                    if(t.getVariabTypeId() == Type.TypeId.STRING && "\n".equals(t.getStringConstant())) {
                        System.out.println();
                    } else {
                        System.out.print(t.toString()); //todo doesn't fix cases where '\n' is within the string (only works when it's by itself
                    }
                }
                scriptLog.reportPrintFunc(args);
                return null;
            case "println":
                for (Type t : args) {
                    if(t.getVariabTypeId() == Type.TypeId.STRING && "\n".equals(t.getStringConstant())) {
                        System.out.println();
                    } else {
                        System.out.print(t.toString()); //todo doesn't fix cases where '\n' is within the string (only works when it's by itself
                    }
                }
                System.out.println();
                scriptLog.reportPrintFunc(args);
                return null;
            case "import_schedule":
                if(args.length != 1 || args[0].getVariabTypeId() != Type.TypeId.STRING) throw new InvalidFunctionException();
                funcImportSchedule(args[0].getStringConstant());
                return null;
            default:
                processCustomFunction(func, args);
        }
        return null;
    }

    protected void processCustomFunction(StaticFunction func, Type[] args) {
        CustomFunction customFunction = funcMap.get(func.getFuncName());
        localStack = new ArrayList<>();
        if(customFunction.getArgs().length != args.length) throw new InvalidFunctionException();

        //todo need to add constructor for Type to include another Type (will maintain referencing easily
        int j = 0;
        for(Type t : args) {
            localStack.add(new Type(t, customFunction.getArgs()[j]));
            j++;
        }


        inFunction = true;
        for(String line : customFunction.getLines()) {
            line = line.trim();
            Parser.Operation operation = parser.typeOfOperation(line);
            try {
                switch (operation) {
                    case COMMENT:
                        break;
                    case ATTRIBUTE:
                        if(!ppStatus) throw new InvalidPreProcessorException();
                        Attributes attr = parser.parseAttributes(line);
                        if (attr == null) throw new InvalidFunctionException();
                        processAttribute(attr);
                        scriptLog.reportAttrFunc(attr);
                        break;
                    case FUNCTION:
                        if(!ppStatus) throw new InvalidPreProcessorException();
                        StaticFunction myfunc = parser.parseStaticFunction(line);
                        processStaticFunction(myfunc);
                        scriptLog.reportFunctionCall(myfunc);
                        break;
                    case INSTANCE:
//                        if(!ppStatus) throw new InvalidPreProcessorException();
//                        ClassInstance classInstance = parser.parseClassInstance(line);
//                        if (classInstance == null) {
//                            int i = 0;
//                            for(; i < line.length(); i++) {
//                                if(line.charAt(i) == ':')
//                                    break;
//                            }
//                            String varName = line.substring(0, i);
//                            String trimmed = line.substring(i + 1).trim();
//                            Parser.Operation op = parser.typeOfOperation(trimmed);
//                            Type t1 = lookupVariable(varName);
//                            Type ret = null;
//                            switch (op) {
//                                case ATTRIBUTE:
//                                    Attributes atr = parser.parseAttributes(trimmed);
//                                    if (atr == null) throw new InvalidFunctionException();
//                                    ret = processAttribute(atr);
//                                    break;
//                                case FUNCTION:
//                                    StaticFunction fnc = parser.parseStaticFunction(trimmed);
//                                    ret = processStaticFunction(fnc);
//                                    break;
//                                default:
//                                    throw new InvalidPairingException();
//                            }
//                            if(ret == null) throw new InvalidGrammarException();
//                            if(t1 == null) {
//                                ret.setVariableName(varName);
//                                variableList.add(ret);
//                            } else {
//                                t1.setTypeVal(ret);
//                            }
//                        } else {
//                            processClassInstance(classInstance);
//                        }
//                        break;
                    default:
                        throw new InvalidGrammarException();
                }
            } catch (Exception e) {
                System.out.println("\u001B[31m" + e.getClass() + "\u001B[0m" + ": " + e.getMessage());
            }
        }
        inFunction = false;
    }

    public Type lookupVariable(String token) {
        for (Type t : variableList) {
            if (token.equals(t.getVariableName())) {
                return t;
            }
        }
        return null;
    }

    protected Type lookupLocalVariable(String token) {
        for(Type t : localStack) {
            if (token.equals(t.getVariableName())) {
                return t;
            }
        }
        return null;
    }

    private Type[] processArgumentsHelper(String[] args) {
        /*
        1. Verify that arguments are none or Type (nothing else)
        2. Call appropriate method
        3. Return value
         */
        Type[] types = new Type[args.length];
        for (int i = 0; i < args.length; i++) {
            Parser.Operation operation = parser.typeOfOperation(args[i]);
            switch (operation) {
                case CONSTANT:
                    types[i] = parser.parseConstant(args[i]);
                    break;
                case VARIABLE:
                    Type t1 = null;
                    if(inFunction) {
                        t1 = lookupLocalVariable(args[i]);
                    } else {
                        t1 = lookupVariable(args[i]);
                    }
                    if (t1 == null) throw new DereferenceNullException();
                    types[i] = t1;
                    break;
                default:
                    throw new InvalidFunctionException();
            }
        }
        return types;
    }

    protected Type[] processArguments(String[] args) {
        /**
         1. Verify each argument is either a Type or an Attr
         2. If Attr, call processArgumentsHelper() and store return value
         3. If Type, simply store value
         4. Call appropriate method
         5. Return value
         */
        Type[] types = new Type[args.length];
        for (int i = 0; i < args.length; i++) {
            Parser.Operation operation = parser.typeOfOperation(args[i]);
            switch (operation) {
                case ATTRIBUTE:
                    Attributes attr = parser.parseAttributes(args[i]);
                    Type[] temp = processArgumentsHelper(attr.getArgs());
                    //Lookup variable (if null, throw exception)
                    Type t1 = null;
                    if(inFunction) {
                        t1 = lookupLocalVariable(attr.getVarName());
                    } else {
                        t1 = lookupVariable(attr.getVarName());
                    }
                    if (t1 == null) throw new DereferenceNullException();
                    //Determine AttrFunc
                    Parser.AttrFunc func = parser.determineAttrFunc(attr.getAttr());
                    if (func == null) throw new InvalidGrammarException();
                    //Make method call with 'temp'
                    Type ret = t1.attrSet(func, temp);
                    scriptLog.reportAttrFunc(attr);
                    //if e is null, throw exception here
                    if (ret == null) throw new InvalidFunctionException();
                    //else, store value in types[i]
                    types[i] = ret;
                    break;
                case CONSTANT:
                    types[i] = parser.parseConstant(args[i]);
                    break;
                case VARIABLE:
                    List<Type> tempList = inFunction ? localStack : variableList;
                    for(Type t : tempList) {
                        if(t.getVariableName() != null && args[i].equals(t.getVariableName())) {
                            types[i] = t;
                            break;
                        }
                    }
                    break;
                default:
                    throw new InvalidFunctionException();
            }
        }
        return types;
    }

    protected void funcBuild() {
        scheduleManager.addTaskList(taskList);
        scheduleManager.buildSchedule();
    }

    protected void funcImportSchedule(String filename) {
        if(preProcessor.isBuild()) {
            scheduleManager.processJBinFile(filename);
        }
    }

    protected void funcExport(String filename) {
        //IOProcessing.writeJBinFile(file, JBin.createJBin());
    }

    //todo add your static functions down here...
}
