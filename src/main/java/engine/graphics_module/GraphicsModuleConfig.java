package engine.graphics_module;

import engine.EngineModuleConfig;

public class GraphicsModuleConfig extends EngineModuleConfig {

    public float windowWidth = 0.5f;
    public float windowHeight = 0.5f;

    public GraphicsModuleConfig() {
        super.moduleType = GraphicsModule.class;
    }
}
