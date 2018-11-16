package game.client.clientStates;

import engine.PositionComp;
import engine.WorldContainer;
import engine.graphics.ColoredMeshComp;
import engine.graphics.ColoredMeshUtils;
import engine.network.NetworkDataInput;
import engine.network.NetworkPregamePackets;
import engine.network.NetworkPregameUtils;
import engine.network.client.ClientState;
import engine.network.client.ClientStates;
import game.client.ClientIngame;
import game.ClientGameTeams;

/**
 * Created by eirik on 04.07.2017.
 */
public class ClientIngameState extends ClientState {


    private Thread gameThread;
    private ClientIngame game;

    private boolean playing;


    @Override
    public void init() {
        super.init();

        createInitialEntities(wc);
    }

    @Override
    public void onEnter() {

        playing = false;

        //tell server that we are ingame
        client.getTcpPacketOut().sendEmpty(NetworkPregamePackets.INGAME_CLIENT_READY);

        //wait for server to respond with initial team data

    }

    @Override
    public void onUpdate() {


        if (playing) {

            if (game.isShouldTerminate()) {
                System.out.println("Game exited, called from ingame state");

                //terminate game
                terminateGame();

                //clear net in
                System.out.println("Packets post game:\n"+client.getTcpPacketIn());
                client.getTcpPacketIn().clear();

                //show client window
                window.show();
                window.focus();

                //goto idle state
                setGotoState(ClientStates.IDLE);

//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }

            return;
        }

        //poll net if not in game
        client.getTcpPacketIn().pollPackets();

        //check if we got data from server
        NetworkDataInput dataIn = client.getTcpPacketIn().pollPacket(NetworkPregamePackets.INGAME_SERVER_CLIENT_GAME_TEAMS);
        if (dataIn != null) {

            //retrieve client teams
            ClientGameTeams teams = NetworkPregameUtils.packetToClientGameTeams(dataIn);

            //clear net in
            client.getTcpPacketIn().clear();

            //when going to game
            playing = true;
            window.hide();

            //create game
            System.out.println("Got data, creating game");
            createGame(teams);

        }

    }

    @Override
    public void onExit() {

    }

    private void createGame(ClientGameTeams teams) {
        game = new ClientIngame();
        game.init(client.getTcpPacketIn(), client.getTcpPacketOut(), teams);
        gameThread = new Thread(game);

        gameThread.start();
    }

    private void terminateGame() {
        game.terminate();

        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void createInitialEntities(WorldContainer wc) {
        int circ = wc.createEntity();
        wc.addComponent(circ, new PositionComp(client.getWindowWidth()/2, client.getWindowHeight()/2));
        wc.addComponent(circ, new ColoredMeshComp(ColoredMeshUtils.createCircleTwocolor(client.getWindowHeight()*(3/4), 32)));
    }
}
