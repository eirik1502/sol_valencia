package game.loaders.game_instance_loader;

import com.google.gson.JsonElement;

import java.util.List;
import java.util.Objects;

public class GameInstanceConfig {
    public List<String> modules;
    public Object resources;
    public JsonElement initialEntities;
    public List<String> componentSystems;
    public JsonElement entityClasses;
}
