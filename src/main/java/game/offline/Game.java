package game.offline;


import engine.*;

import engine.graphics.*;
import engine.graphics.text.Font;
import engine.graphics.text.FontType;
import engine.graphics.view_.View;
import engine.graphics_module.GraphicsModule;
import engine.graphics_module.GraphicsModuleConfig;
import engine.physics.*;
import engine.utils.tickers.LinearTicker;
import engine.window.Window;
import game.GameUtils;
import game.loaders.entity_loader.ColoredMeshCompInstAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by eirik on 13.06.2017.
 */
public class Game {


    protected static final float FRAME_INTERVAL = 1.0f/60.0f;

    protected Window window;
    protected UserInput userInput;
    protected LinearTicker ticker;

    protected WorldContainer wc;

    protected List<EngineModuleConfig> moduleConfigs = new ArrayList<>();
    protected Map<Class<? extends EngineModule>, EngineModule> modulesByName = new HashMap<>();

    protected List<Class<? extends Sys>> systems = new ArrayList<>();
    protected Map<String, EntityClass> entityClasses = new HashMap<>();
    protected List<EntityConstructor> initialEntities = new ArrayList<>();


    public Game() {
        this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public Game(List<Class<? extends Sys>> componentSystems, List<EntityClass> entityClasses, List<EntityConstructor> initialEntities) {
        systems.addAll(componentSystems);
        entityClasses.forEach(ec -> this.entityClasses.put(ec.name, ec));
        this.initialEntities.addAll(initialEntities);

        wc = new WorldContainer( new View(GameUtils.VIEW_WIDTH, GameUtils.VIEW_HEIGHT) );
        wc.setGame(this);
    }

    private void setUp() {
        GraphicsModuleConfig gmc = new GraphicsModuleConfig();
        gmc.windowHeight = 0.5f;
        gmc.windowWidth = 0.5f;
        moduleConfigs.add(gmc);
    }

    public void init() {
        setUp();

        moduleConfigs.forEach(mc -> {
            EngineModule module = null;
            try {
                module = mc.moduleType.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                System.exit(-1);
            }
            modulesByName.put(mc.moduleType, module);

            //init modules
            module.init(mc);
        });

        //        window = new Window(0.8f, 0.8f,"SIIII");
//        userInput = new UserInput(window, GameUtils.VIEW_WIDTH, GameUtils.VIEW_HEIGHT);

        Font.loadFonts(FontType.BROADWAY);
        //AudioMaster.init();


        //set for compatability reasons
        this.window = getModule(GraphicsModule.class).getWindow();
        this.userInput = getModule(GraphicsModule.class).getUserInput();

        //assign cmponents
        entityClasses.values().stream()
                .flatMap(ec -> ec.getComponentClasses().stream())
                .forEach(compClass -> wc.assignComponentType(compClass));

        //init systems
        systems.forEach(s -> {
            Sys sys;
            try {
                sys = s.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                return;
            }
            wc.addSystem(sys);
        });
        //addSystems(wc);

        //set entity class custom initializers
        EntityClass.addComponentLoader(ColoredMeshComp.class, new ColoredMeshCompInstAdapter());

        initialEntities.forEach(eConstr -> {
            EntityClass ec = entityClasses.get(eConstr.getEntityClassName());
            ec.instanciate(wc, eConstr);
        });

        System.out.println("HEELLLLLOOOOO");
        System.out.println(wc);


        ticker = new LinearTicker(FRAME_INTERVAL);
        ticker.setListener(this::update);
    }

    @SuppressWarnings("unchecked")
    public <T extends EngineModule> T getModule(Class<T> moduleType) {
        return (T)this.modulesByName.get(moduleType);
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
        wc.addSystem(new UserMovementInputSys());
        wc.addSystem(new MovementControlSys());
        wc.addSystem(new CollisionDetectionSys());
        wc.addSystem(new NaturalResolutionSys());

        wc.addSystem(new RenderSys());

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
    }


    protected void stopIfRequested() {
        GraphicsModule graphicsModule = getModule(GraphicsModule.class);
        if (graphicsModule.getWindow().shouldClosed() || graphicsModule.getUserInput().isKeyboardPressed(UserInput.KEY_ESCAPE))
            stop();
    }
    protected void pollEvents() {
        getModule(GraphicsModule.class).getWindow().pollEvents();
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
