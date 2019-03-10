package engine.graphics_module;

import engine.EngineModule;
import engine.EngineModuleConfig;
import engine.UserInput;
import engine.graphics.view_.View;
import engine.window.Window;
import game.GameUtils;

public class GraphicsModule extends EngineModule {

    private Window window;
    private UserInput userInput;
    private View view;

    @Override
    public void init(EngineModuleConfig generalConf) {
        GraphicsModuleConfig conf = (GraphicsModuleConfig) generalConf;

        window = new Window(conf.windowWidth, conf.windowHeight,"SIIII");
        userInput = new UserInput(window, GameUtils.VIEW_WIDTH, GameUtils.VIEW_HEIGHT);

        view = new View(window.getWidth(), window.getHeight());

    }

    @Override
    public void terminate() {
        window.close();
    }

    public Window getWindow() {
        return this.window;
    }
    public UserInput getUserInput() {
        return userInput;
    }

    public void render(String texture) {
        //lookup texture
        //get corresponding mesh
        //draw maesh with texture
    }
}
