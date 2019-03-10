package game.loaders.game_instance_loader;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import engine.PositionComp;
import game.loaders.entity_loader.EntityClassEntry;
import game.loaders.entity_loader.EntityClassLoader;
import game.loaders.initial_entities_loader.InitialEntitiesLoader;
import game.offline.EntityClass;
import game.offline.EntityConstructor;
import game.offline.Game;
import utils.FileUtils;

import java.util.*;
import java.util.stream.Collectors;

public class GameInstanceLoader {

    private Gson defaultGson = new Gson();

    private List<EntityClass> entityClasses;
    private List<EntityConstructor> initialEntities;


    public Game load(String configFilename) {
        String instDataJson = FileUtils.loadAsString(configFilename);

        GameInstanceConfig instConf;

        try {
            instConf = defaultGson.fromJson(instDataJson, GameInstanceConfig.class);

            //if there is a json syntax error
        } catch (JsonSyntaxException e) {
            System.err.println("The json syntax was invalid in the file: " + configFilename
                    +"\n\t"+e.getLocalizedMessage());
            return null;
        }

        //check if there is json data in the file, if not, return an emty array
        if (instConf == null) {
            System.err.println("There was no json data in the resource file: " + configFilename);
            return null;
        }

        entityClasses = EntityClassLoader.LoadEntityClasses(instConf.entityClasses);

        initialEntities = InitialEntitiesLoader.Load(instConf.initialEntities);

        System.out.println(initialEntities);

        Set<String> loadedEntityClassNames = entityClasses.stream().map(ec -> ec.name).collect(Collectors.toSet());

        //remove invalid initialEntities
        initialEntities = initialEntities.stream()
                .filter(eConstr -> {
                    boolean isValid = loadedEntityClassNames.contains(eConstr.getEntityClassName());
                    if (!isValid)
                        System.err.println("Loader encountered an initial entity that is not of a EntityClass");

                    return isValid;
                })
                .collect(Collectors.toList());

        return new Game(entityClasses, initialEntities);
    }

}
