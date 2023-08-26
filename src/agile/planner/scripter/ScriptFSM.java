package agile.planner.scripter;

import agile.planner.scripter.exception.DereferenceNullException;
import agile.planner.scripter.exception.InvalidFunctionException;
import agile.planner.scripter.exception.InvalidGrammarException;
import agile.planner.scripter.exception.InvalidPreProcessorException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class ScriptFSM {

    private List<Type> variableList = new LinkedList<>();
    private final Parser parser = new Parser();

    public void executeScript() throws FileNotFoundException {
        Scanner scriptScanner = new Scanner(new File("fun1.smpl"));
        while (scriptScanner.hasNextLine()) {
            String line = scriptScanner.nextLine();
            Parser.Operation operation = parser.typeOfOperation(line);
            switch (operation) {
                case COMMENT:
                    break;
                case PRE_PROCESSOR:
                    PreProcessor preProcessor = parser.parsePreProcessor(line);
                    if (preProcessor == null) throw new InvalidPreProcessorException();
                    processPreProcessor(preProcessor);
                    break;
                case ATTRIBUTE:
                    Attributes attr = parser.parseAttributes(line);
                    if (attr == null) throw new InvalidFunctionException();
                    processAttribute(attr);
                    break;
                case FUNCTION:
                    StaticFunction func = parser.parseStaticFunction(line);
                    if (func == null) throw new InvalidFunctionException();
                    processStaticFunction(func);
                    break;
                case INSTANCE:
                    ClassInstance classInstance = parser.parseClassInstance(line);
                    if (classInstance == null) {
                        //todo then it is a static function attempting to return a value
                        // also need to separate the tokens from the variable and the function
                    } else {
                        processClassInstance(classInstance);
                    }
                    break;
                case SETUP_CUST_FUNC:
                    //todo will work on later
                    break;
                case VARIABLE:
                case CONSTANT:
                    //do nothing here
                    break;
                default:
                    throw new InvalidGrammarException();
            }
        }
    }

    protected void processPreProcessor(PreProcessor preProcessor) {

    }

    protected void processClassInstance(ClassInstance classInstance) {
        //todo will need to update ClassInstance types to hold String[] args

        if (classInstance instanceof CardInstance) {

        } else if (classInstance instanceof TaskInstance) {

        } else if (classInstance instanceof LabelInstance) {

        } else {

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
                for (Type t : args)
                    System.out.print(t.toString());
                return null;
            default:

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
                case VARIABLE:
                    Type t1 = lookupVariable(args[i]);
                    if (t1 == null) throw new DereferenceNullException();
                    types[i] = t1;
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
                case CONSTANT:
                    types[i] = parser.parseConstant(args[i]);
                case VARIABLE:
                    //types[i] = parser.parseVariable(args[i]);
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

    protected Type variableLookup(String name) {
        return null;
    }

    //todo add your static functions down here...

}
