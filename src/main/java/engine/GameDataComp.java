package engine;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by eirik on 31.07.2017.
 */
public class GameDataComp implements Component {

    //map charEntity to damage text entity
    public Map<Integer, Integer> charDamageTextEntities = new HashMap<>();


    //entity for ingame background audio
    public int backgroundMusicEntity;


    //gameEndText entity
    public int gameEndVictoryEntity, gameEndDefeatEntity;

    //end game request
    public boolean endGameRequest = false;
    public boolean gameWon;


}
