package game.loaders;

import com.google.gson.JsonObject;
import engine.Component;

public class ComponentEntry {

    public Class<? extends Component> compType;
    public JsonObject args;
}