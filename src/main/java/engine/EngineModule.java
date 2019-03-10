package engine;

public abstract class EngineModule {

    public abstract void init(EngineModuleConfig conf);
    public abstract void terminate();

}
