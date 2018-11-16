package engine.graphics.text;

/**
 * Created by eirik on 03.07.2017.
 */
public class CharData {

    private CharMeshData meshData;

    private int xoffset, yoffset, xadvance;


    public CharData(CharMeshData meshData, int xadvance) {
        this.meshData = meshData;
//        this.xoffset = xoffset;
//        this.yoffset = yoffset;
        this.xadvance = xadvance;
    }


    public CharMeshData getMeshData() {
        return meshData;
    }
//    public int getXoffset() {
//        return xoffset;
//    }
//    public int getYoffset() {
//        return yoffset;
//    }

    public int getXadvance() {
        return xadvance;
    }

    public String toString() {
        return "[CharData: meshData="+meshData+" xoffset="+xoffset+" yoffset="+yoffset+" xadvvance="+xadvance+"]";
    }
}
