package game.loaders;

import com.google.gson.JsonObject;
import engine.Component;

/**
 * If the component fields doesn't match the expected constructor values directely,
 * implement this to make a loader for it.
 *
 * Created by eirik on 23.11.2018.
 */
public interface ComponentInstanciationAdapter {
    Component load(JsonObject compArgs);
}
