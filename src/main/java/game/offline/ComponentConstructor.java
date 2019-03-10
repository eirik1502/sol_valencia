package game.offline;

import engine.Component;

public interface ComponentConstructor<T extends Component> {
    void modifyInitially(T comp);
}
