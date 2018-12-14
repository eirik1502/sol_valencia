package game.loaders.solGameInstance;

import java.util.List;

/**
 * The root of the game instance configs
 *
 * Created by eirik on 13.12.2018.
 */
public class SolGameInstanceConfig {

    public String instance_name;
    public List<ClientConfig> clients;
    public String map;
    public List<List<String>> teams;
}
