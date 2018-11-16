package engine.network.networkPackets;

/**
 * Created by eirik on 31.07.2017.
 */
public class GameOverData {

    public static final int BYTES = Integer.BYTES;


    public int teamWon;


    public GameOverData(int teamWon) {
        this.teamWon = teamWon;
    }

    public GameOverData() {
    }
}
