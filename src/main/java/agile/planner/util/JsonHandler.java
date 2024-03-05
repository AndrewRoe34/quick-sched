package agile.planner.util;

import agile.planner.data.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class JsonHandler {

    public static void main(String[] args) {
        Task t1 = new Task(0, "A", 3, 2);
        t1.addCheckList(new CheckList(0, "ToDo"));
        t1.getChecklist().addItem("Step1");
        t1.getChecklist().addItem("Step2");
        Gson gson = new GsonBuilder().setPrettyPrinting().create(); // for pretty printing
        String json = gson.toJson(t1);
        System.out.println(json);
    }
}
