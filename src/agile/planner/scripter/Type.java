package agile.planner.scripter;

import agile.planner.data.Card;
import agile.planner.data.Label;
import agile.planner.data.Task;
import agile.planner.scripter.exception.InvalidPairingException;
import agile.planner.util.CheckList;

import java.util.List;

public class Type implements Comparable<Type> {

    private Object datatype;

    private final String variableName;

    private int variableIdx;

    public Type(Object datatype, String variableName, int variableIdx) {
        this.variableName = variableName;
        setDatatype(datatype, variableIdx);
    }

    public Object getDatatype() {
        return datatype;
    }

    public String getVariableName() {
        return variableName;
    }

    public int getVariableIdx() {
        return variableIdx;
    }

    public void setDatatype(Object datatype, int variableIdx) {
        this.datatype = datatype;
        this.variableIdx = variableIdx;
    }

    @Override
    public int compareTo(Type o) {
        return variableName.compareTo(o.getVariableName());
    }

    @Override
    public String toString() {
        switch(variableIdx) {
            case 0:
                return ((Task) datatype).toString();
            case 1:
                return ((Label) datatype).toString();
            case 2:
                return ((CheckList) datatype).toString();
            case 3:
                return ((Card) datatype).toString();
        }
        return null;
    }
}
