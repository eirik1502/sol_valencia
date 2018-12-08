package game.loaders;


import com.google.gson.*;
import engine.Component;
import engine.PositionComp;
import game.offline.EntityEssence;
import utils.FileUtils;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * TODO: should not be able to load components if they have no no-arg constructor, and no custom loader
 *
 * Created by eirik on 23.11.2018.
 */
public class Loader {

    private Gson defaultGson = new Gson();
    private Map<Class<? extends Component>, CustomComponentLoader> customCompLoaders = new HashMap<>();


    public void addComponentLoader(Class<? extends Component> compType, CustomComponentLoader customLoader) {
        this.customCompLoaders.put(compType, customLoader);
    }

    public List<EntityEssence> loadEssenceFromConfig(String configFilename) {
        String configStr = FileUtils.loadAsString(configFilename);
        //System.out.println(configStr);

        Gson customGson = createCustomGsonLoader();

        EntityClassEntry[] configObjs = customGson.fromJson(configStr, EntityClassEntry[].class);

        //handle extension

        //System.out.println(Arrays.toString(configObjs));

        List<EntityEssence> essences =
                Arrays.stream(configObjs).map(co ->  new EntityEssence(co.name, co.components))
                .collect(Collectors.toList());
        return essences;
    }


    private Gson createCustomGsonLoader() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        JsonDeserializer<Component> deserializer = (JsonElement json, Type typeOfT, JsonDeserializationContext context) -> {
            JsonObject jsonObj = json.getAsJsonObject();
            String type = jsonObj.get("type").getAsString();

            Class<?> compClass = null;
            try {
                compClass = Class.forName(type);

            } catch (ClassNotFoundException e) {
                System.err.println("Parsing found "+type+" that could not be interpreted.\n"+e.getCause());
                return null;
            }

            JsonElement compArgEl = jsonObj.get("values");
            Component comp = null;

            if (compArgEl == null) {
                //no constructor
                try {
                    comp = (Component)compClass.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            else {
                JsonObject compArgs = compArgEl.getAsJsonObject();

                //By default, use reflection to set varables in component created
                //based on config constructor.
                //Override this behavior by assigning a loader for a given component.

                if (customCompLoaders.containsKey(compClass)) {
                    comp = customCompLoaders.get(compClass).load(compArgs);
                }
                else {
                    comp = (Component) defaultGson.fromJson(compArgs, compClass);
                }
            }

            return comp;
        };
        gsonBuilder.registerTypeAdapter(Component.class, deserializer);

        return gsonBuilder.create();}
}
