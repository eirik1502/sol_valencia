package game.offline;


import engine.*;

import engine.graphics.*;
import engine.graphics.text.Font;
import engine.graphics.text.FontType;
import engine.graphics.view_.View;
import engine.physics.*;
import engine.utils.tickers.LinearTicker;
import engine.window.Window;
import game.GameUtils;
import game.loaders.entity_loader.ColoredMeshCompInstAdapter;
import game.loaders.entity_loader.EntityClassLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by eirik on 13.06.2017.
 */
public class Game {


    protected static final float FRAME_INTERVAL = 1.0f/60.0f;

    protected Window window;
    protected UserInput userInput;

    protected WorldContainer wc;

    protected LinearTicker ticker;

    protected String configPath = "configs/entityClasses.json";

    protected Map<String, EntityClass> entityClasses = new HashMap<>();
    protected List<EntityConstructor> initialEntities = new ArrayList<>();


    public Game() {
        this(new ArrayList<>(), new ArrayList<>());
    }

    public Game(List<EntityClass> entityClasses, List<EntityConstructor> initialEntities) {
        entityClasses.forEach(ec -> this.entityClasses.put(ec.name, ec));
        this.initialEntities.addAll(initialEntities);

        wc = new WorldContainer( new View(GameUtils.VIEW_WIDTH, GameUtils.VIEW_HEIGHT) );
    }

    public void init() {

        window = new Window(0.8f, 0.8f,"SIIII");
        userInput = new UserInput(window, GameUtils.VIEW_WIDTH, GameUtils.VIEW_HEIGHT);

        Font.loadFonts(FontType.BROADWAY);
        //AudioMaster.init();


        //List<EntityClass> entityClasses = EntityClassLoader.LoadEntityClasses(configPath);

        //assign cmponents
        entityClasses.values().stream()
                .flatMap(ec -> ec.getComponentClasses().stream())
                .forEach(compClass -> wc.assignComponentType(compClass));

        //assignComponents(wc);
        addSystems(wc);
        //createEntities(wc);

        //set entity class custom initializers
        EntityClass.addComponentLoader(ColoredMeshComp.class, new ColoredMeshCompInstAdapter());

        //createConfigEntities();

        initialEntities.forEach(eConstr -> {
            EntityClass ec = entityClasses.get(eConstr.getEntityClassName());
            ec.instanciate(wc, eConstr);
        });

        System.out.println("HEELLLLLOOOOO");
        System.out.println(wc);

        ticker = new LinearTicker(FRAME_INTERVAL);
        ticker.setListener(this::update);
    }

    protected void createConfigEntities() {


//        int ent = essences.get(0).instanciate(wc);
//        PositionComp posComp = wc.getComponent(ent, PositionComp.class);
//        posComp.setX(100);
    }

    private void assignComponents(WorldContainer wc) {
        GameUtils.assignComponentTypes(wc);
        wc.assignComponentType(MovementControlComp.class);
        wc.assignComponentType(MovementInputComp.class);
        wc.assignComponentType(PlayerControlBotComp.class);
        wc.assignComponentType(UserMovementInputComp.class);
    }
    private void addSystems(WorldContainer wc) {
        wc.addSystem(new PlayerControlBotSys());
        wc.addSystem(new UserMovementInputSys(userInput));
        wc.addSystem(new MovementControlSys());
        wc.addSystem(new CollisionDetectionSys());
        wc.addSystem(new NaturalResolutionSys());

        wc.addSystem(new RenderSys(window));

    }


    /**
     * blocking while the game runs
     */
    public void start() {

        wc.updateSystems();

        //blocking
        ticker.start();

        onTerminate();
    }

    public void stop() {
        ticker.stop();
    }

    protected void onTerminate() {

        wc.terminate();
        window.close();
    }


    protected void stopIfRequested() {
        if (window.shouldClosed() || userInput.isKeyboardPressed(UserInput.KEY_ESCAPE))
            stop();
    }
    protected void pollEvents() {
        window.pollEvents();
    }
    protected void updateEngine() {
        wc.updateSystems();

    }

    public void update(float deltaTime) {
        pollEvents();

        updateEngine();

        stopIfRequested();
    }




    private void createEntities(WorldContainer wc) {
        int p = wc.createEntity("player");
        wc.addComponent(p, new PositionComp(500, 500));
        wc.addComponent(p, new ColoredMeshComp(ColoredMeshUtils.createCircleTwocolor(32, 12)));
        wc.addComponent(p, new MovementControlComp());
        MovementInputComp moveInpComp = new MovementInputComp();
//        moveInpComp.xAxis = 0.5f;
//        moveInpComp.yAxis = 0.25f;
        wc.addComponent(p, moveInpComp);
        wc.addComponent(p, new PlayerControlBotComp());

        wc.addComponent(p, new PhysicsComp());
        wc.addComponent(p, new CollisionComp(new Circle(32)));
        wc.addComponent(p, new NaturalResolutionComp());


        int w = wc.createEntity("wall");
        wc.addComponent(w, new ColoredMeshComp(ColoredMeshUtils.createCircleTwocolor(32, 8)));
        wc.addComponent(w, new PositionComp(600,400));
        wc.addComponent(w, new PhysicsComp());
        wc.addComponent(w, new CollisionComp(new Circle(32)));
        wc.addComponent(w, new NaturalResolutionComp());
        wc.addComponent(w, new MovementControlComp());
        wc.addComponent(w, new MovementInputComp());
        //wc.addComponent(w, new PlayerControlBotComp());
        wc.addComponent(w, new UserMovementInputComp());


        //wc.addComponent(p, new MovementInputComp());

    }


}
