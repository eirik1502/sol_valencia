package game.loaders.game_instance_loader;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import engine.PositionComp;
import engine.Sys;
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
    private List<Class<? extends Sys>> componentSystems;


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
        componentSystems = instConf.componentSystems.stream()
                .map(sName -> {
                    Class<? extends Sys> sysClass;
                    try {
                        Class<?> sysClassGeneral = Class.forName(sName);
                        sysClass = sysClassGeneral.asSubclass(Sys.class);
                    } catch (ClassNotFoundException e) {
                        System.err.println("Loader ecnoutered system that could not be located: " + sName);
                        return null;
                    }
                    return sysClass;
                })
                .filter(Objects::nonNull) //remove invalid systems
                .collect(Collectors.toList());


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

        return new Game(componentSystems, entityClasses, initialEntities);
    }

}
