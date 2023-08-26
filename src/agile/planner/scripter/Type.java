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
        //these constants CANNOT use attribute functions
        switch(type) {
            case INTEGER:
            case STRING:
            case BOOLEAN:
                throw new InvalidGrammarException();
        }

        switch(type) {
            case BOARD:
            case CARD:
                switch(attr) {
                    case SET_TITLE:
                        assert args.length == 1 && args[0].getStringConstant() != null;
                        ((Card) datatype).setTitle(args[0].getStringConstant());
                        return null;
                    case GET_TITLE:
                        assert args.length == 0;
                        return new Type(((Card) datatype).getTitle(), null);
//                    case ADD: todo need to uncomment
//                        assert val.length == 1 && val[0] instanceof Type;
//                        return addType((Type) val[0]);
//                    case REMOVE:
//                        assert val.length == 1 && val[0] instanceof Type;
//                        return removeType((Type) val[0]);
                    default:
                        throw new InvalidGrammarException();
                }
            case TASK:
            case LABEL:
            case CHECKLIST:
                break;
            default:
                return new Type(false, null);
        }
        return null;
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
        return datatype.toString();
    }
}
