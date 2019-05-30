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
        String logDirName = "./logs/";

        // retrieve the path and name parts of the logname given
        int dirEnd = logname.lastIndexOf('/');
        if (dirEnd != -1) {
            logDirName += logname.substring(0, dirEnd+1);
            logname = logname.substring(dirEnd+1);
        }

        // create the path to the log if it doesnt exist
        File logDir = new File(logDirName);
        if (!logDir.exists())
            logDir.mkdir();

        // get the timedate tag
        String timeext = "";
        if (uniquePerRun) {
            timeext = getCurrentDateTimeFileext();
        }

        // check if the name is unique, else adda  number to its name
        int i = 0;
        File file;
        do {
            String uniqueTag = i++ == 0 ? "" : "_"+i;
            String filename = logDirName + timeext + uniqueTag + '_' + logname + fileExtension;
            file = new File(filename);
        }
        while(uniquePerRun && file.exists());

        pw = FileUtils.createPrintFile(file);

        // print the timedate to the start of the log
        println(timeext);
    }

    public void printh1(String s) {
        if (pw == null) return;
        this.println(s.toUpperCase());
    }
    public void println(String s) {
        if (pw == null) return;
        pw.println(s);
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
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yy-HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
    private String getCurrentDateTimeFileext() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyMMdd-HHmmss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
}
