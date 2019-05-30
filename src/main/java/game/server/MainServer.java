package game.server;

import game.GameUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Created by eirik on 22.06.2017.
 */
public class MainServer {

    public static void main(String[] args) {


        List<String> names = retrievePlayerNames();
        GameUtils.PLAYER_NAMES.addAll(names);

        Server s = new Server(false);
        s.init();
        s.start();
    }

    private static List<String> retrievePlayerNames() {
        Scanner nameScanner = new Scanner(System.in);

        System.out.println("\nEnter player names (space separated):");
        String names_str = nameScanner.nextLine();
        nameScanner.close();

        names_str = names_str.trim();
        return Arrays.asList(names_str.split("\\s+")); // regex matches with a string of spaces
    }
}
