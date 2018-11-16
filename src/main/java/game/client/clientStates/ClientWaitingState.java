package game.client.clientStates;

import engine.PositionComp;
import engine.WorldContainer;
import engine.graphics.TexturedMeshComp;
import engine.graphics.TexturedMeshUtils;
import engine.graphics.ViewRenderComp;
import engine.graphics.text.Font;
import engine.graphics.text.TextMesh;
import engine.network.*;
import engine.network.client.*;
import game.client.Client;

/**
 * Created by eirik on 04.07.2017.
 */
public class ClientWaitingState extends ClientState {

    private boolean inServerQueue;


    private int exitQueueButtonEntity;
    private int gameLogoEntity;

    private String exitQueueString = "Exit queue";

    private int queueTextEntity;
    private String preQueueString = "Requesting game queue";
    private String queueString = "In game queue";


//    private boolean ready = false;
//    private int nextMessageId;



    @Override
    public void init() {
        super.init();

        createInitialEntities(wc);
    }

    @Override
    public void onEnter() {


//        nextMessageId = -1;
        inServerQueue = false;
        ClientUtils.setEntityString(wc, queueTextEntity, preQueueString);

        //tell server that we want to go into queue
        sendGotoQueue();
    }

    @Override
    public void onUpdate() {

        TcpPacketInput in = client.getTcpPacketIn();

        //handle if client is put in queue
        if (in.removeIfHasPacket(NetworkPregamePackets.QUEUE_SERVER_PUT_IN_QUEUE)) {
            inServerQueue = true;
            ClientUtils.setEntityString(wc, queueTextEntity, queueString);
        }

        if (in.removeIfHasPacket(NetworkPregamePackets.QUEUE_SERVER_GOTO_CHARACTERSELECT)) {
            setGotoState(ClientStates.CHOOSING_CHARACTER);
        }

    }


    @Override
    public void onExit() {

    }


    private boolean sendGotoQueue() {
        //send data corresponding to say that we want to be put in queue


        //should check if server is disconnected
        return true;
    }

    private void exitQueue() {
        client.getTcpPacketOut().sendEmpty(NetworkPregamePackets.QUEUE_CLIENT_EXIT);
        setGotoState(ClientStates.IDLE);
    }


    private void createInitialEntities(WorldContainer wc) {
//        float width = Client.CLIENT_WIDTH/2;
//        float height = Client.CLIENT_HEIGHT/6;
//
////        int rect = wc.createEntity();
////        wc.addComponent(rect, new PositionComp(Client.CLIENT_WIDTH/2f, Client.CLIENT_HEIGHT/2f));
////        wc.addComponent(rect, new ColoredMeshComp(ColoredMeshUtils.createRectangle(width, height) ));
////        wc.addComponent(rect, new MeshCenterComp(width/2f, height/2f));


        //create queue text
        queueTextEntity = wc.createEntity();
        wc.addComponent(queueTextEntity, new PositionComp(ClientUtils.titleLeft, ClientUtils.titleTop));
        wc.addComponent(queueTextEntity, new ViewRenderComp(
                new TextMesh("", Font.getDefaultFont(), ClientUtils.titleTextSize, ClientUtils.titleTextColor ))
        );

        //create exit queue button
        exitQueueButtonEntity = ClientUtils.createButton(wc, ClientUtils.buttonsLeft, ClientUtils.buttonsTop, ClientUtils.buttonWidth, ClientUtils.buttonHeight,
                new TextMesh(exitQueueString, Font.getDefaultFont(), ClientUtils.buttonTextSize, ClientUtils.buttonTextColor),
                null,
                (b, a) -> exitQueue(),
                null, null
        );


        //create small sol logo
        gameLogoEntity = wc.createEntity("game logo");
        wc.addComponent(gameLogoEntity, new PositionComp(Client.CLIENT_WIDTH-320, Client.CLIENT_HEIGHT-180-20));
        wc.addComponent(gameLogoEntity, new TexturedMeshComp(
                TexturedMeshUtils.createRectangle("sol_logo.png", 320, 180)));
    }

}
