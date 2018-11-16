package game.client.clientStates;

import engine.PositionComp;
import engine.UserInput;
import engine.WorldContainer;
import engine.graphics.MeshCenterComp;
import engine.graphics.TexturedMeshComp;
import engine.graphics.TexturedMeshUtils;
import engine.graphics.ViewRenderComp;
import engine.graphics.text.Font;
import engine.graphics.text.FontType;
import engine.graphics.text.TextMesh;
import engine.network.NetworkUtils;
import game.client.Client;
import engine.network.client.ClientState;
import engine.network.client.ClientStates;
import utils.maths.Vec4;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by eirik on 04.07.2017.
 */

public class ClientConnectingState extends ClientState {


    private String hostname;

    private int connectingTextEntity;
    private int ipTextEntity;
    private int statusTextEntity;
    private int gameLogoEntity;

    private boolean gettingIp;


    //for ip user input
    private String hostIpPreString = "Server IP: ";
    private String hostIpString = "";
    private char lastChar = 0;
    private final int maxIpChars = 3*4 + 3; //max ip address chars


    //connect status strings
    private String unknownHostStatus = "Unknown host given";
    private String ioExceptonStatus = "No server found on address given (IO exception)";

    //connecting strings
    private String connectingString = "Connecting...";
    private String connectEstablishedString = "connection established!";


    public ClientConnectingState(String hostname) {
        this.hostname = hostname;
    }

    @Override
    public void init() {
        super.init();

        createInitialEntities(wc);

    }

    @Override
    public void onEnter() {
        //do this on another thread.

        gettingIp = true;
    }

    @Override
    public void onUpdate() {

        if (!gettingIp){


            int connected = connectToServer(hostIpString);

            if (connected == 1) {
                //we have connected

                setGotoState(ClientStates.IDLE);
            }
            setEntityString(connectingTextEntity, "");

            if (connected == -1) {
                //unknown host
                setEntityString(statusTextEntity, unknownHostStatus);
            }
            else if (connected == -2) {
                //io exception
                setEntityString(statusTextEntity, ioExceptonStatus);
            }

//            try {
//                Thread.sleep(200);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            //return to getIp state
            gettingIp = true;

        }
        else {
            updateGetHostIp();

            //if a new key is pressed, remove status on screen
            if (lastChar != 0) {
                setEntityString(statusTextEntity, "");
            }

            //if host ip was commited, print it on screen
            if (!gettingIp) {
                setEntityString(connectingTextEntity, connectingString);
//                setEntityString(statusTextEntity, "");
            }
        }

    }

    @Override
    public void onExit() {

    }

    private void updateGetHostIp() {

        //commit ip address if enter is pressed, and return
        if (userInput.isKeyboardPressed(UserInput.KEY_ENTER)) {
            gettingIp = false;
            return;
        }

        char charPressed = 0;
        boolean stringEdited = false;

        //get a number if a number key is pressed
        char number = userInput.getNumberPressed();
        if (number != 0) {
            charPressed = number;
        }
        //get period if period key is pressed
        else if (userInput.isKeyboardPressed(UserInput.KEY_PERIOD)) {
            charPressed = UserInput.KEY_PERIOD;
        }

        //apply character if key pressed
        if (charPressed != 0 && charPressed != lastChar && hostIpString.length() < maxIpChars) {
            hostIpString += charPressed;
            stringEdited = true;
        }

        //remove a character is backspace is pressed for first time in sequence
        if (userInput.isKeyboardPressed(UserInput.KEY_BACKSPACE)) {
            charPressed = UserInput.KEY_BACKSPACE;
            if (charPressed != lastChar && hostIpString.length() > 0) {

                hostIpString = hostIpString.substring(0, hostIpString.length() - 1);

                stringEdited = true;
            }
        }

        lastChar = charPressed;

        //render new strig if edited
        if (stringEdited) {
            System.out.println(hostIpString);
            setEntityString(ipTextEntity, hostIpPreString + hostIpString);
        }
    }

    /**
     *
     * @param hostname
     * @return a status code: 1: connected, -1: invalid hostname, -2: io exception
     */
    private int connectToServer(String hostname) {
        try {
            System.out.println("Connecting to server");
            Socket socket = new Socket(hostname, NetworkUtils.PORT_NUMBER);
            System.out.println("Connection established!");

            client.setSocket(socket);
            return 1;
        }
        catch (UnknownHostException e) {
            System.err.println("Invalid hostname");
            return -1;
        }
        catch (IOException e) {
            System.err.println("An io exception occured while setting up socket\n could not connect to specified host");
            return -2;
        }
    }

    private void createInitialEntities(WorldContainer wc) {
        float width = Client.CLIENT_WIDTH/2, height = Client.CLIENT_HEIGHT/6;
        int rect = wc.createEntity();
        wc.addComponent(rect, new PositionComp(Client.CLIENT_WIDTH/2f, Client.CLIENT_HEIGHT/2f));
        //wc.addComponent(rect, new ColoredMeshComp(ColoredMeshUtils.createRectangle(width, height) ));
        wc.addComponent(rect, new MeshCenterComp(width/2f, height/2f));

        int ct = wc.createEntity(); //connecting to server text
        wc.addComponent(ct, new PositionComp(Client.CLIENT_WIDTH/2 - 400, Client.CLIENT_HEIGHT/2 -0));
        wc.addComponent(ct, new ViewRenderComp(new TextMesh("", Font.getFont(FontType.BROADWAY), 53, new Vec4(0.8f, 1, 0.8f, 1))));
        connectingTextEntity = ct;

        int it = wc.createEntity(); //ip input text
        wc.addComponent(it, new PositionComp(Client.CLIENT_WIDTH/2 - 400, Client.CLIENT_HEIGHT/2 -100));
        wc.addComponent(it, new ViewRenderComp(new TextMesh(hostIpPreString, Font.getFont(FontType.BROADWAY), 42, new Vec4(1, 1, 1, 1))));
        ipTextEntity = it;

        int st = wc.createEntity(); //error text
        wc.addComponent(st, new PositionComp(Client.CLIENT_WIDTH/2 - 400, Client.CLIENT_HEIGHT/2 -150));
        wc.addComponent(st, new ViewRenderComp(new TextMesh("", Font.getFont(FontType.BROADWAY), 32, new Vec4(1, 0, 0, 0.8f))));
        statusTextEntity = st;

        //create small sol logo
        gameLogoEntity = wc.createEntity("game logo");
        wc.addComponent(gameLogoEntity, new PositionComp(Client.CLIENT_WIDTH-320, Client.CLIENT_HEIGHT-180-20));
        wc.addComponent(gameLogoEntity, new TexturedMeshComp(
                TexturedMeshUtils.createRectangle("sol_logo.png", 320, 180)));
    }

    private void setEntityString(int textMeshEntity, String s) {
        ( (ViewRenderComp)wc.getComponent(textMeshEntity, ViewRenderComp.class) ).getTextMesh(0).setString(s);
    }
}
