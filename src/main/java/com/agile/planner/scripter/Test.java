package com.agile.planner.scripter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Test {

    public static void main(String[] args) throws IOException {
        System.out.println("Simple Script 1.0\n");
        ScriptFSM fsm = new ScriptFSM();
        File file = new File("data/");
        File[] list = file.listFiles();
        List<File> files = new ArrayList<>();
        assert list != null;
        System.out.println("Files:");
        int i = 0;
        for(File l : list) {
            if (l.getName().length() > 5 && ".smpl".equals(l.getName().substring(l.getName().length() - 5))) {
                files.add(l);
                System.out.println((files.size() - 1) + ". " + l.getName());
            }
            i++;
        }
        System.out.print("\nChoose from the above: ");
        Scanner input = new Scanner(System.in);
        if(input.hasNextInt()) {
            int idx = input.nextInt();
            if(idx >= 0 && idx < files.size()) {
//                String script = new String(Files.readAllBytes(Paths.get(files.get(idx).getAbsolutePath())));
                fsm.executeScript(files.get(idx).getAbsolutePath());
            }
        }
    }
}