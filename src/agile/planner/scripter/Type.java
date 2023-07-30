package agile.planner.scripter;

import agile.planner.data.Linker;

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

    public boolean addType(Type o) {
        return datatype.add(o.getDatatype());
    }

    public boolean removeType(Type o) {
        return datatype.remove(o.getDatatype());
    }

    public boolean editAttr(Enum e, Object val) {
        /*
        Will create a method that passes an enum and a value with it
        this will allow utilizing attributes for various Linkers to work with

        ex:
        public boolean editAttr(Enum e, Object val)

        */
        return false;
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
