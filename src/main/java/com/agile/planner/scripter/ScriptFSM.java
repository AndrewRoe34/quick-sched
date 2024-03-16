package com.agile.planner.scripter;

import com.agile.planner.models.Card;
import com.agile.planner.models.Label;
import com.agile.planner.models.Task;
import com.agile.planner.io.IOProcessing;
import com.agile.planner.manager.ScheduleManager;
import com.agile.planner.models.web.ScriptPage;
import com.agile.planner.scripter.tools.ScriptLog;
import com.agile.planner.scripter.exception.*;
import com.agile.planner.models.CheckList;
import com.agile.planner.util.EventLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

public class ScriptFSM {

    private final Scanner inputScanner = new Scanner(System.in);
    private final List<Type> globalStack = new LinkedList<>();
    private final Parser parser = new Parser();
    private final ScriptLog scriptLog = new ScriptLog();
    private List<Type> checklistVariables = new ArrayList<>();
    private List<Type> labelVariables = new ArrayList<>();
    private List<Type> taskVariables = new ArrayList<>();
    private List<Task> taskList = new ArrayList<>();
    private List<Type> cardVariables = new ArrayList<>();
    private List<Type> primitiveVariables = new ArrayList<>();
    private final Map<String, CustomFunction> funcMap = new HashMap<>();
    private PreProcessor preProcessor = null;
    private boolean ppStatus = false;
    private Scanner scriptScanner;
    private List<String> injectScript = new ArrayList<>();
    private int injectScriptIdx;
    private final ScheduleManager scheduleManager = ScheduleManager.getScheduleManager();

