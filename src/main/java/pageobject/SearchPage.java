package pageobject;

import aquality.selenium.browser.AqualityServices;
import aquality.selenium.core.logging.Logger;
import aquality.selenium.elements.interfaces.ILink;
import aquality.selenium.forms.Form;
import org.openqa.selenium.By;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static aquality.selenium.browser.AqualityServices.getBrowser;
import static aquality.selenium.elements.ElementType.LINK;
import static com.google.appengine.api.images.Image.Format.PNG;
import static utils.StringUtils.getIdImageFromUrl;

public class SearchPage extends Form {
    private final static String IMAGE_NOT_FOUND = "Images on page not found";
    private final static String READ_WRITE_EXCEPT = "File read or write exception";

    private final List<ILink> images = getElementFactory().findElements(
            By.cssSelector("div[data-automation^='AssetGrids'] a[class^='jss']"), "Links Image", LINK);

    public SearchPage() {
        super(By.cssSelector("div[data-automation^='AssetGrids'] a[class^='jss']"), "Page Search");
    }

    public Map<String, String> getMapImages(String pathScreen) {
        Map<String, String> listImages = new LinkedHashMap<>();
        try {
            AqualityServices.getConditionalWait().waitForTrue(() -> (images.size() > 0));
        } catch (TimeoutException e) {
            byte[] bytearray = getBrowser().getScreenshot();
            BufferedImage screen;
            try {
                screen = ImageIO.read(new ByteArrayInputStream(bytearray));
                ImageIO.write(screen, PNG.name(), new File(pathScreen));
            } catch (IOException ioException) {
                Logger.getInstance().error(READ_WRITE_EXCEPT + ioException);
            }
            Logger.getInstance().error(IMAGE_NOT_FOUND);
            return listImages;
        }
        for (ILink image : images) {
            String link = image.getHref();
            String idImage = getIdImageFromUrl(link);
            listImages.put(idImage, link);
        }
        return listImages;
    }
}