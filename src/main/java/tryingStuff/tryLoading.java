package tryingStuff;

import com.google.gson.*;
import engine.Component;
import engine.PositionComp;
import game.loaders.entity_loader.EntityClassEntry;
import utils.FileUtils;

import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * Created by eirik on 23.11.2018.
 */
public class tryLoading {

    public static void main(String[] args) {
//        int[] ints = {3, 5, 2, 2, 5};
//        String jsonInts = "[1, 2, 3, 6, 6, 6, 1]";
//
//        Gson gson = new Gson();
//        System.out.println(Arrays.toString(gson.fromJson(jsonInts, int[].class)));

        String configStr = FileUtils.loadAsString("configs/entityClasses.json");
        //System.out.println(configStr);

        //build our own gson
        GsonBuilder gsonBuilder = new GsonBuilder();

        JsonDeserializer<Component> deserializer = (JsonElement json, Type typeOfT, JsonDeserializationContext context) -> {
            JsonObject jsonObj = json.getAsJsonObject();
            String type = jsonObj.get("type").getAsString();

            Class<?> compClass = null;
            Component comp = null;
            try {
                compClass = Class.forName(type);

                comp = (Component)compClass.newInstance();

            } catch (ClassNotFoundException e) {
                System.err.println("Parsing found "+type+" that could not be interpreted.\n"+e.getCause());
                return null;
            }catch (IllegalAccessException e) {
                System.err.println("Parsing found "+type+". This component does not have a no-arg constructure.\n"+e.getCause());
                return null;
            } catch (InstantiationException e) {
                System.err.println("Parsing found "+type+". This is not a component\n"+e.getCause());
                return null;
            }

            JsonObject compArgs = jsonObj.get("values").getAsJsonObject();
            //Gson gson = new Gson();
            //gson.fromJson(jsonObj, Component.class);

            if (comp instanceof PositionComp) {
                PositionComp posComp = (PositionComp)comp;
                float x = compArgs.get("x").getAsFloat();
                float y = compArgs.get("y").getAsFloat();
                posComp.setX(x);
                posComp.setY(y);
            }

            return comp;
        };
        gsonBuilder.registerTypeAdapter(Component.class, deserializer);

        Gson customGson = gsonBuilder.create();

        EntityClassEntry[] configObjs = customGson.fromJson(configStr, EntityClassEntry[].class);
        System.out.println(Arrays.toString(configObjs));
    }

}
