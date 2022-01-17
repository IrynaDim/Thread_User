package com.dev.thread.user.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class FileReader {
    public static Queue<String> readFromFile(String fileName) {
        File file = new File(fileName);
        Queue<String> textFromFile = new ArrayDeque<>();
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
