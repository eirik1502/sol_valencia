package game.loaders;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonToken;
import engine.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by eirik on 23.11.2018.
 */
public class EntityClassEntry {
    String name;
    String extend;
    List<Component> components = new ArrayList<>();


    public String toString() {
        Gson gson = new Gson();
        String out = "\n--------\n"
                +"name: "+name+"\n"
                +"extend: "+extend+"\n";
        if (components == null) {
            return out + "no components";
        }
        out += "components:\n";
        for (Component c : components) {
            out += "\t"+c.getClass().getName() +":"+ gson.toJson(c)+",\n";
        }
        return out;
    }
}
