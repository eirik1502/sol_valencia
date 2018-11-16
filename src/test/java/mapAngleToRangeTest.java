import org.junit.Assert;
import org.junit.Test;
import utils.maths.M;
import utils.maths.TrigUtils;

/**
 * Created by eirik on 28.06.2017.
 */
public class mapAngleToRangeTest {

    @Test
    public void test() {
        Assert.assertEquals(TrigUtils.mapAngleToRange(0.1345f), 0.1345f, 0.01f);

        Assert.assertEquals(TrigUtils.mapAngleToRange(-0.1345f), -0.1345f, 0.01f);

        Assert.assertEquals(TrigUtils.mapAngleToRange(-M.PI-0.1345f), M.PI - 0.1345f, 0.01f);

        Assert.assertEquals(TrigUtils.mapAngleToRange(M.PI+ 2.56f), -M.PI +2.56f, 0.01f);

        Assert.assertEquals(TrigUtils.mapAngleToRange(-2*M.PI), 0, 0.0001f);

    }
}
