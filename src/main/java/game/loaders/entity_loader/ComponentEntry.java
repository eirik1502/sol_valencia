package game.loaders.entity_loader;

import com.google.gson.JsonObject;
import engine.Component;

public class ComponentEntry {

    public boolean isValid;
    public Class<? extends Component> compType;
    public JsonObject args;

    public ComponentEntry() {
        this(true);
    }
    public ComponentEntry(boolean isValid) {
        this.isValid = isValid;
    }

    public boolean isValid() {
        return isValid;
    }
}
