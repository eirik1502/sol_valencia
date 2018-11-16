package engine.graphics.text;

import engine.Component;
import utils.maths.Vec2;
import utils.maths.Vec4;

/**
 * Created by eirik on 05.07.2017.
 */
public class TextMeshComp implements Component {

    private TextMesh textMesh;

//    private float viewX, viewY;
//    private float size;
//    private Vec4 color;


    public TextMeshComp(TextMesh textMesh, float size, float viewX, float viewY, Vec4 color) {
        this.textMesh = textMesh;
//        this.size = size;
//        this.viewX = viewX;
//        this.viewY = viewY;
//        this.color = color;
    }
    public TextMeshComp(TextMesh textMesh) {
        this(textMesh, textMesh.getFont().getFontSize(), 0, 0, new Vec4(1, 1, 1, 1));
    }
    public TextMeshComp() {
        this(new TextMesh("", Font.getFont(FontType.BROADWAY)) );
    }


    public TextMesh getTextMesh() {
        return textMesh;
    }

    public void setTextMesh(TextMesh textMesh) {
        this.textMesh = textMesh;
    }


//    public float getViewX() {
//        return viewX;
//    }
//
//    public void setViewX(float viewX) {
//        this.viewX = viewX;
//    }
//
//    public float getViewY() {
//        return viewY;
//    }
//
//    public void setViewY(float viewY) {
//        this.viewY = viewY;
//    }
//
//    public Vec2 getViewPos() {
//        return new Vec2(getViewX(), getViewY());
//    }
//
//    public float getSize() {
//        return size;
//    }
//    public void setSize(float size) {
//        this.size = size;
//    }
//
//    public Vec4 getColor() {
//        return color;
//    }
//    public void setColor(Vec4 color) {
//        this.color = color;
//    }
}
