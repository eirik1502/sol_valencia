package game.server;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by haraldvinje on 06-Jul-17.
 */
public class ServerCharacterSelection {

    private Map<ServerClientHandler, Integer> characterIds = new HashMap<>();


    //number of players will be generalized later
    private int numberOfPlayers = 2;

    private boolean ready = false;



    public Map<ServerClientHandler, Integer> getCharacterIds(){
        return characterIds;
    }

    public boolean isReady() {
        this.ready = characterIds.size()==numberOfPlayers;
        return ready;
    }

    public void addCharacter(ServerClientHandler client, int characterId){
        characterIds.put(client, characterId);
    }
}
