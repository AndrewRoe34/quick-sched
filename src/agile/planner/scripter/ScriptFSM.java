package agile.planner.scripter;

import agile.planner.data.Card;
import agile.planner.data.Label;
import agile.planner.scripter.exception.DereferenceNullException;
import agile.planner.scripter.exception.InvalidFunctionException;
import agile.planner.scripter.exception.InvalidGrammarException;
import agile.planner.scripter.exception.InvalidPreProcessorException;
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
    private List<Type> cardVariables = new ArrayList<>();
    private List<Type> primitiveVariables = new ArrayList<>();
    private Map<String, CustomFunction> funcMap = new HashMap<>();
    private boolean ppStatus = false;
    private Scanner scriptScanner;

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
                            processPreProcessor(preProcessor);
                            ppStatus = true;
                            break;
                        case ATTRIBUTE:
                            if(!ppStatus) throw new InvalidPreProcessorException();
                            Attributes attr = parser.parseAttributes(line);
                            if (attr == null) throw new InvalidFunctionException();
                            processAttribute(attr);
                            break;
                        case FUNCTION:
                            if(!ppStatus) throw new InvalidPreProcessorException();
                            StaticFunction func = parser.parseStaticFunction(line);
                            processStaticFunction(func);
                            break;
                        case INSTANCE:
                            if(!ppStatus) throw new InvalidPreProcessorException();
                            ClassInstance classInstance = parser.parseClassInstance(line);
                            if (classInstance == null) {
                                //todo then it is a static function attempting to return a value
                                // also need to separate the tokens from the variable and the function
                            } else {
                                processClassInstance(classInstance);
                            }
                            break;
                        case SETUP_CUST_FUNC:
                            if(!ppStatus) throw new InvalidPreProcessorException();
                            CustomFunction customFunction = parser.parseCustomFunction(untrimmed);
                            if(customFunction == null) {
                                throw new InvalidFunctionException();
                            } else {
                                untrimmed = processCustomFunction(customFunction);
                                if(untrimmed != null) {
                                    line = untrimmed.trim();
                                    status = true;
                                }
                            }
                            break;
                        case VARIABLE:
                        case CONSTANT:
                            if(!ppStatus) throw new InvalidPreProcessorException();
                            break;
                        default:
                            throw new InvalidGrammarException();
                    }
                } catch (Exception e) {
                    System.out.println("\u001B[31m" + e.getClass() + "\u001B[0m" + ": " + e.getMessage());
                }
                if(operation != Parser.Operation.SETUP_CUST_FUNC) {
                    status = false;
                }
            }

        }
    }

    protected String processCustomFunction(CustomFunction customFunction) {
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
        funcMap.put(customFunction.getFuncName(), customFunction);
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

    protected void processPreProcessor(PreProcessor preProcessor) {

    }

    protected void processClassInstance(ClassInstance classInstance) {
        if (classInstance instanceof CardInstance) {
            CardInstance card = (CardInstance) classInstance;
            Type t1 = lookupVariable(card.getVarName());
            if(t1 == null) {
                t1 = new Type(new Card(card.getTitle().substring(1, card.getTitle().length() - 1)), card.getVarName(), Type.TypeId.CARD);
                variableList.add(t1);
            } else {
                t1.setLinkerData(new Card(card.getTitle()), Type.TypeId.CARD);
            }
            scriptLog.reportCardCreation(0, card.getTitle()); //todo might want to include variable name (or not)
        } else if (classInstance instanceof TaskInstance) {

        } else if(classInstance instanceof CheckListInstance) {
            CheckListInstance cl = (CheckListInstance) classInstance;
            Type t1 = lookupVariable(cl.getVarName());
            if(t1 == null) {
                t1 = new Type(new CheckList(0, cl.getTitle()), cl.getVarName(), Type.TypeId.CHECKLIST);
                variableList.add(t1);
            } else {
                t1.setLinkerData(new CheckList(0, cl.getTitle()), Type.TypeId.CHECKLIST);
            }
        } else if (classInstance instanceof LabelInstance) {
            LabelInstance label = (LabelInstance) classInstance;
            Type t1 = lookupVariable(label.getVarName());
            if(t1 == null) {
                t1 = new Type(new Label(0, label.getName(), label.getColor()), label.getVarName(), Type.TypeId.LABEL);
                variableList.add(t1);
            } else {
                t1.setLinkerData(new Label(0, label.getName(), label.getColor()), Type.TypeId.LABEL);
            }
        } else if (classInstance instanceof StringInstance) {
            StringInstance str = (StringInstance) classInstance;
            Type t1 = lookupVariable(str.getVarName());
            if(t1 == null) {
                t1 = new Type(str.getStr(), str.getVarName());
                variableList.add(t1);
            } else {
                t1.setStringConstant(str.getStr());
            }
        } else if(classInstance instanceof IntegerInstance) {
            IntegerInstance i = (IntegerInstance) classInstance;
            Type t1 = lookupVariable(i.getVarName());
            if(t1 == null) {
                t1 = new Type(i.getVal(), i.getVarName());
                variableList.add(t1);
            } else {
                t1.setIntConstant(i.getVal());
            }
        } else if(classInstance instanceof BoolInstance) {
            BoolInstance bool = (BoolInstance) classInstance;
            Type t1 = lookupVariable(bool.getVarName());
            if(t1 == null) {
                t1 = new Type(bool.isVal(), bool.getVarName());
                variableList.add(t1);
            } else {
                t1.setBoolConstant(bool.isVal());
            }
        }
    }

    protected Type processAttribute(Attributes attr) {
        /*
        1. Lookup the variable
        2. Attempt to call the method via the Type method attrSet()
         */
        Type[] args = processArgumentsHelper(attr.getArguments());
        Type t1 = lookupVariable(attr.getVarName());
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
                return null;
            default:
                //todo use this for custom functions
        }
        return null;
    }

    public Type lookupVariable(String token) {
        for (Type t : variableList) {
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
                    Type t1 = lookupVariable(args[i]);
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
                    Type[] temp = processArgumentsHelper(attr.getArguments());
                    //Lookup variable (if null, throw exception)
                    Type t1 = lookupVariable(attr.getVarName());
                    if (t1 == null) throw new DereferenceNullException();
                    //Determine AttrFunc
                    Parser.AttrFunc func = parser.determineAttrFunc(attr.getAttr());
                    if (func == null) throw new InvalidGrammarException();
                    //Make method call with 'temp'
                    Type ret = t1.attrSet(func, temp);
                    //if e is null, throw exception here
                    if (ret == null) throw new InvalidFunctionException();
                    //else, store value in types[i]
                    types[i] = ret;
                    break;
                case CONSTANT:
                    types[i] = parser.parseConstant(args[i]);
                    break;
                case VARIABLE:
                    for(Type t : variableList) {
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

    }

    protected void funcImport(String file) {

    }

    protected void funcExport(String file) {

    }

    //todo add your static functions down here...


}
