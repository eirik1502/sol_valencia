package engine.network.client;

/**
 * Created by eirik on 04.07.2017.
 */
public enum ClientStates {
    INTRO(0), CONNECTING(1), IDLE(2), WAITING_GAME(3), CHOOSING_CHARACTER(4), INGAME(5);


    public final int id;

    ClientStates(int id) {
        this.id = id;
    }


    private static int size = ClientStates.values().length;
    public static int size() {
        return size;
    }
}
