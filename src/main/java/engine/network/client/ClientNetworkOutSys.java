package engine.network.client;

import engine.Sys;
import engine.UserCharacterInputComp;
import engine.UserInput;
import engine.WorldContainer;
import engine.network.NetworkDataOutput;
import engine.network.TcpPacketOutput;
import engine.network.networkPackets.CharacterInputData;
import engine.network.NetworkUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by eirik on 04.07.2017.
 */
public class ClientNetworkOutSys implements Sys {


    private WorldContainer wc;

    private TcpPacketOutput tcpPacketOut;

    private UserInput userInput;


    public ClientNetworkOutSys(TcpPacketOutput tcpPacketOut, UserInput userInput) {
        this.tcpPacketOut = tcpPacketOut;

        this.userInput = userInput;

    }

    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
    }


    @Override
    public void update() {
        updateServerByInput();
    }

    private void updateServerByInput() {

        //retrieve character input from the entity that is controlled by the user
        CharacterInputData input = null;
        for (int userInEntity : wc.getEntitiesWithComponentType(UserCharacterInputComp.class) ) {
            UserCharacterInputComp userInComp = (UserCharacterInputComp) wc.getComponent(userInEntity, UserCharacterInputComp.class);

            input = userInComp.getClientData();
        }
        if (input == null) throw new IllegalStateException("No data in UserCharacterInputComp");

        //send input data
        sendInputData(input);
    }

    /**
     * retrieve input straight from user as of now
     * @return
     */
    public CharacterInputData retrieveUserInput() {
        CharacterInputData input = new CharacterInputData();

        input.setMoveLeft(userInput.isKeyboardPressed(UserInput.KEY_A));
        input.setMoveRight(userInput.isKeyboardPressed(UserInput.KEY_D));
        input.setMoveUp(userInput.isKeyboardPressed(UserInput.KEY_W));
        input.setMoveDown(userInput.isKeyboardPressed(UserInput.KEY_S));

        input.setAction1(userInput.isMousePressed(UserInput.MOUSE_BUTTON_1));
        input.setAction2(userInput.isMousePressed(UserInput.MOUSE_BUTTON_2));
        input.setAction3(userInput.isKeyboardPressed(UserInput.KEY_SPACE));

        input.setAimX(userInput.getMouseX());
        input.setAimY(userInput.getMouseY());

        return input;
    }

    public void sendInputData(CharacterInputData id) {
        //translate data
        NetworkDataOutput dataOut = NetworkUtils.characterInputToPacket(id);

        //send
        tcpPacketOut.send(NetworkUtils.CLIENT_CHARACTER_INPUT, dataOut);
    }


    @Override
    public void terminate() {
//        try {
//            outputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}
