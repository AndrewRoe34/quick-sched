package agile.planner.scripter;

import agile.planner.data.Card;
import agile.planner.data.Label;
import agile.planner.data.Linker;
import agile.planner.data.Task;
import agile.planner.scripter.exception.InvalidPairingException;
import agile.planner.util.CheckList;

import java.util.List;

public class Type implements Comparable<Type> {

    private Linker datatype;

    private final String variableName;

    private int variableIdx;

    public Type(Linker datatype, String variableName, int variableIdx) {
        this.variableName = variableName;
        setDatatype(datatype, variableIdx);
    }

    public Linker getDatatype() {
        return datatype;
    }

    public String getVariableName() {
        return variableName;
    }

    public int getVariableIdx() {
        return variableIdx;
    }

    public void setDatatype(Linker datatype, int variableIdx) {
        this.datatype = datatype;
        this.variableIdx = variableIdx;
    }

    public boolean addType(Type o) {
        return datatype.add(o.getDatatype());
    }

    public boolean removeType(Type o) {
        return datatype.remove(o.getDatatype());
    }

    /*
    Will create a method that passes an enum and a value with it
    this will allow utilizing attributes for various Linkers to work with

    ex:
    public boolean editAttr(Enum e, int val)
    public boolean editAttr(Enum e, String str)
    public boolean editAttr(Enum e, boolean flag)

     */

    @Override
    public int compareTo(Type o) {
        return variableName.compareTo(o.getVariableName());
    }

    @Override
    public String toString() {
        return datatype.toString();
    }
}
