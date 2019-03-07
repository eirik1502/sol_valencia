package game.offline;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import engine.Component;
import engine.WorldContainer;
import game.loaders.ComponentEntry;
import game.loaders.CustomComponentLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by eirik on 23.11.2018.
 */
public class EntityClass {

    private static Gson defaultGson = new Gson();
    private static Map<Class<? extends Component>, CustomComponentLoader> customCompLoaders = new HashMap<>();
    public static void addComponentLoader(Class<? extends Component> compType, CustomComponentLoader customLoader) {
        EntityClass.customCompLoaders.put(compType, customLoader);
    }

    private final String name;
    private final List<ComponentEntry> componentEntries = new ArrayList<>();
    //public final List<Component> components = new ArrayList<>();

    public EntityClass(String name) {
        this.name = name;
    }
    public EntityClass(String name, List<ComponentEntry> componentEntries) {
        this.name = name;
        this.componentEntries.addAll(componentEntries);
    }

    public int instanciate(WorldContainer wc) {
        int e = wc.createEntity(name);
        componentEntries.forEach(compEntry -> wc.addComponent(e, componentOf(compEntry)));
        return e;
    }

    /**
     * By default, use reflection to set varables in component created
     * based on config constructor.
     * Override this behavior by assigning a loader for a given component.
     */
    private Component componentOf(ComponentEntry compEntry) {
        Component comp = null;

        JsonObject args = compEntry.args;
        Class<? extends Component> compClass = compEntry.compType;

        //if args is null, there are no initial values
        if (args == null) {
            //no values given, default constructor
            try {
                comp = compClass.newInstance();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        else {
            //instanciate with a constructor for the given values

            if (customCompLoaders.containsKey(compClass)) {
                comp = customCompLoaders.get(compClass).load(args);
            }
            else {
                comp = defaultGson.fromJson(args, compClass);
            }
        }

        return comp;
    }
}
