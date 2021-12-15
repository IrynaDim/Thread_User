package com.dev.thread.user.worker;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FileReader {
    public static Queue<String> readFromFile(String fileName) {
        File file = new File(fileName);
        Queue<String> textFromFile = new ConcurrentLinkedQueue<>();
        try {
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                textFromFile.add(sc.nextLine().toLowerCase());
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Not correct file path.", e);
        }
        return textFromFile;
    }
}
