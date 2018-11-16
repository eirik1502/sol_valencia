package engine.network;

/**
 * Created by eirik on 27.07.2017.
 */
public class NetworkPregamePackets {

    public static final int
            //packet ids in states        CLIENT/SERVER is the program a packet is sendt from


            QUEUE_CLIENT_REQUEST_QUEUE_1V1 = 30,
            QUEUE_CLIENT_REQUEST_QUEUE_2V2 = 31,
            QUEUE_SERVER_PUT_IN_QUEUE = 32,
            QUEUE_SERVER_GOTO_CHARACTERSELECT = 33,
            QUEUE_CLIENT_EXIT = 34,

            CHARSELECT_CLIENT_CHOSE_CHARACTER = 37,
            CHARSELECT_SERVER_GOTO_GAME = 38,

            INGAME_CLIENT_READY = 40,
            INGAME_SERVER_CLIENT_GAME_TEAMS = 41,

            GAME_SERVER_EXIT = 42,
            GAME_CLIENT_EXIT = 43


//            //packet sizes
//            CHARSELECT_SERVER_GOTO_GAME_SIZE = 7 * Integer.BYTES;

                    ;
}
