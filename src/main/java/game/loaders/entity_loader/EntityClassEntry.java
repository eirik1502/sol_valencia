package game.loaders.entity_loader;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eirik on 23.11.2018.
 */
public class EntityClassEntry {
    String name;
    String extend;
    List<ComponentEntry> components = new ArrayList<>();


    public String toString() {
        Gson gson = new Gson();
        String out = "\n--------\n"
                +"name: "+name+"\n"
                +"extend: "+extend+"\n";
        if (components == null) {
            return out + "no components";
        }
        out += "components:\n";
        for (ComponentEntry c : components) {
            out += "\t"+c.compType.getClass().getName() +":"+ c.compType.toString()+",\n";
        }
        return out;
    }
}
