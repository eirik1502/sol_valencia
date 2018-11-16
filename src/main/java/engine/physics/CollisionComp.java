package engine.physics;

import engine.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by haraldvinje on 13-Jun-17.
 */
public class CollisionComp implements Component, Iterable<CollisionData> {

    private Shape shape;
/*
    private List<Integer> collidingEntities = new ArrayList<Integer>();
*/
    private List<CollisionData> primaryCollisionDataList = new ArrayList<CollisionData>(5);
    private List<CollisionData> secondaryCollisionDataList = new ArrayList<CollisionData>(5);



    public CollisionComp(Shape s) {
        this.shape = s;
    }

    public Shape getShape() {
        return shape;
    }

/*
    public void addCollisionData(CollisionComp other){
        collisionDataList.add(new CollisionData(this, null));
    }
*/

    public void addPrimaryCollisionData(CollisionData collisionData){
        primaryCollisionDataList.add(collisionData);
    }

    public void addSecondaryCollisionData(CollisionData collisionData){
        secondaryCollisionDataList.add(collisionData);
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

/*
    public void addCollidingEntity(int otherEntity){
        collidingEntities.add(otherEntity);
    }
*/

    public void reset(){
/*
        collidingEntities.clear();
*/
        primaryCollisionDataList.clear();
        secondaryCollisionDataList.clear();
    }

    public List<CollisionData> getPrimaryCollisionDataList() {
        return primaryCollisionDataList;
    }

    public List<CollisionData> getSecondaryCollisionDataList() {
        return secondaryCollisionDataList;
    }


    @Override
    public Iterator<CollisionData> iterator() {
        return new CollisionCompIterator(primaryCollisionDataList, secondaryCollisionDataList);
    }

    public CollisionCompIterator collisionCompIterator() {
        return new CollisionCompIterator(primaryCollisionDataList, secondaryCollisionDataList);
    }
}
