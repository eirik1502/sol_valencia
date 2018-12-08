package game.loaders;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import engine.Component;
import engine.graphics.ColoredMeshComp;
import engine.graphics.ColoredMeshUtils;

/**
 * Created by eirik on 23.11.2018.
 */
public class ColoredMeshCompLoader implements CustomComponentLoader{
    private Gson defaultGson = new Gson();

    @Override
    public Component load(JsonObject compArgs) {
        //ColoredMeshComp colMeshComp = defaultGson.fromJson(compArgs, ColoredMeshComp.class);
        //JsonElement radiusEl =
        float radius = compArgs.get("radius").getAsFloat();
        int sides = compArgs.get("radius").getAsInt();

        ColoredMeshComp colMeshComp = new ColoredMeshComp(ColoredMeshUtils.createCircleTwocolor(radius, sides));

        return colMeshComp;
    }
}
