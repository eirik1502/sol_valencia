import engine.PositionComp;
import engine.physics.*;
import javafx.geometry.Pos;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.css.Rect;

/**
 * Created by haraldvinje on 14-Jun-17.
 */
public class CollisionDetectionSysTest {

    @Test
    public void testDetectCollision(){
/*      Circle c1 = new Circle(100,100,10);
        Circle c2 = new Circle(110, 100, 7);
        Circle c3 = new Circle(130, 100, 10);
        Circle c4 = new Circle(100,90,8);
        Circle c5 = new Circle(100, 90, 7);*/

        CollisionDetectionSys cds = new CollisionDetectionSys();

        CollisionData data = new CollisionData(0, 0 );


        Circle c1 = new Circle(10);
        PositionComp positionComp1 = new PositionComp(100,100);
        CollisionComp collisionComp1 = new CollisionComp(c1);

        Circle c2 = new Circle(7);
        PositionComp positionComp2 = new PositionComp(110,100);
        CollisionComp collisionComp2 = new CollisionComp(c2);

        Circle c3 = new Circle(7);
        PositionComp positionComp3 = new PositionComp(110,100);
        CollisionComp collisionComp3 = new CollisionComp(c3);

        Circle c4 = new Circle(7);
        PositionComp positionComp4 = new PositionComp(110,100);
        CollisionComp collisionComp4 = new CollisionComp(c4);

        Circle c5 = new Circle(7);
        PositionComp positionComp5 = new PositionComp(130,100);
        CollisionComp collisionComp5 = new CollisionComp(c5);


        Rectangle r6 = new Rectangle(10,10);
        PositionComp positionComp6 = new PositionComp(100, 100);
        CollisionComp collisionComp6 = new CollisionComp(r6);

        Rectangle r7 = new Rectangle(10,15);
        PositionComp positionComp7 = new PositionComp(109, 100);
        CollisionComp collisionComp7 = new CollisionComp(r7);

        Rectangle r8 = new Rectangle(5, 5);
        PositionComp positionComp8 = new PositionComp(50, 100);
        CollisionComp collisionComp8 = new CollisionComp(r8);



        //Circle c1 and c2
        Assert.assertTrue(cds.detectCollision(data, collisionComp1, positionComp1, collisionComp2, positionComp2));

        //Circle c2 and c4
        Assert.assertTrue(cds.detectCollision(data, collisionComp2, positionComp2, collisionComp4, positionComp4));

        //Circle c2 and c5
        Assert.assertFalse(cds.detectCollision(data, collisionComp2, positionComp2, collisionComp5, positionComp5));

        //Rectangle r6 and r7
        Assert.assertTrue(cds.detectCollision(data, collisionComp6, positionComp6, collisionComp7, positionComp7));

        //Rectangle r6 and r8
        Assert.assertFalse(cds.detectCollision(data, collisionComp6, positionComp6, collisionComp8, positionComp8));

        //Rectangle r6 and circle c2
        Assert.assertTrue(cds.detectCollision(data, collisionComp6, positionComp6, collisionComp2, positionComp2));

        //Circle c1 and rectangle r8
        Assert.assertFalse(cds.detectCollision(data, collisionComp1, positionComp1, collisionComp8, positionComp8));


        //write more tests when we have rectangles as well
    }
}
