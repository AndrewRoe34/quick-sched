package agile.planner.scripter;

import agile.planner.data.Card;
import agile.planner.data.Label;
import agile.planner.data.Linker;
import agile.planner.scripter.exception.InvalidGrammarException;
import agile.planner.util.CheckList;

/**
 * Manages and stores data for the specified variable within the Simple scripting language. It handles all core
 * methods and attributes with regards to the object being handled. Variables within Simple are dynamic in nature
 * and allow storing an instance of any kind. Garbage collection is processed by the JVM.
 *
 * @author Andrew Roe
 */
public class Type implements Comparable<Type> {

    /**
     * Represents the various Types that can be possessed by a variable at a specific moment (excluding NULL)
     */
    public enum TypeId {
        /** Enum value for the Board class */
        BOARD,
        /** Enum value for the Card class */
        CARD,
        /** Enum value for the Task class */
        TASK,
        /** Enum value for the Label class */
        LABEL,
        /** Enum value for the CheckList class */
        CHECKLIST,
        /** Enum value for the Integer class */
        INTEGER,
        /** Enum value for the Boolean class */
        BOOLEAN,
        /** Enum value for the String class */
        STRING
    }


    private Linker datatype;

    private Integer intConstant;

    private Boolean boolConstant;

    private String stringConstant;

    private String variableName;

    private TypeId type;

    /**
     * Constructor for Type that involves a data  specifically for scheduling purposes (Task, Card, Label, CheckList, etc.)
     *
     * @param datatype generic type of scheduling data that could possibly be linked with one another
     * @param variableName name of the variable at hand
     * @param type enum type for the object type
     */
    public Type(Linker datatype, String variableName, TypeId type) {
        this.variableName = variableName;
        setLinkerData(datatype, type);
    }

    /**
     * Constructor for Type that involves an Integer
     *
     * @param intConstant integer value
     * @param variableName name of the variable at hand
     */
    public Type(Integer intConstant, String variableName) {
        this.variableName = variableName;
        setIntConstant(intConstant);
    }

    /**
     * Constructor for Type that involves a Boolean
     *
     * @param boolConstant boolean value
     * @param variableName name of the variable at hand
     */
    public Type(Boolean boolConstant, String variableName) {
        this.variableName = variableName;
        setBoolConstant(boolConstant);
    }

    /**
     * Constructor for Type that involves a String
     *
     * @param stringConstant String value
     * @param variableName name of the variable at hand
     */
    public Type(String stringConstant, String variableName) {
        this.variableName = variableName;
        setStringConstant(stringConstant);
    }

    /**
     * Constructor for Type that involves cloning the data of another Type
     *
     * @param t1 Type being cloned
     * @param variableName name of the variable at hand
     */
    public Type(Type t1, String variableName) {
        this.variableName = variableName;
        setTypeVal(t1);
    }

    /**
     * Sets the new Type for the variable given another Type
     *
     * @param ret Type of other variable
     */
    public void setTypeVal(Type ret) {
        switch(ret.getVariabTypeId()) {
            case INTEGER:
                setIntConstant(ret.getIntConstant());
                break;
            case STRING:
                setStringConstant(ret.getStringConstant());
                break;
            case BOOLEAN:
                setBoolConstant(ret.getBoolConstant());
                break;
            case CARD:
            case LABEL:
            case CHECKLIST:
            case TASK:
            case BOARD:
                setLinkerData(ret.getLinkerData(), ret.getVariabTypeId());
                break;
        }
    }

    /**
     * Gets the Linker object specifically for scheduling purposes
     *
     * @return Linker object
     */
    public Linker getLinkerData() {
        return datatype;
    }

    /**
     * Sets the Linker object for the given variable
     *
     * @param datatype new Linker object being stored
     * @param type enum type for the Linker object
     */
    public void setLinkerData(Linker datatype, TypeId type) {
        reset();
        this.datatype = datatype;
        this.type = type;
    }

