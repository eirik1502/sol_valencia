package engine.audio;

import engine.PositionComp;
import engine.Sys;
import engine.WorldContainer;
import javafx.geometry.Pos;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.alcCloseDevice;
import static org.lwjgl.openal.ALC10.alcDestroyContext;

/**
 * Created by haraldvinje on 27-Jul-17.
 */
public class AudioSys implements Sys {


    private WorldContainer wc;

    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.wc = wc;
    }

    @Override
    public void update() {
        wc.entitiesOfComponentTypeStream(AudioComp.class).forEach(entity-> {
            AudioComp ac = (AudioComp) wc.getComponent(entity, AudioComp.class);
            PositionComp posComp = (PositionComp) wc.getComponent(entity, PositionComp.class);

            if (!ac.backgroundAudio){
                ac.setPosition(posComp.getPos3());
            }

            if (ac.requestSound!=-1){
                ac.playSound(ac.requestSound);
            }

            if (ac.requestStopSource){
                ac.stopSound();
            }

            //resetting so sound does not play repeatedly
            ac.requestSound = -1;
        });

        wc.entitiesOfComponentTypeStream(SoundListenerComp.class).forEach(entity-> {
            PositionComp posComp = (PositionComp) wc.getComponent(entity, PositionComp.class);
            alListener3f(AL_POSITION, posComp.getX(), posComp.getY(), posComp.getZ());
        });
    }


    @Override
    public void terminate() {
        AudioMaster.terminate();
    }
}
