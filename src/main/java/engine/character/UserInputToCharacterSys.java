package engine.character;

import engine.Sys;
import engine.UserCharacterInputComp;
import engine.WorldContainer;
import engine.network.networkPackets.CharacterInputData;

/**
 * Created by eirik on 05.07.2017.
 */
public class UserInputToCharacterSys implements Sys{

    private WorldContainer wc;

    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
    }

    @Override
    public void update() {

        wc.entitiesOfComponentTypeStream(UserCharacterInputComp.class).forEach(charInpEntitiy -> {
            CharacterInputComp charInComp = (CharacterInputComp) wc.getComponent(charInpEntitiy, CharacterInputComp.class);
            UserCharacterInputComp userInComp = (UserCharacterInputComp) wc.getComponent(charInpEntitiy, UserCharacterInputComp.class);

            CharacterInputData userInData = userInComp.getClientData();

            charInComp.setMoveLeft( userInData.isMoveLeft() );
            charInComp.setMoveRight( userInData.isMoveRight() );
            charInComp.setMoveUp( userInData.isMoveUp() );
            charInComp.setMoveDown( userInData.isMoveDown() );

            charInComp.setAction1( userInData.isAction1() );
            charInComp.setAction2( userInData.isAction2() );
            charInComp.setAction3( userInData.isAction3() );

            charInComp.setAimX( userInData.getAimX() );
            charInComp.setAimY( userInData.getAimY() );
        });
    }

    @Override
    public void terminate() {

    }
}