    /**
     * Gets the variable name
     *
     * @return name of variable
     */
    public String getVariableName() {
        return variableName;
    }

    /**
     * Sets name for variable
     * @param name name for variable
     */
    public void setVariableName(String name) {
        this.variableName = name;
    }

    /**
     * Gets the enum type id for the variable object
     *
     * @return enum type id
     */
    public TypeId getVariabTypeId() {
        return type;
    }

    /**
     * Gets the integer value from the variable
     *
     * @return integer value
     */
    public Integer getIntConstant() {
        return intConstant;
    }

    /**
     * Sets the integer value for the variable
     *
     * @param intConstant integer value
     */
    public void setIntConstant(Integer intConstant) {
        reset();
        this.intConstant = intConstant;
        type = TypeId.INTEGER;
    }

    /**
     * Gets the boolean value from the variable
     *
     * @return boolean value
     */
    public Boolean getBoolConstant() {
        return boolConstant;
    }

    /**
     * Sets the boolean value for the variable
     *
     * @param boolConstant boolean value
     */
    public void setBoolConstant(Boolean boolConstant) {
        reset();
        this.boolConstant = boolConstant;
        type = TypeId.BOOLEAN;
    }

    /**
     * Gets the String value from the variable
     *
     * @return String value
     */
    public String getStringConstant() {
        return stringConstant;
    }

    /**
     * Sets the String value for the variable
     *
     * @param stringConstant String value
     */
    public void setStringConstant(String stringConstant) {
        reset();
        this.stringConstant = stringConstant;
        type = TypeId.STRING;
    }

    /**
     * Resets the object data for the variable
     */
    public void reset() {
        this.intConstant = null;
        this.boolConstant = null;
        this.stringConstant = null;
        this.datatype = null;
    }

    /*
        Categories when using "." operator:
        t1.<retrieval> [t1.title, t1.hours]
        t1.<edit>      [t1.title: "HW", t1.hours: 3]
        t1.<add>
        t1.<removal>
        t1.<func>

        String: c1.title
        Tokenize by '.': [c1, title]
        Lookup c1 for actual variable reference
            If exists, continue
            Else, null pointer
        Tokenize by ',' for attr operation: []
        Use switch logic to find correct enum value for attr operation
        Call 'attrSet' with enum value and array of arguments
     */

