package tryingStuff;

import java.util.Scanner;

/**
 * Created by eirik on 05.12.2018.
 */
public class SystemScanner {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        while(true) {
            if (s.hasNextLine()) {
                System.out.println(s.nextLine());
            }
        }
    }
}
