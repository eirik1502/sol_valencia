package game.loaders;


import com.google.gson.*;
import engine.Component;
import game.offline.EntityClass;
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

    /**
     * Loades essences from a given config.
     * Uses assigned custom component loaders to map config values to component variables,
     * or if no custom loader is goven for a component,
     * assumes component values in config exactly matches components variables.
     *
     * @param configFilename
     * @return
     */
    public List<EntityClass> loadEssenceFromConfig(String configFilename) {
        String configStr = FileUtils.loadAsString(configFilename);
        //System.out.println(configStr);

        Gson customGson = createCustomGsonLoader();

        EntityClassEntry[] entityClassEntries = customGson.fromJson(configStr, EntityClassEntry[].class);

        //handle extension

        //System.out.println(Arrays.toString(configObjs));

        List<EntityClass> essences =
                Arrays.stream(entityClassEntries).map(entityClass ->
                        new EntityClass(entityClass.name,entityClass.components)
                )
                .collect(Collectors.toList());
        return essences;
    }

    /**
     * Custom gson loader that handles constructor values, and custom componnt loaders.
     */
    private Gson createCustomGsonLoader() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        JsonDeserializer<ComponentEntry> deserializer = (JsonElement json, Type typeOfT, JsonDeserializationContext context) -> {
            //component class
            Class<? extends Component> compClass = null;
            //component initial arguments as json object
            JsonObject compArgs = null;


            //Given json must be JsonObject if syntax is correct
            JsonObject jsonObj = json.getAsJsonObject();
            //type must be specified for orrect syntax
            String compType = jsonObj.get("type").getAsString();

            try {
                compClass = (Class<? extends Component>)Class.forName(compType);

            } catch (ClassNotFoundException e) {
                System.err.println("Parsing found "+compType+" that could not be interpreted.\n"+e.getCause());
                return null;
            }

            JsonElement compArgEl = jsonObj.get("values");
            if (compArgEl != null) {
                compArgs = compArgEl.getAsJsonObject();
            }

            ComponentEntry compEntry = new ComponentEntry();
            compEntry.compType = compClass;
            compEntry.args = compArgs;

            return compEntry;
        };
        gsonBuilder.registerTypeAdapter(ComponentEntry.class, deserializer);

        return gsonBuilder.create();
    }
}
