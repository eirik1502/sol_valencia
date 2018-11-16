package game.client.clientStates;

import engine.PositionComp;
import engine.graphics.TexturedMeshComp;
import engine.graphics.TexturedMeshUtils;
import engine.graphics.ViewRenderComp;
import engine.graphics.text.Font;
import engine.graphics.text.FontType;
import engine.graphics.text.TextMesh;
import engine.network.NetworkPregamePackets;
import engine.network.client.*;
import game.client.Client;

/**
 * Created by eirik on 04.07.2017.
 */
public class ClientIdleState extends ClientState {


    private int welcomeTextEntity;
    private int connectedTextEntity;
    private int gameLogoEntity;

    private String[] buttonTexts;
    private int[] buttons;
    private OnButtonAction[] buttonReleaseActions;


    private String welcomeString = "You are connected!";
    private String connectedString = "WELCOME";


    private final int welcomeFadeTime = 60;
    private int welcomeFadeTimer;


    @Override
    public void init() {
        super.init();

        String[] bt = {"Play 1v1", "Play 2v2", "About the game"};
        OnButtonAction[] ba = {
                //Play 1v1
                (action, entity) -> {
                    setGotoState(ClientStates.WAITING_GAME);
                    //send packet to goto queue
                    client.getTcpPacketOut().sendEmpty(NetworkPregamePackets.QUEUE_CLIENT_REQUEST_QUEUE_1V1);
                },
                //Play 2v2
                (action, entity) -> {
                    setGotoState(ClientStates.WAITING_GAME);
                    //send packet to goto queue
                    client.getTcpPacketOut().sendEmpty(NetworkPregamePackets.QUEUE_CLIENT_REQUEST_QUEUE_2V2);
                },
                //About the game
                (action, entity) -> System.out.println("Should now goto character state")
        };
        buttonReleaseActions = ba;
        buttonTexts = bt;
        buttons = new int[bt.length];

        createInitialEntities();
    }

    @Override
    public void onEnter() {
        //print welcome string on screen, and start fade timer
        ClientUtils.setEntityString(wc, welcomeTextEntity, welcomeString);
        welcomeFadeTimer = welcomeFadeTime;

        ClientUtils.setEntityString(wc, connectedTextEntity, connectedString);

    }

    @Override
    public void onUpdate() {
        if (welcomeFadeTimer >= 0) {
            float colorAlpha = (float)welcomeFadeTimer / (float)welcomeFadeTime;
            ClientUtils.getEntityTextMesh(wc, welcomeTextEntity).getColor().w = colorAlpha;

            //if welcome text is invisible, remove it
            if (welcomeFadeTimer == 0) {
                ClientUtils.setEntityString(wc, welcomeTextEntity, "");
            }

            --welcomeFadeTimer;
        }
    }

    @Override
    public void onExit() {


    }

    private void createInitialEntities() {

        welcomeTextEntity = wc.createEntity();
        wc.addComponent(welcomeTextEntity, new PositionComp(ClientUtils.titleLeft, 100));
        wc.addComponent(welcomeTextEntity, new ViewRenderComp(new TextMesh("", Font.getFont(FontType.BROADWAY), 64, ClientUtils.titleTextColor) ));

        connectedTextEntity = wc.createEntity();
        wc.addComponent(connectedTextEntity, new PositionComp(ClientUtils.titleLeft, ClientUtils.titleTop));
        wc.addComponent(connectedTextEntity, new ViewRenderComp(new TextMesh("", Font.getFont(FontType.BROADWAY), ClientUtils.titleTextSize, ClientUtils.titleTextColor)));


        //create menu buttons
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = ClientUtils.createButton(wc,
                    ClientUtils.buttonsLeft, ClientUtils.buttonsTop+ i*(ClientUtils.buttonHeight+ClientUtils.buttonVertSpace),
                    ClientUtils.buttonWidth, ClientUtils.buttonHeight,
                    new TextMesh(buttonTexts[i], Font.getDefaultFont(), ClientUtils.buttonTextSize, ClientUtils.buttonTextColor),
                    null, buttonReleaseActions[i], null, null
            );

        }

        //create small sol logo
        gameLogoEntity = wc.createEntity("game logo");
        wc.addComponent(gameLogoEntity, new PositionComp(Client.CLIENT_WIDTH-320, Client.CLIENT_HEIGHT-180-20));
        wc.addComponent(gameLogoEntity, new TexturedMeshComp(
                TexturedMeshUtils.createRectangle("sol_logo.png", 320, 180)));

    }

}
