package agile.planner.scripter.tools;

public class ScriptDebug {

    public static String add() {
        return null;
    }

    /*
    Reproduce statements but with debug

    Example:
    setup_task: task, label, card
        #this should be fine
        add: _label, _task
        add: _task, _card

    #data
    task: HW, 5, 0
    label: MA, green
    card: MATH

    #custom function call
    setup_task: _task, _label, _card

    OUTPUT:
    task: HW, 5, 0      [T0]
    label: MA, green    [L0]
    card: MATH          [C1]
    setup_task: _task, _label, _card    [T0], [L0], [C0]
      add: _label, _task  [L0] -> [T0]
      add: _task, _card   [T0] -> [C0]
     */

    /*
    One option is to write it so that it works similar to GCC (shitty, but impressive)
     */
}
