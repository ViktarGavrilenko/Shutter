package utils;

import aquality.selenium.core.logging.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import static aquality.selenium.browser.AqualityServices.getBrowser;
import static com.google.appengine.api.images.Image.Format.PNG;

public class BrowserUtils {
    private final static String READ_WRITE_EXCEPT = "File read or write exception";

    public static void createScreenshot(String pathScreen) {
        byte[] bytearray = getBrowser().getScreenshot();
        try {
            BufferedImage screen = ImageIO.read(new ByteArrayInputStream(bytearray));
            ImageIO.write(screen, PNG.name(), new File(pathScreen));
        } catch (IOException ioException) {
            Logger.getInstance().error(READ_WRITE_EXCEPT + ioException);
        }
    }
}
