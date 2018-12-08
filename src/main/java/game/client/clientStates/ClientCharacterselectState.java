package game.client.clientStates;

import engine.PositionComp;
import engine.UserInput;
import engine.WorldContainer;
import engine.graphics.*;
import engine.graphics.text.Font;
import engine.graphics.text.TextMesh;
import engine.network.NetworkDataOutput;
import engine.network.NetworkPregamePackets;
import engine.network.client.ClientUtils;
import game.LoadImageData;
import game.client.Client;
import engine.network.client.ClientState;
import engine.network.client.ClientStates;
import game.CharacterUtils;
import utils.maths.Vec2;
import utils.maths.Vec4;

/**
 * Created by eirik on 04.07.2017.
 */
public class ClientCharacterselectState extends ClientState {


    private int[] characterIconEntities;
    int[] buttonEntities;
    private int gameLogoEntity;
    private int lockInButtonEntity;

    private int cursorEntity;
    private int commitEntity;

    private boolean characterCommited;
    private int characterSelected;

    private float buttonsTop = ClientUtils.buttonsTop - 60*2;

    private float buttonSpaceY = 50;
    private float iconSpaceX = 450+100;
    private float iconSpaceYExtra = 30;
    private float incrementIconSpaceX = 60;


    private float cursorSpaceX = -25;
    private float cursorSpaceY = 32;


    private String lockInButtonString = "Lock in";
    private float lockInExtraSpaceY = 20;
    private Vec4 lockInButtonColor = new Vec4(0.6f, 0.9f, 0.6f, 1f);
//    private float iconCenterX = Client.CLIENT_WIDTH/2;
//    private float iconCenterY = Client.CLIENT_HEIGHT/2;

    @Override
    public void init() {
        super.init();

        buttonEntities = new int[CharacterUtils.CHARACTER_COUNT];
        characterIconEntities = new int[CharacterUtils.CHARACTER_COUNT];
        createInitialEntities(wc);
    }

    @Override
    public void onEnter() {

        characterCommited = false;
        characterSelected = CharacterUtils.SHRANK;

        wc.deactivateEntity(commitEntity);

    }

    @Override
    public void onExit() {

    }


    @Override
    public void onUpdate() {

        handleExitCharselect();
        handleCharacterselect();
        handleCursorPosition();

        //if input obtained, goto game
        handleServerInput();

    }

    private void handleExitCharselect() {
        //if received an exit game packet, exit
        if (client.getTcpPacketIn().removeIfHasPacket(NetworkPregamePackets.GAME_SERVER_EXIT)) {
            setGotoState(ClientStates.IDLE);
        }
    }

    private void handleCharacterselect() {
        if (characterCommited) return;

        //decrease character selected id
        if (userInput.isKeyboardPressed(UserInput.KEY_UP)) {
            if (characterSelected > 0) {
                --characterSelected;
            }
        }
        //increase character selected id
        else if (userInput.isKeyboardPressed(UserInput.KEY_DOWN)) {
            if (characterSelected < CharacterUtils.CHARACTER_COUNT - 1) {
                ++characterSelected;
            }
        }
        else if (userInput.isKeyboardPressed(UserInput.KEY_ENTER)) {

            commitCharacterSelected();
        }

    }

    private void handleCursorPosition() {
        //set cursor position according to character

        //if we have commited dont need to do this
        if (characterCommited) {
            return;
        }

        //get selected character position
        PositionComp charPosComp = (PositionComp) wc.getComponent(buttonEntities[characterSelected], PositionComp.class);

        //get cursor position
        PositionComp cursPosComp = (PositionComp) wc.getComponent(cursorEntity, PositionComp.class);

        //set cursor position
        cursPosComp.setPos( charPosComp.getPos().add( new Vec2(cursorSpaceX, cursorSpaceY) ) );
    }

