package ui;

import javafx.scene.image.Image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Images {

    private static final String RESOURCES_PATH = String.format("resources%simg%s", File.separator, File.separator);

    public static Image BACK;

    static {
        BACK = readImage("back.png");
    }

    private static Image readImage(String filename)
    {
        try {
            FileInputStream stream = new FileInputStream(String.format("%s%s", RESOURCES_PATH, filename));
            return new Image(stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

}
