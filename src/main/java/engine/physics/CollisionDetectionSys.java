package engine.physics;

import engine.PositionComp;
import engine.Sys;
import engine.WorldContainer;
import utils.maths.M;
import utils.maths.Vec2;

import java.util.Set;

/**
 * Created by haraldvinje on 13-Jun-17.
 */
public class CollisionDetectionSys implements Sys {


/*
    private Integer[] collisionEntitiesArray;
*/

    private WorldContainer worldContainer;

    public CollisionDetectionSys(){}


    @Override
    public void setWorldContainer(WorldContainer wc) {
        this.worldContainer = wc;
    }


    public void update(){


        Integer[] collisionEntities = createCollisionEntitiesArray();
        //System.out.println(Arrays.toString(cea));
        int length = collisionEntities.length;


        //reseting all collision entities. Need to reset before to avoid resetting from secondary list in collComp.
        for (int i = 0; i<length; i++) {
            int entity1 = collisionEntities[i];
            CollisionComp collComp1 = (CollisionComp) worldContainer.getComponent(entity1, CollisionComp.class);
            collComp1.reset();
        }

        for (int i = 0; i<length; i++){
            int entity1 = collisionEntities[i];

            CollisionComp collComp1 = (CollisionComp)worldContainer.getComponent(entity1, CollisionComp.class);

            PositionComp posComp1 = (PositionComp)worldContainer.getComponent(entity1, PositionComp.class);

            for (int j = i+1; j<length; j++){
                int entity2 = collisionEntities[j];

                CollisionComp collComp2 = (CollisionComp)worldContainer.getComponent(entity2, CollisionComp.class);
                PositionComp posComp2 = (PositionComp)worldContainer.getComponent(entity2, PositionComp.class);

                CollisionData collData = new CollisionData(entity1, entity2);
              
                if (detectCollision(collData, collComp1, posComp1, collComp2, posComp2)) {
                  
                    collComp1.addPrimaryCollisionData(collData);
                    collComp2.addSecondaryCollisionData(collData);
                }
            }
        }
    }

    @Override
    public void terminate() {

    }

    private Integer[] createCollisionEntitiesArray(){
        Set keySet = worldContainer.getEntitiesWithComponentType(CollisionComp.class);
        int size = keySet.size();
        return (Integer[]) keySet.toArray(new Integer[size]);
    }


    public boolean detectCollision(CollisionData data, CollisionComp colComp1, PositionComp posComp1, CollisionComp colComp2, PositionComp posComp2){
        Shape shape1 = colComp1.getShape();
        Shape shape2 = colComp2.getShape();

        if (shape1 instanceof Circle && shape2 instanceof Circle){
            return detectCollisionCircCirc(data, colComp1, posComp1, colComp2, posComp2);
        }
        else if (shape1 instanceof Rectangle && shape2 instanceof Rectangle) {
            return detectCollisionRectRect(data, colComp1, posComp1, colComp2, posComp2);
        }
        else if (shape1 instanceof Rectangle && shape2 instanceof Circle) {
            return detectCollisionRectCirc(data, colComp1, posComp1, colComp2, posComp2);
        }
        else if (shape1 instanceof Circle && shape2 instanceof Rectangle) {
          
            data.swapEntities();
            boolean result = detectCollisionRectCirc(data, colComp2, posComp2, colComp1, posComp1);
            if (result) {
                data.swapEntities();
                data.reverseCollisionVector();
            }
            return result;
        }

        throw new IllegalArgumentException("trying to detect collision between nonsupported shapes");
    }

    private boolean detectCollisionCircCirc(CollisionData data, CollisionComp colComp1, PositionComp posComp1, CollisionComp colComp2, PositionComp posComp2){
        Circle circ1 = (Circle) colComp1.getShape();
        Circle circ2 = (Circle) colComp2.getShape();
        Vec2 pos1 = posComp1.getPos();
        Vec2 pos2 = posComp2.getPos();

        float r1 = circ1.getRadius();
        float r2 = circ2.getRadius();

        float maxDist = r1 + r2;
        float maxDistSquared = M.pow2(maxDist);

        Vec2 distVec = pos2.subtract(pos1);

        if (distVec.getLengthSquared() >= maxDistSquared) {
            return false;
        }

        float dist = distVec.getLength();

        if (dist != 0) {
            data.setPenetrationDepth( maxDist - dist );
            data.setCollisionVector( distVec.scale(1/dist) ); //optimized normalize
            return true;
        }

        //set default values if circles on same pos
        data.setPenetrationDepth(r1);
        data.setCollisionVector( new Vec2(1, 0) );
        return true;
    }

