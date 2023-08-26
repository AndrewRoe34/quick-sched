package agile.planner.scripter;

import agile.planner.data.Card;
import agile.planner.data.DataAttr;
import agile.planner.data.Linker;
import agile.planner.scripter.exception.InvalidGrammarException;

public class Type implements Comparable<Type> {

    public enum TypeId {
        BOARD,
        CARD,
        TASK,
        LABEL,
        CHECKLIST
    }

    private Linker datatype;

    private final String variableName;

    private TypeId type;

    public Type(Linker datatype, String variableName, TypeId type) {
        this.variableName = variableName;
        setDatatype(datatype, type);
    }

    public Linker getDatatype() {
        return datatype;
    }

    public String getVariableName() {
        return variableName;
    }

    public TypeId getVariableIdx() {
        return type;
    }

    public void setDatatype(Linker datatype, TypeId type) {
        this.datatype = datatype;
        this.type = type;
    }

    private boolean addType(Type o) {
        return datatype.add(o.getDatatype());
    }

    private boolean removeType(Type o) {
        return datatype.remove(o.getDatatype());
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
    public Object attrSet(Parser.AttrFunc attr, String[] val) {
        switch(type) {
            case BOARD:
            case CARD:
                switch(attr) {
                    case SET_TITLE:
                        assert val.length == 1;
                        ((Card) datatype).setTitle(val[0]);
                        return null;
                    case GET_TITLE:
                        assert val.length == 0;
                        return ((Card) datatype).getTitle();
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
            default:
                return false;
        }
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
