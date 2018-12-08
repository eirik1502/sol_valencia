package game.load_attributes.images_attributes;

import game.load_attributes.ImageAttribs;
import game.load_attributes.ImagesAttribs;

/**
 * Created by eirik on 06.12.2018.
 */
public class GameImageAttribs extends ImagesAttribs{

    public GameImageAttribs() {
        //characters
        put("charMagnetRed", "magnet.png", 386, 258, 0.4f);
        put("charMagnetBlue", "masai_blue.png", 386, 258, 0.4f);

        //projectiles
        put("charMagnetSpear", "magnet_spear.png", 0, 0, 0.66f);
        put("charMagnetLion", "masai_lion.png", 0, 0, 0.66f);
    }


    private void put(String imageName, String filename_, float centerX_, float centerY_, float scale_) {
        ImageAttribs ia = new ImageAttribs() {
            {
                this.filename = filename_;
                this.centerX = centerX_;
                this.centerY = centerY_;
                this.scale = scale_;
            }
        };
        imagesAttribs.put(imageName, ia);
    }
}
