package game.loaders.initial_entities_loader;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import engine.Component;
import game.loaders.entity_loader.ComponentEntry;
import game.loaders.entity_loader.EntityClassEntry;
import game.loaders.entity_loader.EntityClassLoader;
import game.offline.ComponentConstructor;
import game.offline.EntityClass;
import game.offline.EntityConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Gets the initial entities JsonElemnts, yields the init entities names and verifies that they are existing
 */
public class InitialEntitiesLoader {

    public static List<EntityConstructor> Load(JsonElement initialEntitiesElem) {

        List<EntityClass> entityClassEntries = EntityClassLoader.LoadEntityClasses(initialEntitiesElem);

        return entityClassEntries.stream()
                .map(InitialEntitiesLoader::entityClassEntryToConstructor)
                .collect(Collectors.toList());
    }

    private static EntityConstructor entityClassEntryToConstructor(EntityClass ecEntry) {
        EntityConstructor entityConstr = new EntityConstructor(ecEntry.name);
        ecEntry.componentEntries.forEach(ce ->
                entityConstr.addComponentConstructor(ce.compType, new GsonComponentConstructor(ce.args))
        );
        return entityConstr;
    }
}
