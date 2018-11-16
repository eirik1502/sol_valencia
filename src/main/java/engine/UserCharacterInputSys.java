package engine;

import engine.network.networkPackets.CharacterInputData;

/**
 * Created by eirik on 15.06.2017.
 */
public class UserCharacterInputSys implements Sys {


    private WorldContainer wc;

    private UserInput userInput;


    public UserCharacterInputSys(UserInput userInput) {
        this.userInput = userInput;
    }

    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
    }

    @Override
    public void update() {

        for (int entity : wc.getEntitiesWithComponentType(UserCharacterInputComp.class)) {

            UserCharacterInputComp inputComp = (UserCharacterInputComp)wc.getComponent(entity, UserCharacterInputComp.class);

            CharacterInputData inData = new CharacterInputData();

            inData.setMoveLeft(userInput.isKeyboardPressed(UserInput.KEY_A));
            inData.setMoveRight(userInput.isKeyboardPressed(UserInput.KEY_D));
            inData.setMoveUp(userInput.isKeyboardPressed(UserInput.KEY_W));
            inData.setMoveDown(userInput.isKeyboardPressed(UserInput.KEY_S));

            inData.setAction1(userInput.isMousePressed(UserInput.MOUSE_BUTTON_1));
            inData.setAction2(userInput.isMousePressed(UserInput.MOUSE_BUTTON_2));
            inData.setAction3(userInput.isKeyboardPressed(UserInput.KEY_SPACE));

            inData.setAimX(userInput.getMouseX());
            inData.setAimY(userInput.getMouseY());

            inputComp.setClientData(inData);

        }
    }

    @Override
    public void terminate() {

    }
}
