package engine;

import engine.audio.AudioComp;
import engine.character.CharacterComp;
import engine.combat.DamageableComp;
import engine.graphics.ColoredMeshComp;
import engine.graphics.ColoredMeshUtils;
import engine.graphics.ViewRenderComp;
import engine.graphics.text.Font;
import engine.graphics.text.FontType;
import engine.graphics.text.TextMesh;
import engine.graphics.text.TextMeshComp;
import engine.network.CharacterStateData;
import utils.maths.Vec4;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by eirik on 09.07.2017.
 */
public class OnScreenSys implements Sys{


    private WorldContainer wc;


//    private int botBarEnt;
//    private List<Integer> damageTexts = new ArrayList<>();
//    private int gameEndTextEntity;

    private String wictoryString = "Victory :)";
    private String defeatString = "Defeat :(";
    private Vec4 wictoryColor = new Vec4(0, 1, 0, 1);
    private Vec4 defeatColor = new Vec4(1, 0, 0, 1);


    public OnScreenSys(WorldContainer wc, int characterCount) {

//        //create bottom bar
//        //botBarEnt = createBotBarEnt(wc);
//
//        //create character damage displays
//        int halfCharCount = characterCount /2;
//        Vec4 textColor = new Vec4(0.9f, 0, 0, 1);
//        for (int i = 0; i < characterCount; i++) {
//
//            float x = i < halfCharCount ? 100 : wc.getView().getWidth() - 200;
//            float y = wc.getView().getHeight()-200 + 64f * (i % halfCharCount);
//            int textEnt = createTextEntity(wc, "0", 64, x, y, textColor);
//            damageTexts.add( textEnt );
//        }
//
//        //create game end text
//        gameEndTextEntity = createTextEntity(wc, "", 128, 300, 300, new Vec4() );
    }

    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
    }

    @Override
    public void update() {

        int charNumb = 0;
        for (int entity : wc.getEntitiesWithComponentType(GameDataComp.class)) {
            GameDataComp dataComp = (GameDataComp) wc.getComponent(entity, GameDataComp.class);

            //render damage taken text
            for (Map.Entry<Integer, Integer> entry : dataComp.charDamageTextEntities.entrySet()) {
                int charEnt = entry.getKey();
                int textEnt = entry.getValue();
                DamageableComp charDmgableComp = (DamageableComp)wc.getComponent(charEnt, DamageableComp.class);
                ViewRenderComp textViewrendComp = (ViewRenderComp) wc.getComponent(textEnt, ViewRenderComp.class);

                textViewrendComp.getTextMesh(0).setString( String.format("%.0f", charDmgableComp.getDamage() ) );
            }

            //render game end
            if (dataComp.endGameRequest) {

                AudioComp backgroundAudio = (AudioComp) wc.getComponent(dataComp.backgroundMusicEntity, AudioComp.class);
                backgroundAudio.requestStopSource = true;

                if (dataComp.gameWon && !wc.hasComponent(dataComp.gameEndVictoryEntity, AudioComp.class)) {

                    wc.activateEntity( dataComp.gameEndVictoryEntity );
                    AudioComp audioComp = (AudioComp) wc.getComponent (dataComp.gameEndVictoryEntity, AudioComp.class);

                    audioComp.requestSound = 0;
                }
                else if (!dataComp.gameWon && !wc.hasComponent(dataComp.gameEndDefeatEntity, AudioComp.class)) {

                    wc.activateEntity( dataComp.gameEndDefeatEntity );
                    AudioComp audioComp = (AudioComp) wc.getComponent (dataComp.gameEndDefeatEntity, AudioComp.class);
                    audioComp.requestSound = 0;


//                    audioComp.requestSound = 0;
                }
            }

//            setTextString(damageTexts.get(charNumb), Integer.toString((int)dmgableComp.getDamage()));

            charNumb++;
        }
    }

    @Override
    public void terminate() {

    }

    private int createTextEntity(WorldContainer wc,  String s, float size, float x, float y, Vec4 color) {
        int t = wc.createEntity();
        wc.addComponent(t, new PositionComp(x, y));
        wc.addComponent(t, new ViewRenderComp( new TextMesh( s, Font.getFont(FontType.BROADWAY),size, color) ) );

        return t;
    }


    private void setTextString(int entity, String s) {
        ViewRenderComp viewrendComp = (ViewRenderComp) wc.getComponent(entity, ViewRenderComp.class);
        viewrendComp.getTextMesh(0).setString(s);
    }

    private int createBotBarEnt(WorldContainer wc) {
        float viewW = wc.getView().getWidth();
        float viewH = wc.getView().getHeight();

        float barHeight = 128;

        int b = wc.createEntity();
        wc.addComponent(b, new PositionComp(0, viewH-barHeight));
        wc.addComponent(b, new ViewRenderComp(ColoredMeshUtils.createRectangle(viewW, barHeight)) );

        return b;
    }
}
