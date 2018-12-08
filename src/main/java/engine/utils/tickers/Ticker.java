package engine.utils.tickers;

/**
 * Created by eirik on 26.11.2018.
 */
public interface Ticker {
    /**
     * Sets the listener t be called on tick
     * @param listener
     */
    void setListener(Tickable listener);

    /**
     * Starts the ticker. This call will block until stop is called
     */
    void start();

    /**
     * Stops the ticker on next instruction cycle.
     */
    void stop();
}
