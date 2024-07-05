package com.pms.core_bape_web.utils;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Logging {
    private String defaultLoggingLocation;

    public Logging () {
        this.defaultLoggingLocation = "./src/main/resources/hist.log";
    }

    public void writeLog(String content) {
        try (FileWriter fw = new FileWriter(this.defaultLoggingLocation, true)) {
            try (BufferedWriter bw = new BufferedWriter(fw)) {
                try (PrintWriter out = new PrintWriter(bw)) {
                    String currentTime = LocalDateTime.now().toString();
                    out.println("[" + currentTime + "] " + content);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public String loadLog() {
        StringBuilder content = new StringBuilder();
        try {
            File myObj = new File(this.defaultLoggingLocation);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                content.append(data).append("\n");
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return content.toString();
    }
}
