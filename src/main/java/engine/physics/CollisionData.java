package engine.physics;

import engine.PositionComp;
import engine.WorldContainer;
import javafx.geometry.Pos;
import utils.maths.Vec2;

/**
 * Created by haraldvinje on 14-Jun-17.
 */
public class CollisionData {


    private int entity1, entity2;


    private Vec2 collisionVector;
    private float penetrationDepth;

    private boolean active = true;



    public CollisionData(int e1, int e2){
        this.entity1 = e1;
        this.entity2 = e2;


    }

    public int getEntity1() {
        return entity1;
    }
    public int getEntity2() {
        return entity2;
    }

    public void setActive(boolean flag){
        this.active = flag;
    }

    public boolean isActive(){
        return active;
    }


    public void swapEntities(){
        int nentity1 = this.entity2;

        this.entity2 = this.entity1;

        this.entity1 = nentity1;
    }





    public void setCollisionVector(Vec2 vector){this.collisionVector = vector; }

    public void setPenetrationDepth(float penetrationDepth) {this.penetrationDepth = penetrationDepth; }

    public Vec2 getCollisionVector() {return collisionVector; }

    public float getPenetrationDepth() {return penetrationDepth;
    }

    public void reverseCollisionVector() {
        collisionVector = collisionVector.negative();
    }


    @Override
    public String toString() {
        return "[Collision Data: entity1="+entity1+" entity2="+entity2 + " active="+active +"]";
    }

}
