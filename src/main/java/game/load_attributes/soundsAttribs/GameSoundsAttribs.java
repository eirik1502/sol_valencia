package game.load_attributes.soundsAttribs;

import game.load_attributes.ImageAttribs;
import game.load_attributes.ImagesAttribs;
import game.load_attributes.SoundAttribs;
import game.load_attributes.SoundsAttribs;

/**
 * Created by eirik on 06.12.2018.
 */
public class GameSoundsAttribs extends SoundsAttribs{

    public GameSoundsAttribs() {
        //characters
        put("charMagnetSpearPokeStart", "audio/click4.ogg", 1);
        put("charMagnetSpearPokeHit", "audio/arrow_impact.ogg", 1);
        put("charMagnetSpearThrowStart", "audio/masai_arrow_throw.ogg", 1);
        put("charMagnetSpearThrowHit", "audio/hook_hit.ogg", 1);
        put("charMagnetLionDanceStart", "audio/lion-roar.ogg", 1);
    }


    private void put(String soundName, String filename_, float volume_) {
        SoundAttribs attribs = new SoundAttribs() {
            {
                this.filename = filename_;
                this.volume = volume_;
            }
        };
        soundsAttribs.put(soundName, attribs);
    }
}
