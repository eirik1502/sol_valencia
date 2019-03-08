package game.loaders;

import com.google.gson.JsonObject;
import engine.Component;
import engine.graphics.ColoredMeshComp;
import engine.graphics.ColoredMeshUtils;

/**
 * Created by eirik on 23.11.2018.
 */
public class ColoredMeshCompInstAdapter implements ComponentInstanciationAdapter {

    @Override
    public Component load(JsonObject compArgs) {
        float radius = compArgs.get("radius").getAsFloat();
        int sides = compArgs.get("radius").getAsInt();

        ColoredMeshComp colMeshComp = new ColoredMeshComp(ColoredMeshUtils.createCircleTwocolor(radius, sides));

        return colMeshComp;
    }
}
