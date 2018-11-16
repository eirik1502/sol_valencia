package game.client;

import engine.*;
import engine.graphics.text.Font;
import engine.graphics.text.FontType;
import engine.network.TcpPacketInput;
import engine.network.TcpPacketOutput;
import engine.network.client.ClientState;
import engine.network.client.ClientStateUtils;
import engine.network.client.ClientStates;
import engine.window.Window;
import engine.network.client.ClientStates;
import game.client.clientStates.*;

import java.io.IOException;
import java.net.Socket;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by eirik on 21.06.2017.
 */
public class Client {

//    public static final float WINDOW_WIDTH_SCALE = 0.7f, WINDOW_HEIGHT_SCALE = 0.7f;
    public static final float WINDOW_WIDTH_SCALE = 0.2f, WINDOW_HEIGHT_SCALE = 0.2f;

    public static final float CLIENT_WIDTH = 1600f, CLIENT_HEIGHT = 900f;


    public static final float FRAME_INTERVAL = 1f/60f;

    private String hostname;
    private Socket socket = null;

    private TcpPacketOutput tcpPacketOut;
    private TcpPacketInput tcpPacketIn;
//    private NetworkDataInput netInStream;
//    private NetworkDataOutput netOutStream;

    private Window window;
    private UserInput userInput;


    private ClientState currentState;
    private EnumMap<ClientStates, ClientState> states = new EnumMap<>(ClientStates.class);
    private Map<Integer, ClientState> statesById = new HashMap<>();


    private long lastTime;
    private boolean running = true;



    public Client(String hostname) {
        this.hostname = hostname;
    }

    public void setSocket(Socket socket) {
        if (this.socket == null) {
            this.socket = socket;

            try {
                tcpPacketIn = new TcpPacketInput( socket.getInputStream() );
                tcpPacketOut = new TcpPacketOutput( socket.getOutputStream() );

            } catch (IOException e) {
               throw new RuntimeException(e);
            }
        }
        else throw new IllegalStateException("Trying to set socket after it is already set");
    }

    public void removeSocket() {
        try {
            socket.close();
            tcpPacketOut.close();
            tcpPacketIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        socket = null;
        tcpPacketIn = null;
        tcpPacketOut = null;
    }

    public Socket getSocket() {
        return socket;
    }

    public TcpPacketInput getTcpPacketIn(){
        return tcpPacketIn;
    }
    public TcpPacketOutput getTcpPacketOut(){
        return tcpPacketOut;
    }

    public void terminate() {
        running = false;
    }

    public void init() {
        window = new Window(0.7f, "SOL client");

        userInput = new UserInput(window, CLIENT_WIDTH, CLIENT_HEIGHT);

        Font.loadFonts(FontType.BROADWAY);


        ClientIntroState clientIntroState = new ClientIntroState();
        ClientConnectingState clientConnectingState = new ClientConnectingState(hostname);
        ClientIdleState clientIdleState = new ClientIdleState();
        ClientWaitingState clientWaitingState = new ClientWaitingState();
        ClientCharacterselectState clientCharacterselectState = new ClientCharacterselectState();
        ClientIngameState clientIngameState = new ClientIngameState();

        //assign states
        states.put(ClientStates.INTRO, clientIntroState);
        states.put(ClientStates.CONNECTING, clientConnectingState);
        states.put(ClientStates.IDLE, clientIdleState);
        states.put(ClientStates.WAITING_GAME,clientWaitingState);
        states.put(ClientStates.CHOOSING_CHARACTER, clientCharacterselectState);
        states.put(ClientStates.INGAME, clientIngameState );

        statesById.put(ClientStateUtils.IDLE, clientIdleState);
        statesById.put(ClientStateUtils.CONNECTING, clientConnectingState);
        statesById.put(ClientStateUtils.WAITING_GAME, clientWaitingState);
        statesById.put(ClientStateUtils.CHOOSING_CHARACTER, clientCharacterselectState);
        statesById.put(ClientStateUtils.INGAME, clientIngameState);

        gotoState(ClientStates.INTRO);
    }

    public void start() {

        lastTime = System.nanoTime();

        float timeSinceUpdate = 0;

        while (running) {
            timeSinceUpdate += timePassed();
            //System.out.println("Time since update: "+timeSinceUpdate);

            if (timeSinceUpdate >= FRAME_INTERVAL) {
                timeSinceUpdate -= FRAME_INTERVAL;

                update();
            }


            if (window.shouldClosed() || userInput.isKeyboardPressed(UserInput.KEY_ESCAPE))
                break;
        }

        //end current state
        currentState.onExit();

        close();
    }

    public void close() {
        try {
            if (socket != null) {
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Window.terminateGLFW();
    }


    public void update() {
        window.pollEvents();

        //poll packets to make them available for read
        if (tcpPacketIn != null && currentState != states.get( ClientStates.INGAME ) ) {
            tcpPacketIn.pollPackets();

            if (tcpPacketIn.isRemoteSocketClosed()) {
                System.err.println("Server closed");

                removeSocket();
                gotoState(ClientStates.INTRO);
//                System.exit(3);

            }
            else {
                tcpPacketOut.sendHostAlive();
            }
        }

        //update according to state
        currentState.onUpdate();

        //update current entities
        currentState.getWorldContainer().updateSystems();

        ClientStates nextState = currentState.popGotoState();
        if (nextState != null) {
            gotoState(nextState);
        }

    }

    private void gotoState(ClientStates newStateType) {
        if (currentState != null) {
            currentState.onExit();
        }

        ClientState newState = states.get(newStateType);
        if (!newState.isInitialized()) {
            newState.setClientFields(this, window, userInput);
            newState.init();
        }
        currentState = newState;

        //render new state
        currentState.getWorldContainer().updateSystems();

        currentState.onEnter();
    }

    private void gotoStateById(int newStateType) {
        if (currentState != null) {
            currentState.onExit();
        }

        ClientState newState = statesById.get(newStateType);
        if (!newState.isInitialized()) {
            newState.setClientFields(this, window, userInput);
            newState.init();
        }
        currentState = newState;

        //render new state
        currentState.getWorldContainer().updateSystems();

        currentState.onEnter();
    }

    public float getWindowWidth() {
        return window.getWidth();
    }
    public float getWindowHeight() {
        return window.getHeight();
    }

    private float timePassed() {
        long newTime = System.nanoTime();
        int deltaTime = (int)(newTime - lastTime);
        float deltaTimeF = (float) deltaTime;

        lastTime = newTime;

        return deltaTimeF/1000000000;
    }

}
