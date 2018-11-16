package engine.graphics.text;

/**
 * Created by eirik on 03.07.2017.
 */
public enum FontType {
    BROADWAY("broadway.png", "broadway.fnt");

    private String textAtlasPath;
    private String fontFilePath;

    FontType(String textAtlasPath, String fontFilePath) {
        this.textAtlasPath = textAtlasPath;
        this.fontFilePath = fontFilePath;
    }

    public String getTextAtlasPath() {
        return textAtlasPath;
    }

    public String getFontFilePath() {
        return fontFilePath;
    }

    public static int size() {
        return size;
    }

    private static final int size = FontType.values().length;
}
