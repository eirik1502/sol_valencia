package engine.audio;

import engine.PositionComp;
import engine.Sys;
import engine.WorldContainer;
import javafx.geometry.Pos;

import java.util.List;

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
            AudioComp ac = wc.getComponent(entity, AudioComp.class);
            PositionComp posComp = wc.getComponent(entity, PositionComp.class);

            if (!ac.backgroundAudio){
                ac.setPosition(posComp.getPos3());
            }

            if (ac.getRequestSoundIndex() != -1){
                playSource(ac);
            }

            if (ac.requestStopSource){
                ac.stopSound();
            }

            //resetting so sound does not play repeatedly
            ac.setRequestSoundIndex(-1);
        });

        wc.entitiesOfComponentTypeStream(SoundListenerComp.class).forEach(entity-> {
            PositionComp posComp = (PositionComp) wc.getComponent(entity, PositionComp.class);
            alListener3f(AL_POSITION, posComp.getX(), posComp.getY(), posComp.getZ());
        });
    }


    private void playSource(AudioComp audioComp){
        int reqSoundIndex = audioComp.getRequestSoundIndex();
        List<Sound> sounds = audioComp.soundList;

        //check if requested sound is valid
        if (reqSoundIndex < 0 || reqSoundIndex >= sounds.size()) {
            System.err.println("Trying to play a sound by invalid index");
            return;
        }

        Sound sound = sounds.get(reqSoundIndex);
        int sourcePointer = audioComp.sourcePointer;

        alSourceStop(sourcePointer);
        alSourcei(sourcePointer, AL_BUFFER, sound.getBufferPointer());
        alSourcePlay(sourcePointer);
    }

    @Override
    public void terminate() {
        AudioMaster.terminate();
    }
}
