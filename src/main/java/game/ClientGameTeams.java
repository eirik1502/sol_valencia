package game;

import java.util.Arrays;

/**
 * Created by eirik on 28.07.2017.
 */
public class ClientGameTeams {

    private int[][] characterIds;
    private int controlCharacterTeam;
    private int controlCharacterIndex;


    public ClientGameTeams() {

    }
    public ClientGameTeams(int[][] characterIds, int controlCharacterTeam, int controlCharacterIndex) {
        this.characterIds = characterIds;
        this.controlCharacterTeam = controlCharacterTeam;
        this.controlCharacterIndex = controlCharacterIndex;
    }

    public int[] getCharacterIdsOnTeam(int team) {
        return characterIds[team];
    }

    public int[][] getCharacterIds() {
        return characterIds;
    }

    public void setCharacterIds(int[][] characterIds) {
        this.characterIds = characterIds;
    }

    public int getTeamCount() {
        return characterIds.length;
    }
    public int getTotalCharacterCount() {
        return Arrays.stream(characterIds).mapToInt(t -> t.length).sum();
    }


    public int getControlCharacterTeam() {
        return controlCharacterTeam;
    }

    public void setControlCharacterTeam(int controlCharacterTeam) {
        this.controlCharacterTeam = controlCharacterTeam;
    }

    public int getControlCharacterIndex() {
        return controlCharacterIndex;
    }

    public void setControlCharacterIndex(int controlCharacterIndex) {
        this.controlCharacterIndex = controlCharacterIndex;
    }

    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < characterIds.length; i++) {
            s += "Team"+i+"= " + Arrays.toString(characterIds[i]) + "\n";
        }
        s += "Client on team= " + getControlCharacterTeam() +"\n";
        s += "Client character index= " + getControlCharacterIndex() + "\n";

        return s;
    }
}