    /**
     * Determines the appropriate method to call for the given type given the method name and arguments.
     *
     * @param attr attribute function enum to determine correct method
     * @param args given arguments for the specified method
     * @return Type based on result of method (NULL represents void return)
     */
    public Type attrSet(Parser.AttrFunc attr, Type[] args) {
        switch(type) {
            case BOARD:
            case CARD:
                switch(attr) {
                    case SET_TITLE:
                        if(args.length != 1) throw new InvalidGrammarException();
                        ((Card) datatype).setTitle(args[0].getStringConstant());
                        return null;
                    case GET_TITLE:
                        if(args.length != 0) throw new InvalidGrammarException();
                        return new Type(((Card) datatype).getTitle(), null);
                    case ADD:
                        if(args.length != 1) throw new InvalidGrammarException();
                        return new Type(addType(args[0]), null);
                    default:
                        throw new InvalidGrammarException();
                }
            case TASK:
            case LABEL:
                switch(attr) {
                    case GET_TITLE:
                        if(args.length != 0) throw new InvalidGrammarException();
                        return new Type(((Label) datatype).getName(), null);
                    case GET_COLOR:
                        if(args.length != 0) throw new InvalidGrammarException();
                        return new Type(((Label) datatype).getColor(), null);
                    case SET_TITLE:
                        if(args.length != 1) throw new InvalidGrammarException();
                        ((Label) datatype).setName(args[0].getStringConstant());
                        return null;
                    case SET_COLOR:
                        if(args.length != 1) throw new InvalidGrammarException();
                        ((Label) datatype).setColor(args[0].getIntConstant());
                        return null;
                    default:
                        throw new InvalidGrammarException();
                }
            case CHECKLIST:
                switch(attr) {
                    case GET_ID:
                        if(args.length != 0) throw new InvalidGrammarException();
                        return new Type(((CheckList) datatype).getChecklistId(), null);
                    case GET_TITLE:
                        if(args.length != 0) throw new InvalidGrammarException();
                        return new Type(((CheckList) datatype).getName(), null);
                    case SET_TITLE:
                        if(args.length != 1) throw new InvalidGrammarException();
                        ((CheckList) datatype).setName(args[0].getStringConstant());
                        return null;
                    case GET_PERCENT:
                        if(args.length != 0) throw new InvalidGrammarException();
                        return new Type(((CheckList) datatype).getPercentage(), null);
                    case ADD_ITEM:
                        if(args.length != 1) throw new InvalidGrammarException();
                        boolean status = ((CheckList) datatype).addItem(args[0].getStringConstant());
                        return new Type(status, null);
                    case REMOVE_ITEM_BY_ID:
                        if(args.length != 1) throw new InvalidGrammarException();
                        ((CheckList) datatype).removeItemById(args[0].getIntConstant());
                        return null;
                    case REMOVE_ITEM_BY_NAME:
                        if(args.length != 1) throw new InvalidGrammarException();
                        ((CheckList) datatype).removeItemByName(args[0].getStringConstant());
                        return null;
                    case MARK_ITEM_BY_ID:
                        if(args.length != 2) throw new InvalidGrammarException();
                        ((CheckList) datatype).markItemById(args[0].getIntConstant(), args[1].getBoolConstant());
                        return null;
                    case MARK_ITEM_BY_NAME:
                        if(args.length != 2) throw new InvalidGrammarException();
                        ((CheckList) datatype).markItemByName(args[0].getStringConstant(), args[1].getBoolConstant());
                        return null;
                    default:
                        throw new InvalidGrammarException();
                }
            case INTEGER:
                switch(attr) {
                    case ADD_ONE: //todo need to integrate for /, *, %
                        if(args.length == 0) {
                            this.intConstant++;
                            return new Type(getIntConstant(), null);
                        }
                        for(Type t : args) {
                            if(t.getVariabTypeId() == TypeId.INTEGER) {
                                this.intConstant += t.getIntConstant();
                            } else {
                                throw new InvalidGrammarException();
                            }
                        }
                        return new Type(getIntConstant(), null);
                    case SUBTRACT_ONE:
                        if(args.length == 0) {
                            this.intConstant--;
                            return new Type(getIntConstant(), null);
                        }
                        for(Type t : args) {
                            if(t.getVariabTypeId() == TypeId.INTEGER) {
                                this.intConstant -= t.getIntConstant();
                            } else {
                                throw new InvalidGrammarException();
                            }
                        }
                        return new Type(getIntConstant(), null);
                    case DIVIDE_ONE:
                        if(args.length == 0) throw new InvalidGrammarException();
                        for(Type t : args) {
                            if(t.getVariabTypeId() == TypeId.INTEGER) {
                                this.intConstant /= t.getIntConstant();
                            } else {
                                throw new InvalidGrammarException();
                            }
                        }
                        return new Type(getIntConstant(), null);
                    case MULTIPLY_ONE:
                        if(args.length == 0) throw new InvalidGrammarException();
                        for(Type t : args) {
                            if(t.getVariabTypeId() == TypeId.INTEGER) {
                                this.intConstant *= t.getIntConstant();
                            } else {
                                throw new InvalidGrammarException();
                            }
                        }
                        return new Type(getIntConstant(), null);
                    case MOD_ONE:
                        if(args.length == 0) throw new InvalidGrammarException();
                        for(Type t : args) {
                            if(t.getVariabTypeId() == TypeId.INTEGER) {
                                this.intConstant %= t.getIntConstant();
                            } else {
                                throw new InvalidGrammarException();
                            }
                        }
                        return new Type(getIntConstant(), null);
                    case MOD:
                        if(args.length == 0) throw new InvalidGrammarException();
                        int val = this.intConstant;
                        for(Type t : args) {
                            if(t.getVariabTypeId() == TypeId.INTEGER) {
                                val %= t.getIntConstant();
                            } else {
                                throw new InvalidGrammarException();
                            }
                        }
                        return new Type(val, null);
                    case EQUALS:
                        if(args.length != 1) throw new InvalidGrammarException();
                        return new Type(this.intConstant.equals(args[0].getIntConstant()), null);
                    case LESS_THAN:
                        if(args.length != 1) throw new InvalidGrammarException();
                        return new Type(this.intConstant < args[0].getIntConstant(), null);
                    case GREATER_THAN:
                        if(args.length != 1) throw new InvalidGrammarException();
                        return new Type(this.intConstant > args[0].getIntConstant(), null);
                    case LESSER_EQUALS:
                        if(args.length != 1) throw new InvalidGrammarException();
                        return new Type(this.intConstant <= args[0].getIntConstant(), null);
                    case GREATER_EQUALS:
                        if(args.length != 1) throw new InvalidGrammarException();
                        return new Type(this.intConstant >= args[0].getIntConstant(), null);
                    case NOT_EQUAL:
                        if(args.length != 1) throw new InvalidGrammarException();
                        return new Type(!this.intConstant.equals(args[0].getIntConstant()), null);
                }
            case STRING:
                switch(attr) {
                    case CONCATENATE:
                        if(args.length == 0) throw new InvalidGrammarException();
                        StringBuilder sb = new StringBuilder(this.stringConstant);
                        for(Type t : args) {
                            sb.append(t.toString());
                        }
                        this.stringConstant = sb.toString();
                        return this;
                    case ADD:
                        if(args.length == 0) throw new InvalidGrammarException();
                        sb = new StringBuilder(this.stringConstant);
                        for(Type t : args) {
                            sb.append(t.toString());
                        }
                        //setStringConstant(sb.toString());
                        return new Type(sb.toString(), null);
                    case LENGTH:
                        if(args.length != 0) throw new InvalidGrammarException();
                        return new Type(this.stringConstant.length(), null);
                    case PARSE_INT:
                        if(args.length != 0) throw new InvalidGrammarException();
                        return new Type(Integer.parseInt(this.stringConstant), null);
                    case PARSE_BOOL:
                        if(args.length != 0) throw new InvalidGrammarException();
                        return new Type(Boolean.parseBoolean(this.stringConstant), null);
                    case SUB_STRING:
                        if(args.length == 0 || args.length > 2 || args[0].getVariabTypeId() != TypeId.INTEGER
                                || args.length == 2 && args[1].getVariabTypeId() != TypeId.INTEGER) {
                            throw new InvalidGrammarException();
                        }
                        if(args.length == 1) {
                            return new Type(this.stringConstant.substring(args[0].getIntConstant()), null);
                        } else {
                            return new Type(this.stringConstant.substring(args[0].getIntConstant(),
                                    args[1].getIntConstant()), null);
                        }
                }
            case BOOLEAN:
            default:
                throw new InvalidGrammarException();
        }
    }

    /**
     * Adds a Linker type to another Linker type if possible
     * @param o other Type variable
     * @return boolean status for success
     */
    private boolean addType(Type o) {
        return datatype.add(o.getLinkerData());
    }

    /**
     * Removes a Linker type from another Linker type if possible
     * @param o other Type variable
     * @return boolean status for success
     */
    private boolean removeType(Type o) {
        return datatype.remove(o.getLinkerData());
    }

    @Override
    public int compareTo(Type o) {
        return variableName.compareTo(o.getVariableName());
    }

    @Override
    public String toString() {
        switch(type) {
            case STRING:
                return stringConstant;
            case INTEGER:
                return "" + intConstant;
            case BOOLEAN:
                return "" + boolConstant;
        }
        return datatype.toString();
    }
}
