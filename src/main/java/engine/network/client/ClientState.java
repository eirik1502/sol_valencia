package engine.network.client;

import engine.UserInput;
import engine.WorldContainer;
import engine.window.Window;
import game.client.Client;

/**
 * Created by eirik on 04.07.2017.
 */
public abstract class ClientState {

    protected Client client;

    protected Window window;
    protected UserInput userInput;

    protected WorldContainer wc;

    private boolean initialized = false;

    private ClientStates gotoState = null;


    public void setClientFields(Client client, Window window, UserInput userInput) {
        this.client = client;
        this.window = window;
        this.userInput = userInput;
    }



    public WorldContainer getWorldContainer() {
        return wc;
    }
    public boolean isInitialized() {
        return initialized;
    }

    public void init() {
        wc = ClientUtils.createDefaultWorldContainer(window, userInput);
        initialized = true;
    }

    public ClientStates popGotoState() {
        ClientStates s = gotoState;
        gotoState = null;
        return s;
    }



    public void setGotoState(ClientStates state) {
        gotoState = state;
    }



    public abstract void onEnter();

    public abstract void onUpdate();

    public abstract void onExit();

}
