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
    private static JsonParser jsonParser = new JsonParser();


    private static List<EntityClassEntry> LoadEntityClassEntries(JsonElement entityClassesElem) {
        Gson customGson = createCustomGsonLoader();

        EntityClassEntry[] entityClassEntries;
        entityClassEntries = customGson.fromJson(entityClassesElem, EntityClassEntry[].class);
        return new ArrayList<>(Arrays.asList(entityClassEntries));
    }

    /**
     * Extracts EntityClasses from the json entity classes json file, given the filename,
     * if the file containes valid json syntax
     *
     * @return The list of EntityClass'es loaded or an empty array if something went wrong.
     */
    public static List<EntityClass> LoadEntityClasses(String configFilename) {
        JsonElement entityClassesElem;

        String entityClassJson = FileUtils.loadAsString(configFilename);

        try {
            entityClassesElem = jsonParser.parse(entityClassJson);

            //if there is a json syntax error
        } catch (JsonSyntaxException e) {
            System.err.println("The json syntax was invalid in the file: " + configFilename
                    +"\n\t"+e.getLocalizedMessage());

            return new ArrayList<>();
        }

        //check if there is json data in the file, if not, return an emty array
        if (entityClassesElem == null) {
            System.err.println("There was no json data in the resource file: " + configFilename);
            return new ArrayList<>();
        }

        return LoadEntityClasses(entityClassesElem);
    }

    /**
     * Extracts EntityCLasses from the given JsonElement
     */
    public static List<EntityClass> LoadEntityClasses(JsonElement entityClassesElem) {

        List<EntityClassEntry> entityClassEntries = LoadEntityClassEntries(entityClassesElem);

        return entityClassEntries.stream()
                .map(entityClassEntry -> new EntityClass(
                        entityClassEntry.name,
                        //filter invalid components
                        entityClassEntry.components.stream()
                                .filter(ComponentEntry::isValid)
                                .collect(Collectors.toList())
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
