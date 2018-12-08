package utils;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Created by eirik on 07.12.2018.
 */
public class FileUtilsTest {

    @Test
    public void createPrintFileTest() {
        //create a directory for our test
        String newdir = "logFileTest";
        File newDirLoc = new File(newdir);
        boolean dirCreated = newDirLoc.mkdir();

        Assert.assertTrue(dirCreated);

        String filepath1 = "fooTest";

        String newdirPath = newdir + "/";
        File file1 = new File(newdirPath+filepath1);

        //create logs
        PrintWriter pw1 = FileUtils.createPrintFile(file1);

        Assert.assertTrue(file1.exists());

        String content = "123123jh%(//&(YIHK";
        pw1.println(content);

        pw1.close();

        try {
            Scanner scanner = new Scanner(file1);

            Assert.assertTrue(scanner.hasNextLine());
            Assert.assertTrue(scanner.nextLine().equals(content));
            Assert.assertFalse(scanner.hasNextLine());

            scanner.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Assert.assertFalse(true);
        }


        boolean file1Deleted = file1.delete();
        Assert.assertTrue(file1Deleted);
        Assert.assertFalse(file1.exists());

        boolean dirDeleted = newDirLoc.delete();
        Assert.assertTrue(dirDeleted && !newDirLoc.exists());
    }


}
