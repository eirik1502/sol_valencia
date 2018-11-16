import game.offline.Main;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by eirik on 12.06.2017.
 */
public class MainTest {

    @Test
    public void testMain() {

        Main m = new Main();
        Assert.assertEquals(m.toString(), "main");
    }
}
