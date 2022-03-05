package pageobject;

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

import static aquality.selenium.browser.AqualityServices.getBrowser;
import static aquality.selenium.core.elements.ElementsCount.MORE_THEN_ZERO;
import static aquality.selenium.elements.ElementType.LINK;
import static com.google.appengine.api.images.Image.Format.PNG;
import static utils.StringUtils.getIdImageFromUrl;

public class SearchPage extends Form {
    private final static String IMAGE_NOT_FOUND = "Images on page not found ";
    private final static String READ_WRITE_EXCEPT = "File read or write exception";

    private final List<ILink> images = getElementFactory().findElements(
            By.xpath("//div[contains(@data-automation, 'AssetGrids')]//a"), "Links Image", LINK, MORE_THEN_ZERO);

    public SearchPage() {
        super(By.xpath("//span[@class='MuiChip-label']"), "Page Search");
    }

    public Map<String, String> getMapImages(String pathScreen) {
        Map<String, String> listImages = new LinkedHashMap<>();
        Logger.getInstance().info("Number of images " + images.size());
        if (images.size() > 0) {
            for (ILink image : images) {
                String link = image.getHref();
                String idImage = getIdImageFromUrl(link);
                listImages.put(idImage, link);
            }
        } else {
            byte[] bytearray = getBrowser().getScreenshot();
            try {
                BufferedImage screen = ImageIO.read(new ByteArrayInputStream(bytearray));
                ImageIO.write(screen, PNG.name(), new File(pathScreen));
            } catch (IOException ioException) {
                Logger.getInstance().error(READ_WRITE_EXCEPT + ioException);
            }
            Logger.getInstance().error(IMAGE_NOT_FOUND);
        }
        return listImages;
    }
}