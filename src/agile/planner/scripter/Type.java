package agile.planner.scripter;

import agile.planner.data.Card;
import agile.planner.data.Linker;
import agile.planner.scripter.exception.InvalidGrammarException;

public class Type implements Comparable<Type> {

    public enum TypeId {
        BOARD,
        CARD,
        TASK,
        LABEL,
        CHECKLIST,
        INTEGER,
        BOOLEAN,
        STRING
    }

    private Linker datatype;

    private Integer intConstant;

    private Boolean boolConstant;

    private String stringConstant;

    private final String variableName;

    private TypeId type;

    public Type(Linker datatype, String variableName, TypeId type) {
        this.variableName = variableName;
        setLinkerData(datatype, type);
    }

    public Type(Integer intConstant, String variableName) {
        this.variableName = variableName;
        setIntConstant(intConstant);
    }

    public Type(Boolean boolConstant, String variableName) {
        this.variableName = variableName;
        setBoolConstant(boolConstant);
    }

    public Type(String stringConstant, String variableName) {
        this.variableName = variableName;
        setStringConstant(stringConstant);
    }

    public Linker getLinkerData() {
        return datatype;
    }

    public void setLinkerData(Linker datatype, TypeId type) {
        this.datatype = datatype;
        this.type = type;
    }

    public String getVariableName() {
        return variableName;
    }

    public TypeId getVariabTypeId() {
        return type;
    }

    public Integer getIntConstant() {
        return intConstant;
    }

    public void setIntConstant(Integer intConstant) {
        this.intConstant = intConstant;
        type = TypeId.INTEGER;
    }

    public Boolean getBoolConstant() {
        return boolConstant;
    }

    public void setBoolConstant(Boolean boolConstant) {
        this.boolConstant = boolConstant;
        type = TypeId.BOOLEAN;
    }

    public String getStringConstant() {
        return stringConstant;
    }

    public void setStringConstant(String stringConstant) {
        this.stringConstant = stringConstant;
        type = TypeId.STRING;
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
                    default:
                        throw new InvalidGrammarException();
                }
            case TASK:
            case LABEL:
            case CHECKLIST:
            case INTEGER:
                switch(attr) {
                    case ADD:
                        if(args.length == 0) throw new InvalidGrammarException();
                        for(Type t : args) {
                            if(t.getVariabTypeId() == TypeId.INTEGER) {
                                this.intConstant += t.getIntConstant();
                                return null;
                            } else {
                                throw new InvalidGrammarException();
                            }
                        }
                        break;
                    case SUBTRACT:
                        if(args.length == 0) throw new InvalidGrammarException();
                        for(Type t : args) {
                            if(t.getVariabTypeId() == TypeId.INTEGER) {
                                this.intConstant -= t.getIntConstant();
                                return null;
                            } else {
                                throw new InvalidGrammarException();
                            }
                        }
                        break;
                    case DIVIDE:
                        if(args.length == 0) throw new InvalidGrammarException();
                        for(Type t : args) {
                            if(t.getVariabTypeId() == TypeId.INTEGER) {
                                this.intConstant /= t.getIntConstant();
                                return null;
                            } else {
                                throw new InvalidGrammarException();
                            }
                        }
                        break;
                    case MULTIPLY:
                        if(args.length == 0) throw new InvalidGrammarException();
                        for(Type t : args) {
                            if(t.getVariabTypeId() == TypeId.INTEGER) {
                                this.intConstant *= t.getIntConstant();
                                return null;
                            } else {
                                throw new InvalidGrammarException();
                            }
                        }
                        break;
                    case MOD:
                        if(args.length == 0) throw new InvalidGrammarException();
                        for(Type t : args) {
                            if(t.getVariabTypeId() == TypeId.INTEGER) {
                                this.intConstant %= t.getIntConstant();
                                return null;
                            } else {
                                throw new InvalidGrammarException();
                            }
                        }
                        break;
                }
            case STRING:
                switch(attr) {
                    case ADD:
                        if(args.length == 0) throw new InvalidGrammarException();
                        StringBuilder sb = new StringBuilder(this.stringConstant);
                        for(Type t : args) {
                            sb.append(t.toString()); //todo need to add toString for Int, String, and Bool
                        }
                        setStringConstant(sb.toString());
                        return null;
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
                throw new InvalidGrammarException();
            default:
                return new Type(false, null);
        }
    }

    private boolean addType(Type o) {
        return datatype.add(o.getLinkerData());
    }

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
