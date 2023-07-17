package agile.planner.scripter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Test {

    public static void main(String[] args) throws IOException {
        ScriptContext sc = new ScriptContext();
        String script = new String(Files.readAllBytes(Paths.get("data/test2.smpl")));
        sc.executeScript(script);
    }
}