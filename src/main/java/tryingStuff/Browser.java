package tryingStuff;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by eirik on 27.11.2018.
 */
public class Browser {

    public static void main(String[] args) {
        try {
            Desktop.getDesktop().browse(new URL("http://localhost:3000/?gameat=http://localhost:3001").toURI());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
