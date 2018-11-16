package engine;

/**
 * TODO: caontain a list of dependent components, and check if they exist in entities
 * Created by haraldvinje on 14-Jun-17.
 */
public interface Sys {

    public void setWorldContainer(WorldContainer wc);
    public void update();
    public void terminate();
}
