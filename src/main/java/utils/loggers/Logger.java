package utils.loggers;

import utils.FileUtils;

import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by eirik on 07.12.2018.
 */
public class Logger {

    private static final String fileExtension = ".log";
    private PrintWriter pw;


    //null logger
    public Logger() {
        pw = null;
    }
    public Logger(String logname) {
        this(logname, false);
    }
    public Logger(String logname, boolean uniquePerRun) {
        String filepath = "logs/" + logname;
        String timeext = "";

        if (uniquePerRun) {
            timeext = getCurrentDateTimeFileext();
        }

        //if the file exists, add a number to it
        int i = 0;
        File file;
        do {
            file = new File(filepath + (i == 0 ? "" : i) + timeext + fileExtension);
            ++i;
        }
        while(uniquePerRun && file.exists());

        pw = FileUtils.createPrintFile(file);

        println("Log created at: " + getCurrentDateTime());
    }

    public void printh1(String s) {
        if (pw == null) return;
        this.println(s.toUpperCase());
    }
    public void println(String s) {
        if (pw == null) return;
        pw.println(s);
    }
    public void printError(String s) {
        if (pw == null) return;
        pw.println("[ERROR] " + s);
    }
    public void printWarning(String s) {
        if (pw == null) return;
        pw.println("[warning] " + s);
    }
    public void println(float f) {
        if (pw == null) return;
        pw.println(f);
    }
    public void println(int i) {
        if (pw == null) return;
        pw.println(i);
    }

    public void close() {
        if (pw == null) return;
        pw.close();
    }

    private String getCurrentDateTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yy_HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
    private String getCurrentDateTimeFileext() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("_yyMMdd_HHmmss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
}
