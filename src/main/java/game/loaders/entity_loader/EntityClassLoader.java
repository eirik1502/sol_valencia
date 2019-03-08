package game.loaders.entity_loader;


import com.google.gson.*;
import engine.Component;
import engine.PositionComp;
import game.offline.EntityClass;
import utils.FileUtils;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * Created by eirik on 23.11.2018.
 */
public class EntityClassLoader {

    private static Gson defaultGson = new Gson();

    /**
     * Loades essences from a given config.
     * Uses assigned custom component loaders to map config values to component variables,
     * or if no custom loader is goven for a component,
     * assumes component values in config exactly matches components variables.
     *
     * @param configFilename
     * @return
     */
    public static List<EntityClass> LoadGameClasses(String configFilename) {
        String configStr = FileUtils.loadAsString(configFilename);
        //System.out.println(configStr);

        Gson customGson = createCustomGsonLoader();

        EntityClassEntry[] entityClassEntries;
        try {
            entityClassEntries = customGson.fromJson(configStr, EntityClassEntry[].class);

            //if there is a json syntax error
        } catch (JsonSyntaxException e) {
            System.err.println("The json syntax was invalid in the file: " + configFilename
                +"\n\t"+e.getLocalizedMessage());
            return new ArrayList<>();
        }

        //check if there is json data in the file, if not, return an emty array
        if (entityClassEntries == null) {
            System.err.println("There was no json data in the resource file: " + configFilename);
            return new ArrayList<>();
        }

        return Arrays.stream(entityClassEntries)
                .map(entityClassEntry -> new EntityClass(
                        entityClassEntry.name,
                        //filter invalid components
                        entityClassEntry.components.stream().filter(ComponentEntry::isValid).collect(Collectors.toList())
                        )
                )
                .collect(Collectors.toList());
    }

    /**
     * Custom gson loader that handles constructor values, and custom componnt loaders.
     */
    private static Gson createCustomGsonLoader() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        JsonDeserializer<ComponentEntry> deserializer = (JsonElement json, Type typeOfT, JsonDeserializationContext context) -> {
            //component class
            Class<? extends Component> compClass;
            //component initial arguments as json object
            JsonObject compArgs = null;


            //Given json must be JsonObject if syntax is correct
            JsonObject jsonObj = json.getAsJsonObject();
            //type must be specified for orrect syntax
            String compType = jsonObj.get("type").getAsString();

            Class<?> unknownCompClass;
            try {
                unknownCompClass = Class.forName(compType);

            } catch (ClassNotFoundException e) {
                System.err.println("Parser encountered a component that is of a nonexisting type: " + compType);
                return new ComponentEntry(false);
            }

            try {
                compClass = unknownCompClass.asSubclass(Component.class);
            } catch (ClassCastException e) {
                System.err.println("Parser encountered a component of a class that is not a Component: " + compType);
                return new ComponentEntry(false);
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
