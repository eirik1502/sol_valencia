package engine.physics;

import engine.Component;

import java.util.Iterator;
import java.util.List;

/**
 * Created by eirik on 20.06.2017.
 */
public class CollisionCompIterator implements Iterator<CollisionData> {

    private Iterator<CollisionData> primaryIterator, secondaryIterator;
    private boolean primaryFinished = false;

    private CollisionData currentData;


    public CollisionCompIterator(List<CollisionData> primaryList, List<CollisionData> secondaryList) {
        this.primaryIterator = primaryList.iterator();
        this.secondaryIterator = secondaryList.iterator();
    }


    @Override
    public boolean hasNext() {
        if (!primaryFinished) {
            if (primaryIterator.hasNext()) {
                return true;
            }
            else {
                primaryFinished = true;
            }
        }
        //this is checked even though primaryFinished is set right before
        if (primaryFinished) {
            return (secondaryIterator.hasNext());
        }
        throw new IllegalStateException("Implemented wrong, should not reach this point");
    }

    @Override
    public CollisionData next() {
        //System.out.println("CollisionIterator, primaryFinished: " + primaryFinished);
        if (!primaryFinished) {
            currentData = primaryIterator.next();
        }
        else {
            currentData = secondaryIterator.next();
        }

        return currentData;
    }

    @Override
    public void remove() {
        if (!primaryFinished) {
            primaryIterator.remove();
        }
        else {
            secondaryIterator.remove();
        }
    }

    public int getSelfEntity() {
        if (!primaryFinished) {
            return currentData.getEntity1();
        }
        else {
            return currentData.getEntity2();
        }
    }
    public int getOtherEntity() {
        if (!primaryFinished) {
            return currentData.getEntity2();
        }
        else {
            return currentData.getEntity1();
        }
    }

//    public boolean otherIsType(Class<? extends Component> compType) {
//        if (!primaryFinished) {
//            return currentData.getEntity2();
//        }
//        else {
//            return currentData.getEntity1();
//        }
//    }
}
