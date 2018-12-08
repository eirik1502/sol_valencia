package game;

import engine.WorldContainer;
import engine.audio.Sound;

import java.util.List;

/**
 * Created by eirik on 08.12.2018.
 */
public interface SpecificCharacterUtils {
    void addAbilityComps(WorldContainer wc, int entity, boolean media);
    List<Sound> createCharacterSounds();
}
