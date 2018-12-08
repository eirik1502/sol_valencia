package engine.graphics.text;

import engine.graphics.Texture;
import engine.graphics.TextureUtils;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

/**
 * Created by eirik on 03.07.2017.
 */
public class Font {

    private static EnumMap<FontType, Font> loadedFonts = new EnumMap<FontType, Font>(FontType.class);

    public static void loadFonts(FontType... fonts) {
        for (int i = 0; i < fonts.length; i++) {
            FontType fontType = fonts[i];
            Font font = FontUtils.loadFont(fontType.getFontFilePath(), fontType.getTextAtlasPath());

            loadedFonts.put(fontType, font);
        }
    }

    public static Font getFont(FontType type) {
        if (!loadedFonts.containsKey(type)) throw new IllegalStateException("Trying to access font "+type+" that is not loaded");
        return loadedFonts.get(type);
    }
    public static Font getDefaultFont() {
        return getFont(FontType.BROADWAY);
    }

//    public static void main(String[] args) {
//        loadFonts(FontType.BROADWAY);
//    }


    private final int lineHeight, fontSize;
    private HashMap<Character, CharData> charData;
    private Texture fontAtlas;


    Font(int lineHeight, int fontSize, HashMap<Character, CharData> charData, Texture fontAtlas) {
        this.charData = charData;
        this.fontAtlas = fontAtlas;
        this.lineHeight = lineHeight;
        this.fontSize = fontSize;

        //System.out.println(charData.keySet());
        //System.out.println("Line height="+lineHeight);
    }

    public Texture getFontAtlas() {
        return fontAtlas;
    }

    public CharData getCharData(char c) {
        return charData.get(c);
    }

    public boolean hasChar(char c) {
        return charData.containsKey(c);
    }

    public int getLineHeight() {
        return lineHeight;
    }

    public int getFontSize() {
        return fontSize;
    }
}
