package engine;

import engine.network.networkPackets.CharacterInputData;

/**
 * should be filled with relevant user input for a character each frame
 * Created by eirik on 15.06.2017.
 */
public class UserCharacterInputComp implements Component {

    private CharacterInputData clientData;


    public CharacterInputData getClientData() {
        return clientData;
    }

    public void setClientData(CharacterInputData clientData) {
        this.clientData = clientData;
    }

}
