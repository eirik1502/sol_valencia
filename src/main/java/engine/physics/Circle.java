package engine.physics;

/**
 * Created by haraldvinje on 13-Jun-17.
 */
public class Circle extends Shape {

    private float radius;

    public Circle(){
    }

    public Circle(float radius){
        this.radius = radius;
    }

//    public Circle(float cx, float cy, float radius){
//        this.radius = radius;
//    }
//
//    public void setValues(float cx, float cy, float radius){
//        this.radius = radius;
//    }

//    public float getCx() {
//        return cx;
//    }
//
//    public void setCx(float cx) {
//        this.cx = cx;
//    }
//
//    public float getCy() {
//        return cy;
//    }
//
//    public void setCy(float cy) {
//        this.cy = cy;
//    }

    public void setRadius(float radius){
        this.radius = radius;
    }

    public float getRadius(){
        return this.radius;
    }
}
