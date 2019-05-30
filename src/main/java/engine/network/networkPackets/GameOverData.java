package engine.network.networkPackets;

import engine.network.NetworkUtils;

/**
 * Created by eirik on 31.07.2017.
 */
public class GameOverData implements NetworkPacket{

    public static final int BYTES = Integer.BYTES;


    public int teamWon;


    public GameOverData(int teamWon) {
        this.teamWon = teamWon;
    }

    public GameOverData() {
    }

    @Override
    public int getPacketId() {
        return NetworkUtils.SERVER_GAME_OVER_ID;
    }

    public String serialize() {
        return ""+teamWon;
    }
}
