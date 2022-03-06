package pageobject;

import aquality.selenium.core.logging.Logger;
import aquality.selenium.elements.interfaces.ILink;
import aquality.selenium.forms.Form;
import org.openqa.selenium.By;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static aquality.selenium.core.elements.ElementsCount.MORE_THEN_ZERO;
import static aquality.selenium.elements.ElementType.LINK;
import static utils.BrowserUtils.createScreenshot;
import static utils.StringUtils.getIdImageFromUrl;

public class SearchPage extends Form {
    private final static String IMAGE_NOT_FOUND = "Images on page not found ";

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
            createScreenshot(pathScreen);
            Logger.getInstance().error(IMAGE_NOT_FOUND);
        }
        return listImages;
    }
}