package game.loaders.initial_entities_loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonObject;
import engine.Component;
import game.offline.ComponentConstructor;

import java.lang.reflect.Type;

public class GsonComponentConstructor implements ComponentConstructor<Component> {

    private JsonObject args;


    public GsonComponentConstructor(JsonObject args) {
        this.args = args;


    }

    @Override
    public void modifyInitially(Component comp) {
        populate(this.args, comp);
    }

    private void populate(JsonObject json, Component comp) {
        //maps the Component read into the given component
        Class<? extends Component> compClass = comp.getClass();
        new GsonBuilder().registerTypeAdapter(compClass, new InstanceCreator<Component>() {
            @Override public Component createInstance(Type t) { return comp; }
        }).create().fromJson(json, compClass);
    }
}