    public void executeScript(String filename) throws IOException {
        EventLog eventLog = scheduleManager.getEventLog();
        eventLog.reportScriptInstance(filename, true);
        scriptScanner = new Scanner(new File(filename));
        while (scriptScanner.hasNextLine() || !injectScript.isEmpty() && injectScriptIdx < injectScript.size()) {
            String untrimmed;
            if(!injectScript.isEmpty() && injectScriptIdx < injectScript.size()) {
                untrimmed = injectScript.get(injectScriptIdx++);
            } else {
                untrimmed = scriptScanner.nextLine();
            }
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
                            processAttribute(attr, null);
                            scriptLog.reportAttrFunc(attr);
                            break;
                        case FUNCTION:
                            if(!ppStatus) throw new InvalidPreProcessorException();
                            StaticFunction func = parser.parseStaticFunction(line);
                            processStaticFunction(func, null);
                            scriptLog.reportFunctionCall(func);
                            break;
                        case INSTANCE:
                            if(!ppStatus) throw new InvalidPreProcessorException();
                            ClassInstance classInstance = parser.parseClassInstance(line, globalStack, new ArrayList<>());
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
                                        ret = processAttribute(atr, null);
                                        scriptLog.reportAttrFunc(atr);
                                        break;
                                    case FUNCTION:
                                        StaticFunction fnc = parser.parseStaticFunction(trimmed);
                                        ret = processStaticFunction(fnc, null);
                                        scriptLog.reportFunctionCall(fnc);
                                        break;
                                    default:
                                        throw new InvalidPairingException();
                                }
                                if(ret == null) throw new InvalidGrammarException();
                                if(t1 == null) {
                                    ret.setVariableName(varName);
                                    globalStack.add(ret);
                                    scriptLog.reportVariableAssignment(ret);
                                } else {
                                    t1.setTypeVal(ret);
                                    scriptLog.reportVariableAssignment(t1);
                                }
                            } else {
                                Type t1 = processClassInstance(classInstance);
                                scriptLog.reportInstantiation(t1);
                                scriptLog.reportVariableAssignment(t1);
                            }
                            break;
                        case SETUP_CUST_FUNC:
                            if(!ppStatus) throw new InvalidPreProcessorException();
                            if(!injectScript.isEmpty() && injectScriptIdx < injectScript.size()) throw new InvalidGrammarException();
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
                        case IF_CONDITION:
                            if(!ppStatus) throw new InvalidPreProcessorException();
                            if(!injectScript.isEmpty() && injectScriptIdx < injectScript.size()) throw new InvalidGrammarException();
                            customFunction = parser.parseIfCondition(untrimmed);
                            if(customFunction == null) {
                                throw new InvalidFunctionException();
                            } else {
                                if("else".equals(customFunction.getFuncName())) {
                                    throw new InvalidGrammarException();
                                }
                                untrimmed = executeIfCondition(customFunction);
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
                        IOProcessing.writeScripterLogToFile(scriptLog.toString());
                    }
                    System.exit(1);
                }
                if(operation != Parser.Operation.SETUP_CUST_FUNC) {
                    status = false;
                }
            }
        }
        eventLog.reportScriptInstance(filename, false);
        if(preProcessor.isBuild()) {
            scheduleManager.outputScheduleToConsole();
        }
        if(preProcessor.isLog()) {
            IOProcessing.writeScripterLogToFile(scriptLog.toString());
            IOProcessing.writeSysLogToFile(eventLog.toString());
        }
        if(preProcessor.isHtml()) {
            File script = new File(filename);
            String scriptName = script.getName();
            String scriptStr = IOProcessing.readScriptFile(filename);
            ScriptPage scriptPage = new ScriptPage(scriptLog.toString(), eventLog.toString(), scriptStr, scriptName);
            IOProcessing.writeScripterPage(scriptPage.buildPage(), scriptName.substring(0, scriptName.length() - 5));
        }
    }

    private void executeStructureBlock(Parser.Operation operation, String line, List<Type> localStack) {
        switch (operation) {
            case COMMENT:
            case CONSTANT:
                break;
            case PRE_PROCESSOR:
                throw new InvalidPreProcessorException();
            case ATTRIBUTE:
                Attributes attr = parser.parseAttributes(line);
                if (attr == null) throw new InvalidFunctionException();
                processAttribute(attr, localStack);
                scriptLog.reportAttrFunc(attr);
                break;
            case FUNCTION:
                StaticFunction func = parser.parseStaticFunction(line);
                processStaticFunction(func, localStack);
                scriptLog.reportFunctionCall(func);
                break;
            case INSTANCE:
                ClassInstance classInstance = parser.parseClassInstance(line, globalStack, localStack);
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
                            ret = processAttribute(atr, localStack);
                            scriptLog.reportAttrFunc(atr);
                            break;
                        case FUNCTION:
                            StaticFunction fnc = parser.parseStaticFunction(trimmed);
                            ret = processStaticFunction(fnc, localStack);
                            scriptLog.reportFunctionCall(fnc);
                            break;
                        default:
                            throw new InvalidPairingException();
                    }
                    if(ret == null) throw new InvalidGrammarException();
                    if(t1 == null) {
                        ret.setVariableName(varName);
//                        localStack.add(ret); // need to replace, not just add
                        scriptLog.reportVariableAssignment(ret);
                        addAndReplaceVar(localStack, ret);
                    } else {
                        t1.setTypeVal(ret);
                        scriptLog.reportVariableAssignment(t1);
                    }
                } else {
                    Type ret = processClassInstance(classInstance);
                    localStack.add(ret);
                    globalStack.remove(ret);
                    scriptLog.reportVariableAssignment(ret);
                }
                break;
            default:
                throw new InvalidGrammarException();
        }
    }

    private void addAndReplaceVar(List<Type> localStack, Type val) {
        for(int i = 0; i < localStack.size(); i++) {
            if(localStack.get(i).getVariableName().equals(val.getVariableName())) {
                localStack.set(i, val);
                return;
            }
        }
        localStack.add(val);
    }

    protected boolean[] executeIfConditionFunc(CustomFunction ifCondition, List<Type> localStack) {
        Type[] args = processArguments(ifCondition.getArgs(), localStack);
        if(args.length == 0) throw new InvalidGrammarException();
        //need to execute the code here
        boolean status = true;
        for(Type t1 : args) {
            if(t1.getBoolConstant() == null) {
                throw new InvalidGrammarException();
            } else {
                status = t1.getBoolConstant();
                if(!status) {
                    break;
                }
            }
        }
        // 0 == boolean arg result, 1 == return status
        boolean[] results = {status, false};
        scriptLog.reportIfCondition(ifCondition.getArgs(), status);
        if(status) {
            for(String s : ifCondition.getLines()) {
                if(parser.typeOfOperation(s) == Parser.Operation.RETURN) {
                    results[1] = true;
                    return results;
                }
                executeStructureBlock(parser.typeOfOperation(s), s.trim(), localStack);
            }
        }
        return results;
    }

    protected boolean executeElseConditionFunc(CustomFunction ifCondition, List<Type> localStack) {
        scriptLog.reportIfCondition(ifCondition.getArgs(), true);
        for(String s : ifCondition.getLines()) {
            if(parser.typeOfOperation(s) == Parser.Operation.RETURN) {
                return true;
            }
            executeStructureBlock(parser.typeOfOperation(s), s.trim(), localStack);
        }
        return false;
    }

    protected String executeIfCondition(CustomFunction ifCondition) {
        String line = setupCustomFunction(ifCondition);
        Type[] args = processArguments(ifCondition.getArgs(), null);
        if(args.length != 1) throw new InvalidGrammarException();
        //need to execute the code here
        scriptLog.reportIfCondition(ifCondition.getArgs(), args[0].getBoolConstant());
        if(args[0].getBoolConstant()) {
            for(String s : ifCondition.getLines()) {
                executeStructureBlock(parser.typeOfOperation(s), s.trim(), null);
            }
        }
        return line;
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
            customFunction.addLine(line);
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
                globalStack.add(t1);
            } else {
                t1.setLinkerData(new Card(card.getTitle()), Type.TypeId.CARD);
            }
        } else if (classInstance instanceof TaskInstance) {
            TaskInstance task = (TaskInstance) classInstance;
            t1 = lookupVariable(task.getVarName());
            Task createdTask = new Task(0, task.getName(), task.getTotalHours(), task.getDueDate());
            if(t1 == null) {
                t1 = new Type(createdTask, task.getVarName(), Type.TypeId.TASK);
                globalStack.add(t1);
            } else {
                t1.setLinkerData(createdTask, Type.TypeId.TASK);
            }
            taskList.add(createdTask);
        } else if(classInstance instanceof CheckListInstance) {
            CheckListInstance cl = (CheckListInstance) classInstance;
            t1 = lookupVariable(cl.getVarName());
            if(t1 == null) {
                t1 = new Type(new CheckList(0, cl.getTitle()), cl.getVarName(), Type.TypeId.CHECKLIST);
                globalStack.add(t1);
            } else {
                t1.setLinkerData(new CheckList(0, cl.getTitle()), Type.TypeId.CHECKLIST);
            }
        } else if (classInstance instanceof LabelInstance) {
            LabelInstance label = (LabelInstance) classInstance;
            t1 = lookupVariable(label.getVarName());
            if(t1 == null) {
                t1 = new Type(new Label(0, label.getName(), label.getColor()), label.getVarName(), Type.TypeId.LABEL);
                globalStack.add(t1);
            } else {
                t1.setLinkerData(new Label(0, label.getName(), label.getColor()), Type.TypeId.LABEL);
            }
        } else if (classInstance instanceof StringInstance) {
            StringInstance str = (StringInstance) classInstance;
            t1 = lookupVariable(str.getVarName());
            if(t1 == null) {
                t1 = new Type(str.getStr(), str.getVarName());
                globalStack.add(t1);
            } else {
                t1.setStringConstant(str.getStr());
            }
        } else if(classInstance instanceof IntegerInstance) {
            IntegerInstance i = (IntegerInstance) classInstance;
            t1 = lookupVariable(i.getVarName());
            if(t1 == null) {
                t1 = new Type(i.getVal(), i.getVarName());
                globalStack.add(t1);
            } else {
                t1.setIntConstant(i.getVal());
            }
        } else if(classInstance instanceof BoolInstance) {
            BoolInstance bool = (BoolInstance) classInstance;
            t1 = lookupVariable(bool.getVarName());
            if(t1 == null) {
                t1 = new Type(bool.isVal(), bool.getVarName());
                globalStack.add(t1);
            } else {
                t1.setBoolConstant(bool.isVal());
            }
        }
        return t1;
    }

    protected Type processAttribute(Attributes attr, List<Type> localStack) {
        /*
        1. Lookup the variable
        2. Attempt to call the method via the Type method attrSet()
         */
        Type[] args = processArgumentsHelper(attr.getArgs(), localStack);
        Type t1 = null;
        if(localStack != null) {
            t1 = lookupLocalVariable(attr.getVarName(), localStack);
        }
        if(t1 == null) {
            t1 =  lookupVariable(attr.getVarName());
        }
        if (t1 == null) throw new DereferenceNullException();
        Parser.AttrFunc func = parser.determineAttrFunc(attr.getAttr());
        return t1.attrSet(func, args);
    }

    private String formatString(String s) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < s.length(); i++) {
            if(s.charAt(i) == '\\' && i + 1 < s.length()) {
                if(s.charAt(i + 1) == 'n') {
                    sb.append("\n");
                    i++;
                } else if(s.charAt(i + 1) == 't') {
                    sb.append("\t");
                    i++;
                }
            } else {
                sb.append(s.charAt(i));
            }
        }
        return sb.toString();
    }

    protected Type processStaticFunction(StaticFunction func, List<Type> localStack) {
        /*
        1. Lookup correct function
        2. Call processArguments() for Type array
        3. Call function with the specified arguments
         */
        Type[] args = processArguments(func.getArgs(), localStack);
        switch (func.getFuncName()) {
            case "build":
                funcBuild();
                return null;
            case "print":
                for (Type t : args) {
                    if(t.getVariabTypeId() == Type.TypeId.STRING && "\n".equals(t.getStringConstant())) {
                        System.out.println();
                    } else {
                        System.out.print(formatString(t.toString()));
                    }
                }
//                scriptLog.reportPrintFunc(args);
                return null;
            case "println":
                for (Type t : args) {
                    if(t.getVariabTypeId() == Type.TypeId.STRING && "\n".equals(t.getStringConstant())) {
                        System.out.println();
                    } else {
                        System.out.print(formatString(t.toString()));
                    }
                }
                System.out.println();
//                scriptLog.reportPrintFunc(args);
                return null;
            case "import_schedule":
                if(args.length != 1 || args[0].getVariabTypeId() != Type.TypeId.STRING) throw new InvalidFunctionException();
                funcImportSchedule(args[0].getStringConstant());
                return null;
            case "export_schedule":
                if(args.length != 1 || args[0].getVariabTypeId() != Type.TypeId.STRING) throw new InvalidFunctionException();
                funcExportSchedule(args[0].getStringConstant());
                return null;
            case "add_all_tasks":
                if(args.length != 0) throw new InvalidFunctionException();
                funcAddAllTasks();
                return null;
            case "input_tasks":
                if(args.length != 1 || args[0].getVariabTypeId() != Type.TypeId.INTEGER) throw new InvalidFunctionException();
                funcInputTasks(args[0].getIntConstant());
                return null;
            case "input_int":
                if(args.length > 1) throw new InvalidFunctionException();
                if(args.length == 1) {
                    return funcInputInt(args[0].getStringConstant());
                }
                return funcInputInt(null);
            case "input_word":
                if(args.length > 1) throw new InvalidFunctionException();
                if(args.length == 1) {
                    return funcInputWord(args[0].getStringConstant());
                }
                return funcInputWord(null);
            case "input_line":
                if(args.length > 1) throw new InvalidFunctionException();
                if(args.length == 1) {
                    return funcInputLine(args[0].getStringConstant());
                }
                return funcInputLine(null);
            case "pause":
                if(args.length != 0) throw new InvalidFunctionException();
                funcPause();
                return null;
            case "view_interface":
                if(args.length != 0) throw new InvalidFunctionException();
                funcViewInterface();
                return null;
            case "inject_code":
                if(localStack != null || args.length != 0) throw new InvalidFunctionException();
                funcInjectCode();
                return null;
            case "get_card":
                if(args.length != 1 || args[0].getVariabTypeId() != Type.TypeId.INTEGER) throw new InvalidFunctionException();
                return funcGetCard(args[0].getIntConstant());
            case "avg":
                if(args.length == 0) throw new InvalidFunctionException();
                int sum = 0;
                for(Type t1 : args) {
                    if(t1.getVariabTypeId() != Type.TypeId.INTEGER) throw new InvalidFunctionException();
                    sum += t1.getIntConstant();
                }
                return new Type(sum / args.length, null);
            case "view_stack":
                if(args.length != 0) throw new InvalidFunctionException();
                funcViewStack(localStack);
                return null;
            case "write_file":
                if(args.length != 2 || args[0].getVariabTypeId() != Type.TypeId.STRING) throw new InvalidFunctionException();
                funcWriteFile(args[0].getStringConstant(), formatString(args[1].toString()));
                return null;
            case "set_schedule":
                if(args.length != 1 || args[0].getVariabTypeId() != Type.TypeId.INTEGER) throw new InvalidFunctionException();
                int x = args[0].getIntConstant();
                if(x >= 1 && x <= 3) {
                    funcSetSchedule(args[0].getIntConstant());
                } else {
                    throw new InvalidFunctionException();
                }
                return null;
            case "google_export":
                if(args.length != 0) throw new InvalidFunctionException();
                funcGoogleExport();
                return null;
            case "google_import":
                if(args.length != 0) throw new InvalidFunctionException();
                funcGoogleImport();
                return null;
            default:
                processCustomFunction(func, args);
        }
        return null;
    }

    protected void processCustomFunction(StaticFunction func, Type[] args) {
        CustomFunction customFunction = funcMap.get(func.getFuncName());
        List<Type> localStack = new ArrayList<>();
        if(!"if".equals(func.getFuncName()) && customFunction.getArgs().length != args.length) throw new InvalidFunctionException();

        //add constructor for Type to include another Type (will maintain referencing easily
        if(!"if".equals(func.getFuncName())) {
            int j = 0;
            for(Type t : args) {
                localStack.add(new Type(t, customFunction.getArgs()[j]));
                j++;
            }
        }

        if(!"if".equals(func.getFuncName())) {
            //do nothing for now
        }

        boolean priorIfCondition = false;
        boolean priorIfFailed = false;
        for(int lineIdx = 0; lineIdx < customFunction.getLines().size(); lineIdx++) {
            String line = customFunction.getLines().get(lineIdx);
            String untrimmed = line;
            line = line.trim();
            Parser.Operation operation = parser.typeOfOperation(line);
            switch (operation) {
                case COMMENT:
                    break;
                case RETURN:
                    return;
                case ATTRIBUTE:
                    if(!ppStatus) throw new InvalidPreProcessorException();
                    Attributes attr = parser.parseAttributes(line);
                    if (attr == null) throw new InvalidFunctionException();
                    processAttribute(attr, localStack);
                    priorIfCondition = false;
                    priorIfFailed = false;
                    scriptLog.reportAttrFunc(attr);
                    break;
                case FUNCTION:
                    if(!ppStatus) throw new InvalidPreProcessorException();
                    StaticFunction myfunc = parser.parseStaticFunction(line);
                    processStaticFunction(myfunc, localStack);
                    priorIfCondition = false;
                    priorIfFailed = false;
                    scriptLog.reportFunctionCall(myfunc);
                    break;
                case IF_CONDITION:
                    if(!ppStatus) throw new InvalidPreProcessorException();
                    if("if".equals(func.getFuncName())) throw new InvalidGrammarException();
                    CustomFunction ifCondition = parser.parseIfCondition(untrimmed);
                    if(ifCondition == null) throw new InvalidGrammarException();
                    lineIdx++;
                    for(; lineIdx < customFunction.getLines().size(); lineIdx++) {
                        if(numSpaces(customFunction.getLines().get(lineIdx)) > ifCondition.getNumSpaces()) {
                            ifCondition.addLine(customFunction.getLines().get(lineIdx));
                        } else {
                            break;
                        }
                    }
                    //needed a way to obtain the return status and whether the condition block was executed
                    if("if".equals(ifCondition.getFuncName())) {
                        boolean[] status = executeIfConditionFunc(ifCondition, localStack);
                        if(status[1]) return;
                        priorIfCondition = true;
                        priorIfFailed = !status[0];
                    } else if("elif".equals(ifCondition.getFuncName()) && priorIfCondition && priorIfFailed) {
                        boolean[] status = executeIfConditionFunc(ifCondition, localStack);
                        if(status[1]) return;
                        priorIfFailed = !status[0];
                    } else if("else".equals(ifCondition.getFuncName()) && priorIfCondition && priorIfFailed) {
                        boolean status = executeElseConditionFunc(ifCondition, localStack);
                        if(status) return;
                        priorIfCondition = false;
                        priorIfFailed = false;
                    } else if("elif".equals(ifCondition.getFuncName()) && !priorIfCondition ||
                            "else".equals(ifCondition.getFuncName()) && !priorIfCondition) {
                        throw new InvalidGrammarException("Improper chaining of control structures");
                    }
//                    else {
//                        if(priorIfCondition && priorIfFailed) {
//                            if("elif".equals(ifCondition.getFuncName())) {
//                                boolean[] status = executeIfConditionFunc(ifCondition, localStack);
//                                if(status[1]) return;
//                                priorIfFailed = !status[0];
//                            } else {
//                                boolean status = executeElseConditionFunc(ifCondition, localStack);
//                                if(status) return;
//                                priorIfCondition = false;
//                                priorIfFailed = false;
//                            }
//                        } else if(!priorIfCondition) {
//                            throw new InvalidGrammarException();
//                        }
//                    }
                    lineIdx--;
                    break;
                case INSTANCE:
                    if(!ppStatus) throw new InvalidPreProcessorException();
                    ClassInstance classInstance = parser.parseClassInstance(line, globalStack, localStack);
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
                                ret = processAttribute(atr, localStack);
                                scriptLog.reportAttrFunc(atr);
                                break;
                            case FUNCTION:
                                StaticFunction fnc = parser.parseStaticFunction(trimmed);
                                ret = processStaticFunction(fnc, localStack);
                                scriptLog.reportFunctionCall(fnc);
                                break;
                            default:
                                throw new InvalidPairingException();
                        }
                        if(ret == null) throw new InvalidGrammarException();
                        if(t1 == null) {
                            ret.setVariableName(varName);
//                            localStack.add(ret);
                            addAndReplaceVar(localStack, ret);
                            scriptLog.reportVariableAssignment(ret);
                        } else {
                            t1.setTypeVal(ret);
                            scriptLog.reportVariableAssignment(t1);
                        }
                    } else {
                        Type ret = processClassInstance(classInstance);
                        localStack.add(ret);
                        globalStack.remove(ret);
                        scriptLog.reportVariableAssignment(ret);
                    }
                    priorIfCondition = false;
                    priorIfFailed = false;
                    break;
                default:
                    throw new InvalidGrammarException();
            }
        }
    }

    public Type lookupVariable(String token) {
        for (Type t : globalStack) {
            if (token.equals(t.getVariableName())) {
                return t;
            }
        }
        return null;
    }

    protected Type lookupLocalVariable(String token, List<Type> localStack) {
        for(Type t : localStack) {
            if (token.equals(t.getVariableName())) {
                return t;
            }
        }
        return null;
    }

    private Type[] processArgumentsHelper(String[] args, List<Type> localStack) {
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
                    if(localStack != null && localStack.size() > 0) {
                        t1 = lookupLocalVariable(args[i], localStack);
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

    protected Type[] processArguments(String[] args, List<Type> localStack) {
        /*
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
                    Type[] temp = processArguments(attr.getArgs(), localStack);
                    //Lookup variable (if null, throw exception)
                    Type t1 = null;
                    if(localStack != null && localStack.size() > 0) {
                        t1 = lookupLocalVariable(attr.getVarName(), localStack);
                    }
                    if (t1 == null) {
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
//                    List<Type> tempList = inFunction ? localStack : globalStack;
                    List<Type> tempList = localStack != null ? localStack : globalStack;
                    boolean flag = false;
                    for(Type t : tempList) {
                        if(t.getVariableName() != null && args[i].equals(t.getVariableName())) {
                            types[i] = t;
                            flag = true;
                            break;
                        }
                    }
                    if(!flag) {
                        for(Type t : globalStack) {
                            if(t.getVariableName() != null && args[i].equals(t.getVariableName())) {
                                types[i] = t;
                                break;
                            }
                        }
                    }
                    break;
                default:
                    throw new InvalidFunctionException();
            }
        }
        return types;
    }

    protected void funcAddAllTasks() {
        scheduleManager.addTaskList(taskList);
    }

    protected void funcBuild() {
        scheduleManager.buildSchedule();
    }

    protected void funcImportSchedule(String filename) {
        if(preProcessor.isBuild()) {
            scheduleManager.importJBinFile(filename);
        }
    }

    protected void funcExportSchedule(String filename) {
        if(preProcessor.isExprt()) {
            scheduleManager.exportJBinFile(filename);
        }
    }

    protected void funcInputTasks(int num) {
        System.out.println("Enter " + num + " Tasks: name, hours, due_date");
        for(int i = 0; i < num; i++) {
            System.out.print("> ");
            if(inputScanner.hasNextLine()) {
                String line = inputScanner.nextLine();
                //todo finish the rest and maybe give option to provide variable for label or checklist (maybe)
                String[] tokens = line.split(",");
                if(tokens.length > 3) {
                    throw new InvalidGrammarException();
                }
                scheduleManager.addTask(tokens[0].trim(), Integer.parseInt(tokens[1].trim()), Integer.parseInt(tokens[2].trim()));
            }
        }
    }

    protected  void funcInputCheckLists(int num) {
        //todo
    }

    protected Type funcInputInt(String stringConstant) {
        if(stringConstant != null) {
            System.out.print(stringConstant);
        }
        if(inputScanner.hasNextInt()) {
            Type localType = new Type(inputScanner.nextInt(), null);
            if(inputScanner.hasNextLine()) inputScanner.nextLine();
            return localType;
        } else {
            throw new InvalidGrammarException();
        }
    }

    protected Type funcInputWord(String stringConstant) {
        if(stringConstant != null) {
            System.out.print(stringConstant);
        }
        if(inputScanner.hasNext()) {
            Type localType = new Type(inputScanner.next(), null);
            if(inputScanner.hasNextLine()) inputScanner.nextLine();
            return localType;
        } else {
            throw new InvalidGrammarException();
        }
    }

    protected Type funcInputLine(String stringConstant) {
        if(stringConstant != null) {
            System.out.print(stringConstant);
        }
        if(inputScanner.hasNextLine()) {
            return new Type(inputScanner.nextLine(), null);
        } else {
            throw new InvalidGrammarException();
        }
    }

    protected void funcWriteFile(String filename, String contents) {
        PrintStream printStream = null;
        try {
            printStream = new PrintStream(filename);
        } catch (FileNotFoundException e) {
            throw new InvalidFunctionException();
        }
        printStream.print(contents);
    }

    protected void funcPause() {
        System.out.print("Enter 'y' to continue: ");
        while(inputScanner.hasNextLine()) {
            String line = inputScanner.nextLine().trim();
            if(line.length() > 0 && line.charAt(0) == 'y') {
                break;
            }
        }
    }

    /**
     * Outputs a text based version of Cards as a UI
     */
    protected void funcViewInterface() {
        List<Card> cards = scheduleManager.getCards();
        // use foreach loop to determine max number of tasks while printing out the first line of Cards
        int maxTasks = 0;
        for(Card c1 : cards) {
            maxTasks = Math.max(c1.getTask().size(), maxTasks);
            if(c1.toString().length() > 25) {
                System.out.print(c1.toString().substring(0, 25));
            } else {
                System.out.print(c1);
                for(int i = c1.toString().length(); i < 25; i++) {
                    System.out.print(" ");
                }
            }
            System.out.print("|");
        }

        System.out.println();
        for(int i = 0; i < cards.size(); i++) {
            System.out.print("--------------------------"); //will print out this line for the number of cards there are
        }

        // use foreach loop inside a for loop to output the tasks
        for(int i = 0; i < maxTasks; i++) {
            System.out.println();
            for(Card c1 : cards) {
                if(i < c1.getTask().size()) {
                    //print out the task (up to 18 characters)
                    Task t1 = c1.getTask().get(i);
                    String outputTask = t1.toString();
                    if(outputTask.length() > 25) {
                        System.out.print(outputTask.substring(0, 25));
                    } else {
                        System.out.print(outputTask);
                        for(int j = outputTask.length(); j < 25; j++) {
                            System.out.print(" ");
                        }
                    }
                    System.out.print("|");
                } else {
                    System.out.print("                         |");
                }
            }
        }
        System.out.println();
    }

    protected void funcInjectCode() {
        //todo need to list all global variables here
        System.out.println("GLOBAL STACK:");
        for(Type t1 : globalStack) {
            System.out.println("var_name=" + t1.getVariableName() + ",\tvar_value=" + t1);
        }
        injectScriptIdx = 0;
        System.out.println("__START__");
        injectScript = new ArrayList<>();
        while(inputScanner.hasNextLine()) {
            String line = inputScanner.nextLine();
            if("__END__".equals(line.trim())) {
                break;
            }
            injectScript.add(line);
        }
    }

    protected void funcViewStack(List<Type> localStack) {
        System.out.println("GLOBAL STACK:");
        for(Type t1 : globalStack) {
            if(t1.getStringConstant() != null) {
                System.out.println(t1.getVariableName() + ",\t\"" + t1 + "\"");
            } else {
                System.out.println(t1.getVariableName() + ",\t" + t1);
            }
        }
        System.out.println("LOCAL STACK:");
        if(localStack != null) {
            for(Type t1 : localStack) {
                if(t1.getStringConstant() != null) {
                    System.out.println(t1.getVariableName() + ",\t\"" + t1 + "\"");
                } else {
                    System.out.println(t1.getVariableName() + ",\t" + t1);
                }
            }
        }
    }

    protected Type funcGetCard(int idx) { //todo need to handle index out of bounds possibly
        return new Type(scheduleManager.getCards().get(idx), null, Type.TypeId.CARD);
    }

    protected void funcSetSchedule(int idx) {
        this.scheduleManager.setScheduleOption(idx);
    }

    protected void funcGoogleExport() {
        try {
            scheduleManager.exportScheduleToGoogle();
        } catch (IOException e) {
            throw new InvalidFunctionException();
        }
    }

    protected void funcGoogleImport() {
        try {
            scheduleManager.importScheduleFromGoogle();
        } catch (IOException e) {
            throw new InvalidFunctionException();
        }
    }

    protected void funcUpdateProfile() {
        /*
        set_schedule_alg(x)
        set_overflow(flag)
        ...

        This way, we can have a piece of software for editing the user's settings

        Goal is to have 2 core files:
        1. Managing system settings/configurations
        2. Data entry and viewing the schedule


        Sample code for File1.simpl:
        import: __CURR_CONFIG__

        reset_curr_settings()

        func prompt()
          println("Would you like to update a setting?")
          println("1. user_name=null")
          println("2. user_email=null")
          println("3. max_days=14")
          println("4. archive_days=14")
          println("5. priority=false")
          println("6. overflow=true")
          println("7. fit_schedule=false")
          println("8. schedule_algorithm=1")
          println("9. min_hours=1")
          println("10. exit()")

        for(true)
          prompt()
          x: input_int()
          if(x.equals(1))
            str: input_word()
            set_user_name(str)
          if(x.equals(2))
            ...
          ...

        update_curr_settings()
         */
    }

    //todo add your static functions down here...
}
