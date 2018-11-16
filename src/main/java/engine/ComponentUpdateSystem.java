package engine;

import engine.physics.Circle;
import engine.physics.Shape;

/**
 * Created by haraldvinje on 14-Jun-17.
 */
@Deprecated
public class ComponentUpdateSystem{

//    private WorldContainer worldContainer;
//
//
//    public ComponentUpdateSystem(WorldContainer wc){
//        this.worldContainer = wc;
//    }
//
//    public void setWorldContainer(WorldContainer wc){
//        this.worldContainer = wc;
//    }
//
//    public void updateComponents() {
//        connectShapesToEntities();
//    }
//
//    private void connectShapesToEntities(){
//        for(int entity : worldContainer.getCollisionComps().keySet()){
//            Shape shape = worldContainer.getCollisionComponent(entity).getShape();
//            if (shape instanceof Circle){
//                connectCircleToEntity(entity, (Circle) shape);
//            }
//            //add for rectangles as well later..
//        }
//    }
//
//    private void connectCircleToEntity(int entity, Circle circle){
//        ((Circle) worldContainer.getCollisionComponent(entity).getShape()).setCx(worldContainer.getPositionComponent(entity).getX());
//        ((Circle) worldContainer.getCollisionComponent(entity).getShape()).setCy(worldContainer.getPositionComponent(entity).getY());
//    }
}
