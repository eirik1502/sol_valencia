package tryingStuff;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by eirik on 03.12.2018.
 */
public class UrlResources {

    public static void main(String[] args) {
        String location = "file:///C:/Users/eirik/OneDrive%20-%20NTNU/code_projects/sol_valencia/sol_editor/sol_editor/public/entityClasses.json";

        URI uri = null;
        URL url = null;
        InputStream inStream = null;
        try {
            uri = new URI(location);

            url = uri.toURL();

            inStream = url.openStream();

        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Scanner scanner = new Scanner(inStream);

        while(scanner.hasNextLine()) {
            System.out.println(scanner.nextLine());
        }

    }
}