    private boolean detectCollisionRectRect(CollisionData data, CollisionComp colComp1, PositionComp posComp1, CollisionComp colComp2, PositionComp posComp2) {
        Rectangle rect1 = (Rectangle)colComp1.getShape();
        Rectangle rect2 = (Rectangle)colComp2.getShape();

        Vec2 pos1 = posComp1.getPos();
        Vec2 pos2 = posComp2.getPos();

        Vec2 distVec = pos2.subtract(pos1);

        // Calculate half extents along x axis for each object
        float rect1HExtentX = rect1.getWidth() / 2.0f;
        float rect2HExtentX = rect2.getWidth() / 2.0f;

        // Calculate overlap on x axis
        float xOverlap = rect1HExtentX + rect2HExtentX - M.abs( distVec.x );

        // SAT test on x axis
        if(xOverlap > 0)
        {
            // Calculate half extents along y axis for each object
            float rect1HExtentY = rect1.getHeight() / 2.0f;
            float rect2HExtentY = rect2.getHeight() / 2.0f;

            // Calculate overlap on y axis
            float yOverlap = rect1HExtentY + rect2HExtentY - M.abs( distVec.y );

            // SAT test on y axis
            if(yOverlap > 0)
            {
                // Find out which axis is axis of least penetration
                if(xOverlap < yOverlap) {
                    // Point towards B knowing that n points from A to B
                    if(distVec.x < 0)
                        data.setCollisionVector(new Vec2( -1, 0 ) );
                    else
                        data.setCollisionVector(new Vec2( 1, 0 ) );

                    data.setPenetrationDepth( xOverlap );
                    return true;
                }
                else {
                    // Point toward B knowing that n points from A to B
                    if(distVec.y < 0)
                        data.setCollisionVector( new Vec2( 0, -1 ) );
                    else
                        data.setCollisionVector( new Vec2( 0, 1 ) );

                    data.setPenetrationDepth( yOverlap );
                    return true;
                }
            }
        }

        return false;
    }

    private boolean detectCollisionRectCirc(CollisionData data, CollisionComp colComp1, PositionComp posComp1, CollisionComp colComp2, PositionComp posComp2) {
        Rectangle rect = (Rectangle)colComp1.getShape();
        Circle circ = (Circle)colComp2.getShape();
        Vec2 posRect = posComp1.getPos();
        Vec2 posCirc = posComp2.getPos();

        // Vector from A to B
        Vec2 distVec = posCirc.subtract(posRect);

        // Calculate half extents along each axis
        float rectHalfExtentX = rect.getWidth() / 2;
        float rectHalfExtentY = rect.getHeight() / 2;

        // Closest point on rect to center of circ
        Vec2 closestRectPoint = new Vec2(	M.clamp(distVec.x, -rectHalfExtentX, rectHalfExtentX),
                M.clamp(distVec.y, -rectHalfExtentY, rectHalfExtentY) );

        boolean circInsideRect = false;

        // Circle is inside the AABB, so we need to clamp the circle's center
        // to the closest edge
        if(distVec.equals(closestRectPoint)) {
            //if the clamping above did not make closestRectPoint different from distVec, circ center inside rect
            circInsideRect = true;

            // Find closest axis
            //if(M.abs( distVec.x ) > M.abs( distVec.y ) ) {
            if(M.abs( distVec.x/rect.getWidth() ) > M.abs( distVec.y/rect.getHeight() ) ) {
                //not entirely right. If rectangles width != height it might clamp to wrong axis.
                //  ---> fixed by dividing by width and height of rectangle
                // Clamp to rect edge in x direction
                if(closestRectPoint.x > 0)
                    closestRectPoint.x = rectHalfExtentX;
                else
                    closestRectPoint.x = -rectHalfExtentX;
            }
            else { //clamp to rect edge in y direction
                if(closestRectPoint.y > 0)
                    closestRectPoint.y = rectHalfExtentY;
                else
                    closestRectPoint.y = -rectHalfExtentY;
            }
        }

        Vec2 rectCircVec = distVec.subtract( closestRectPoint );
        float rectCircDistSquared = rectCircVec.getLengthSquared();
        float circRadius = circ.getRadius();

        // Can now determine if there is a collision
        if(rectCircDistSquared > M.pow2(circRadius) && !circInsideRect) {
            return false;
        }

        // Avoided sqrt if no collision is found
        float rectCircDist = M.sqrt(rectCircDistSquared);


        if (rectCircDist == 0) {
            return false;
        }

        Vec2 normal = rectCircVec.scale(1/rectCircDist);
        float penetration = circRadius - rectCircDist;

        // Flip normal if circ inside rect
        if(circInsideRect) {
            normal = normal.negative();
            //penetration = rectCircDist; <--- this seems right, but works better without
        }

        data.setCollisionVector( normal );
        data.setPenetrationDepth( penetration );
        return true;
    }

//    private void calculateCollisionData(CollisionData collisionData){
//        Shape s1 = ((CollisionComp)worldContainer.getComponent(collisionData.getEntity1(), CollisionComp.class)).getShape();
//        Shape s2 = ((CollisionComp)worldContainer.getComponent(collisionData.getEntity2(), CollisionComp.class)).getShape();
//
//        if (s1 instanceof Circle && s2 instanceof Circle){
//            calculateCollisionDataCircCirc((Circle) s1, (Circle) s2, collisionData);
//        }
//    }

//    private void calculateCollisionDataCircCirc(Circle c1, Circle c2, CollisionData collisionData){
//
//        Vec2 colVector = new Vec2(c1.getX()-c2.getX(), c1.getY()-c2.getY() );
//        float maxDist = c1.getRadius() + c2.getRadius();
//        float dist = colVector.getLength();
//
//        collisionData.setCollisionVector(colVector.scale(1/dist));
//        collisionData.setPenetrationDepth(maxDist-dist);
//    }
}
