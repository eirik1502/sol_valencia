package engine.graphics;

import engine.Component;
import engine.graphics.text.TextMesh;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by eirik on 09.07.2017.
 */
public class ViewRenderComp implements Component{

    private List<TexturedMesh> textureMeshes = new ArrayList<>(1);
    private List<ColoredMesh> colorMeshes = new ArrayList<>(1);
    private List<TextMesh> textMeshes = new ArrayList<>(1);


    public ViewRenderComp() {

    }
    public ViewRenderComp(ColoredMesh mesh) {
        colorMeshes.add(mesh);
    }
    public ViewRenderComp(TexturedMesh mesh) {
        textureMeshes.add(mesh);
    }
    public ViewRenderComp(TextMesh mesh) {
        textMeshes.add(mesh);
    }


    public Stream<TexturedMesh> textureMeshesStream() {
        return textureMeshes.stream();
    }
    public Stream<ColoredMesh> colorMeshesStream() {
        return colorMeshes.stream();
    }
    public Stream<TextMesh> textMeshesStream() {
        return textMeshes.stream();
    }


    public ColoredMesh getColoredMesh(int index) {
        return colorMeshes.get(index);
    }
    public TexturedMesh getTextureMesh(int index) {
        return textureMeshes.get(index);
    }
    public TextMesh getTextMesh(int index) {
        return textMeshes.get(index);
    }

    public void addMesh(TextMesh mesh) {
        textMeshes.add(mesh);
    }
    public void addMesh(TexturedMesh mesh) {
        textureMeshes.add(mesh);
    }
    public void addMesh(ColoredMesh mesh) {
        colorMeshes.add(mesh);
    }
}