    private boolean handleServerInput() {

        if (client.getTcpPacketIn().removeIfHasPacket(NetworkPregamePackets.CHARSELECT_SERVER_GOTO_GAME)) {
            setGotoState(ClientStates.INGAME);
        }

        return true;
    }


    private void commitCharacterSelected() {
        if (characterCommited) return;

        characterCommited = true;
        //send data to server that character is selected
        sendCharacterSelected(characterSelected);
        //change cursor
        commitCursor();
    }
    private void commitCursor() {
        PositionComp cursorPosComp = (PositionComp) wc.getComponent(cursorEntity, PositionComp.class);

        wc.activateEntity(commitEntity);
        PositionComp commitPosComp = (PositionComp) wc.getComponent(commitEntity, PositionComp.class);
        commitPosComp.setPos(cursorPosComp.getPos());
    }


    private void sendCharacterSelected(int characterSelected) {
        NetworkDataOutput data = new NetworkDataOutput();
        data.writeInt(characterSelected);

        client.getTcpPacketOut().send(NetworkPregamePackets.CHARSELECT_CLIENT_CHOSE_CHARACTER, data);
    }


    private void createInitialEntities(WorldContainer wc) {

        //create character buttons and icons
        for (int i = 0; i < CharacterUtils.CHARACTER_COUNT; i++) {
            final int ii = i;

            float x = ClientUtils.buttonsLeft;
            float y = buttonsTop + i*(ClientUtils.buttonHeight + buttonSpaceY);

            buttonEntities[i] = ClientUtils.createButton(wc,
                    x, y,
                    ClientUtils.buttonWidth, ClientUtils.buttonHeight,

                    new TextMesh(CharacterUtils.CHARACTER_NAMES[i], Font.getDefaultFont(), ClientUtils.buttonTextSize, ClientUtils.buttonTextColor),
                    null,
                    (e, a) -> characterSelected = ii,
                    null, null
            );

            x += iconSpaceX + i*incrementIconSpaceX;
            y += iconSpaceYExtra*i;
            //create character icons
            int charIconEntity = wc.createEntity("character icon");
            wc.addComponent(charIconEntity, new PositionComp(x, y));

            LoadImageData imageData = CharacterUtils.loadCharData[i][1];
            CharacterUtils.addCharacterGraphicsComps(wc, charIconEntity, imageData);

            characterIconEntities[i] = charIconEntity;
        }

        //crate lockInButton
        float x = ClientUtils.buttonsLeft;
        float y = buttonsTop + lockInExtraSpaceY + CharacterUtils.CHARACTER_COUNT * (ClientUtils.buttonHeight + buttonSpaceY);

        lockInButtonEntity = ClientUtils.createButton(wc,
                x, y,
                ClientUtils.buttonWidth, ClientUtils.buttonHeight,

                new TextMesh(lockInButtonString, Font.getDefaultFont(), ClientUtils.buttonTextSize, lockInButtonColor),
                null,
                (e, a) -> commitCharacterSelected(),
                null, null
        );

        //create small sol logo
        gameLogoEntity = wc.createEntity("game logo");
        wc.addComponent(gameLogoEntity, new PositionComp(Client.CLIENT_WIDTH-320, Client.CLIENT_HEIGHT-180-20));
        wc.addComponent(gameLogoEntity, new TexturedMeshComp(
                TexturedMeshUtils.createRectangle("sol_logo.png", 320, 180)));


        this.cursorEntity  = wc.createEntity();
        wc.addComponent(cursorEntity, new PositionComp(0, 0) );
        float[] red = {1f,0f,0f};
        wc.addComponent(cursorEntity, new ColoredMeshComp(ColoredMeshUtils.createCircleSinglecolor(20, 16, red)));

        commitEntity = wc.createEntity();
        float[]green = {0f, 1f, 0f};
        wc.addComponent(commitEntity, new PositionComp(0, 0, 0.1f));
        wc.addComponent(commitEntity, new ColoredMeshComp(ColoredMeshUtils.createCircleSinglecolor(20, 16, green)));

    }
}
